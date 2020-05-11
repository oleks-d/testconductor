package edu.testconductor.controllers;

import edu.testconductor.domain.*;
import edu.testconductor.repos.*;
import edu.testconductor.services.EmailServiceImpl;

import edu.testconductor.services.MainPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class StudentController {

    private static Logger logger = LoggerFactory.getLogger(StudentController.class);

    @Autowired
    private UserRepo usersRepo;

    @Autowired
    private QuestionsRepo questionsRepo;

    @Autowired
    private ThemeRepo themeRepo;

    @Autowired
    private StudentSessionRepo sessionsRepo;

    @Autowired
    private ExamsRepo examsRepo;

    @Autowired
    private GroupsRepo groupsRepo;

    @Autowired
    private MainPageService mainPageService;

    @Autowired
    private EmailServiceImpl emailService;

    //TODO make custom
    int DEFAULT_NUMBER_OF_QUESTIONS = 10;

    String QUESTIONS_SEPARATOR = ";";

    @Value("${app.datetime.format}")
    private String dateTimeFormat;

    @PostMapping(value="/start_test")
    public ModelAndView prepareExaminationPage(@RequestParam Long examID, @RequestParam String userName, HttpServletRequest request) {

        String lang = (String) request.getSession().getAttribute("lang");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("examID", examID);

        Exam exam = examsRepo.findById(examID).get();
        User user = usersRepo.findByUsername(userName);

        LocalDateTime startTime = LocalDateTime.parse(exam.getStartDateTime(), DateTimeFormatter.ofPattern(dateTimeFormat));
        LocalDateTime endTime = LocalDateTime.parse(exam.getEndDateTime(), DateTimeFormatter.ofPattern(dateTimeFormat));
        if (LocalDateTime.now().isBefore(startTime)) {
            if (lang.equals("ENG"))
                params.put("warning", "Too early. Exam will start at " + exam.getStartDateTime());
            else
                params.put("warning", "Зарано. Заняття почнеться " + exam.getStartDateTime());
            return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
        }
        if (LocalDateTime.now().isAfter(endTime)) {
            if (lang.equals("ENG"))
                params.put("warning", "Exam already finished " + endTime);
            else
                params.put("warning", "Заняття закінчилось " + endTime);

            return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
        }

        StudentSession session = null;
        Iterable<StudentSession> sessions = sessionsRepo.findAllByEmail(user.getEmail());
        for (StudentSession existingSession : sessions) {
            if (existingSession.getExam().getId().equals(examID)) { // was already generated
                logger.warn("Test was already processed : " + user.getName() + " " + user.getEmail());

                LocalDateTime expectedEndOfTest = LocalDateTime.parse(existingSession.getTime(), DateTimeFormatter.ofPattern(dateTimeFormat)).plusSeconds(exam.getTimeForCompletionInMinutes()*60);
                LocalDateTime currentTime = LocalDateTime.now();

                if(currentTime.isAfter(expectedEndOfTest)) {
                    if(existingSession.getEndTime() == null) {
                        existingSession.setEndTime(expectedEndOfTest.format(DateTimeFormatter.ofPattern(dateTimeFormat)));
                    }
                    if(existingSession.getResult() == -1) {
                        existingSession.setResult(0);
                    }
                    sessionsRepo.save(existingSession);

                    if (lang.equals("ENG")) {
                        params.put("warning", "Time for test finished \n " );
                    } else {
                        params.put("warning", "Час на виконання скінчився \n " );
                    }
                    logger.warn("Time finished " + existingSession.getName() + " " + existingSession.getExam().getId());
                    return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
                }

                if (existingSession.getEndTime() == null) {
                    session = existingSession;
                    break;
                } else {
                    if (lang.equals("ENG")) {
                        params.put("warning", "Test was already processed: \n " + existingSession.getEmail() + "\n" + existingSession.getName());
                    } else {
                        params.put("warning", "Тест вже був оброблений: \n " + existingSession.getEmail() + "\n" + existingSession.getName());
                    }
                    logger.warn("Second attempt (1) " + existingSession.getName() + " " + existingSession.getExam().getId());
                    return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
                }
            }
        }

        List<Question> questions;
        int timeout = exam.getTimeForCompletionInMinutes() * 60;
        if (session == null){
            session = new StudentSession(user.getName(), user.getEmail(), user.getGroupName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)), exam);

            String code = generateCodeForStudent(user.getName(), examID);
            session.setCode(code);

            questions = generateQuestionsFor(exam);

            String allQuestionIDs = questions.stream().map(q -> String.valueOf(q.getId())).collect(Collectors.joining(QUESTIONS_SEPARATOR));

            session.setResultString(allQuestionIDs);

            sessionsRepo.save(session);
            logger.info("Session started " + session.getName() + " " + session.getExam().getId());
        } else {
            questions = generateQuestionsFromResultOfExistingSession(session.getResultString());
            if(questions == null){ // if no results string
                questions = generateQuestionsFor(exam);
            }

            LocalDateTime expectedEndOfTest = LocalDateTime.parse(session.getTime(), DateTimeFormatter.ofPattern(dateTimeFormat)).plusSeconds(exam.getTimeForCompletionInMinutes()*60);
            LocalDateTime currentTime = LocalDateTime.now();
            Duration diff =  Duration.between(currentTime, expectedEndOfTest);

            timeout = (int) diff.getSeconds();

            logger.warn("Session restored " + session.getName() + " " + session.getExam().getId());
        }

        params.put("timeout", timeout);
        params.put("studentCode", session.getCode());
        params.put("questions", questions);
        return new ModelAndView("examinationTest", params);
    }


    @PostMapping(value="/test")
    public ModelAndView showExaminationPage(@RequestParam Map<String,String> allAnswers, HttpServletRequest request, RedirectAttributes redirectAttr) {
        String lang = (String)request.getSession().getAttribute("lang");
        HashMap<String, Object> params = new HashMap<String, Object>();
        String resultString = "";
        int mark = 0;
        for(String questionText : allAnswers.keySet()){
            if(questionText.equals("studentCode"))
                continue;
            if(questionText.equals("_csrf"))
                continue;
            Question question = questionsRepo.findAllByText(questionText).get(0);
            resultString = resultString + "\n\n" + questionText + " \n Answer: " + allAnswers.get(questionText) + "\nExpected: " + question.getCorrectAnswer();
            if(makeComparable(question.getCorrectAnswer()).equals( makeComparable(allAnswers.get(questionText)))){
                mark++;
            }
        }
        StudentSession session = sessionsRepo.findByCode(allAnswers.get("studentCode"));

        if(session.getResult() > -1){
            if(lang.equals("ENG")) {
                params.put("warning", "Test was already processed: \n " + session.getResult() + " "  + session.getEndTime());
            } else {
                params.put("warning", "Тест вже був оцінений: \n " + session.getResult() + " " + session.getEndTime());
            }
            logger.warn("Second attempt " + session.getName() + " " + session.getExam().getId());
            return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
        } else { // check if timeout was reached
            LocalDateTime startTime = LocalDateTime.parse(session.getTime(), DateTimeFormatter.ofPattern(dateTimeFormat));
            LocalDateTime endTime = LocalDateTime.now();
            int timeForCompletion = session.getExam().getTimeForCompletionInMinutes();
            Duration diff =  Duration.between(startTime, endTime);
            if( diff.toMinutes() >  timeForCompletion) {
                resultString = "TIMEOUT\n\n" + resultString;
                logger.warn("Timeout " + session.getName() + " " + session.getExam().getId());
            }
        }

        session.setResult(mark);
        session.setResultString(resultString);
        session.setEndTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat )));
        sessionsRepo.save(session);

        logger.info("Test finished " + session.getName() + " " + session.getExam().getId());

        Exam exam = examsRepo.findById(session.getExam().getId()).get();

        String message = "Student:  " + session.getName() + "\n" +
                "Group  :  " + session.getGroupName() + "\n" +
                "Theme  :  " + exam.getExamName() + " - " + exam.getTheme() + "\n" +
                "Start time:  " + session.getTime() + "\n" +
                "End time  :  " + session.getEndTime() + "\n" +
                "Result :  " + mark + "/" + exam.getNumberOfQuestions()  + "\n" +
                "Thank you";

        //emailService.sendSimpleMessage(session.getEmail(), "Result of test: " + exam.getExamName()+ " - " + exam.getTheme(), message);
        redirectAttr.addFlashAttribute("message",  "Your result is : " + mark + "/" + exam.getNumberOfQuestions());
        //params.put("message", "Your result is : " + mark + "/" + DEFAULT_NUMBER_OF_QUESTIONS);
        return new ModelAndView("redirect:/");
    }

    private String makeComparable(String s) {
        return s.toLowerCase().replaceAll("[^a-zA-Z0-9а-яА-Я]","");
    }

    private List<Question> generateQuestionsFromResultOfExistingSession(String resultString) {
        ArrayList<Question> results = new ArrayList<Question>();
        if(resultString == null || resultString.equals(""))
            return null;
        for(String id : resultString.split(QUESTIONS_SEPARATOR))
            results.add(questionsRepo.getOne(Long.valueOf(id)));
        return results;
    }

    ArrayList<Question> generateQuestionsFor(Exam exam){

        //TODO : refactor before showing to real people
        String UKR_LETTER1 = "а";
        String UKR_LETTER2 = "о";
        String UKR_LETTER3 = "у";

        int MAX_NUM_OF_QUESTIONS = exam.getNumberOfQuestions();

        ArrayList<Question> results = new ArrayList<Question>();
        List<Question> allQuestionsForCourse = null;
        if(exam.getTheme().equals("FINAL_ENG")){
            ArrayList<Theme> allThemes = (ArrayList<Theme>) themeRepo.findAllByOrderByNameAsc();

            for(Theme theme : allThemes) {
                if (!theme.getName().contains(UKR_LETTER1) && !theme.getName().contains(UKR_LETTER2) && !theme.getName().contains(UKR_LETTER3)) {
                    allQuestionsForCourse = (ArrayList<Question>) questionsRepo.findAllByTheme(theme.getName());

                    int MAX_NUM_OF_QUESTIONS_PER_THEME = results.size() + 1; // one question per theme
                    Random random = new Random();
                    while (results.size() < MAX_NUM_OF_QUESTIONS_PER_THEME) {
                        Question candidateItem = allQuestionsForCourse.get(random.nextInt(allQuestionsForCourse.size()-1));
                        if (!results.contains(candidateItem))
                            results.add(candidateItem);
                    }
                }
            }
        } else if(exam.getTheme().equals("FINAL_UKR")){

            ArrayList<Theme> allThemes = (ArrayList<Theme>) themeRepo.findAllByOrderByNameAsc();

            for(Theme theme : allThemes) {
                if (theme.getName().contains(UKR_LETTER1) || theme.getName().contains(UKR_LETTER2) || theme.getName().contains(UKR_LETTER3)) {
                    allQuestionsForCourse = (ArrayList<Question>) questionsRepo.findAllByTheme(theme.getName());

                    int MAX_NUM_OF_QUESTIONS_PER_THEME = results.size() + 1; // one question per theme
                    Random random = new Random();
                    while (results.size() < MAX_NUM_OF_QUESTIONS_PER_THEME) {
                        Question candidateItem = allQuestionsForCourse.get(random.nextInt(allQuestionsForCourse.size()-1));
                        if (!results.contains(candidateItem))
                            results.add(candidateItem);
                    }
                }
            }

        }  else {
            allQuestionsForCourse = questionsRepo.findAllByTheme(exam.getTheme());

            Random random = new Random();
            while (results.size() < MAX_NUM_OF_QUESTIONS) {
                Question candidateItem = allQuestionsForCourse.get(random.nextInt(allQuestionsForCourse.size()-1));
                if (!results.contains(candidateItem))
                    results.add(candidateItem);
            }
        }
        return results;
    }

    private String generateCodeForStudent(String studentName, Long theme) {
        String code = Base64.getEncoder().encodeToString(studentName.getBytes()) + Base64.getEncoder().encodeToString(theme.toString().getBytes()) + Base64.getEncoder().encodeToString(LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)).getBytes());
        return code.replace("+", "");
    }


}
