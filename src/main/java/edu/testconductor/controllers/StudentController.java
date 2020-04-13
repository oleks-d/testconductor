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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    @Value("${app.datetime.format}")
    private String dateTimeFormat;

    @GetMapping(value="/register_for_test")
    public ModelAndView showRegistrationStudentPage(@RequestParam("examID") Optional<String> examID) {
        Iterable<Exam> exams = examsRepo.findAll();
        Iterable<StudentGroup> groups = groupsRepo.findAllByOrderByGroupNameAsc();

        HashMap<String, Object> params = new HashMap<String, Object>();
        if( examID.isPresent()) {
            Exam results = examsRepo.findById(Long.parseLong(examID.get())).get();
            params.put("selectedExam", examID.get());
            params.put("selectedExamLabel", results.getExamName() + " " + results.getTheme()+ " " + results.getStartDateTime());

        }

        params.put("groups", groups);
        params.put("exams", exams);
        return new ModelAndView("examinationRegistration", params);

    }

//    @PostMapping(value="/register_for_test")
//    public ModelAndView generateCodeForTest(@RequestParam String studentName, @RequestParam String studentEmail, @RequestParam String groupName, @RequestParam Long examID) {
//        Iterable<StudentGroup> groups = groupsRepo.findAll();
//        Iterable<Exam> exams = examsRepo.findAll();
//        Exam exam = examsRepo.findById(examID).get();
//
//        HashMap<String, Object> params = new HashMap<String, Object>();
//        Iterable<StudentSession> sessions = sessionsRepo.findAllByEmail(studentEmail);
//        for(StudentSession session : sessions){
//            if (session.getExam().equals(examID)) { // was already generated
//                logger.info("Already generated : " + studentName + " " + studentEmail);
//                params.put("exams", exams);
//                params.put("group", groups);
//                params.put("selectedExam", examID);
//                params.put("selectedExamLabel", exam.getExamName() + " " + exam.getTheme()+ " " + exam.getStartDateTime());
//                params.put("warning", "Code was already generated for this email for: \n " + session.getEmail() + "\n" + session.getName());
//                return new ModelAndView("examinationRegistration", params);
//            }
//        }
//
//        LocalDateTime startTime = LocalDateTime.parse(exam.getStartDateTime(), DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT));
//        LocalDateTime endTime = LocalDateTime.parse(exam.getEndDateTime(), DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT));
//        if (LocalDateTime.now().isBefore(startTime)){
//            logger.info("Too early for : " + studentName + " " + studentEmail);
//            params.put("exams", exams);
//            params.put("group", groups);
//            params.put("selectedExam", examID);
//            params.put("selectedExamLabel", exam.getExamName() + " " + exam.getTheme()+ " " + exam.getStartDateTime());
//            params.put("warning", "Too early. Exam will start at " + exam.getStartDateTime());
//            return new ModelAndView("examinationRegistration", params);
//        }
//        if (LocalDateTime.now().isAfter(endTime)){
//            logger.info("Too late for : " + studentName + " " + studentEmail);
//            params.put("exams", exams);
//            params.put("group", groups);
//            params.put("selectedExam", exam.getId());
//            params.put("selectedExamLabel", exam.getExamName() + " " + exam.getTheme()+ " " + exam.getStartDateTime());
//            params.put("warning", "Exam already finished " + endTime);
//            return new ModelAndView("examinationRegistration", params);
//        }
//
//        logger.info("Session was created for : " + studentName + " " + studentEmail);
//        // save session and code
//        StudentSession session = new StudentSession(studentName, studentEmail, groupName, LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)), examID);
//
//                String code = generateCodeForStudent(session, studentName,  examID);
//
//        String message = "Please use following code to start test:\n" + code +
//                "\n\nExam: " + exam.getExamName() + " - " + exam.getTheme() + "\n" +
//                //+ getURLforStartTest(code) + "\n" +
//                "Start time: " + exam.getStartDateTime() + "\n" +
//                "End time: " + exam.getEndDateTime() + " \n" +
//                "Thank you";
//
//        logger.info("Sending message for : " + studentName + " " + studentEmail);
//        emailService.sendSimpleMessage(studentEmail, "Code for test: " + exam.getExamName()+ " - " + exam.getTheme(), message);
//        logger.info("Sending message for (done): " + studentName + " " + studentEmail);
//        params.put("message", "Your code was generated and sent by email: " + studentEmail);
//
//        return new ModelAndView("examinationRegistration", params);
//    }

//    private String getURLforStartTest(String code) {
//        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
//        URI newUri = builder.build().toUri();
//        return newUri.toString() + "/start_test?code=" + code;
//    }


//    @GetMapping(value="/start_test", params = "test")
//    public ModelAndView showRegistrationPage(@RequestParam String code, Model model) {
//        //Iterable<Course> courses = coursesRepo.findAll();
//
//        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("code", code);
//        return new ModelAndView("examinationStart", params);
//    }
//
//    @PostMapping(value="/start_test")
//    public ModelAndView startTest(@RequestParam String studentCode) {
//        HashMap<String, Object> params = new HashMap<String, Object>();
//        StudentSession session = sessionsRepo.findByCode(studentCode);
//        if (session == null) { // was not generated
//                params.put("warning", "Unknown code (check code in Email)");
//                return new ModelAndView("examinationRegistration", params);
//        } else {
//            if(session.getResult() != -1){
//                params.put("warning", "Test already processed (check results in Email)");
//                return new ModelAndView("examinationRegistration", params);
//            }
//        }
//
//        Exam exam = examsRepo.findById(session.getExam()).get();
//        LocalDateTime startTime = LocalDateTime.parse(exam.getStartDateTime(), DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT));
//        LocalDateTime endTime = LocalDateTime.parse(exam.getEndDateTime(), DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT));
//        if (LocalDateTime.now().isBefore(startTime)){
//            params.put("warning", "Too early. Exam will start at " + exam.getStartDateTime());
//            return new ModelAndView("examinationRegistration", params);
//        }
//        if (LocalDateTime.now().isAfter(endTime)){
//            params.put("warning", "Exam already finished " + endTime);
//            return new ModelAndView("examinationRegistration", params);
//        }
//
//        session.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(DEFAULT_TIME_FORMAT)));
//
//        sessionsRepo.save(session);
//
//        List<Question> questions = generateQuestionsFor(exam);
//        params.put("timeout", exam.getTimeForCompletionInMinutes() * 60);
//        params.put("studentCode", studentCode);
//        params.put("questions", questions);
//        return new ModelAndView("examinationTest", params);
//    }

    @PostMapping(value="/start_test")
    public ModelAndView showRegistrationPage(@RequestParam Long examID, @RequestParam String userName, HttpServletRequest request) {
        //Iterable<Course> courses = coursesRepo.findAll();

        String lang = (String)request.getSession().getAttribute("lang");

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("examID", examID);

        Exam exam = examsRepo.findById(examID).get();
        User user = usersRepo.findByUsername(userName);

        LocalDateTime startTime = LocalDateTime.parse(exam.getStartDateTime(), DateTimeFormatter.ofPattern(dateTimeFormat));
        LocalDateTime endTime = LocalDateTime.parse(exam.getEndDateTime(), DateTimeFormatter.ofPattern(dateTimeFormat));
        if (LocalDateTime.now().isBefore(startTime)){
            if(lang.equals("ENG"))
                params.put("warning", "Too early. Exam will start at " + exam.getStartDateTime());
            else
                params.put("warning", "Зарано. Заняття почнеться " + exam.getStartDateTime());
            return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
        }
        if (LocalDateTime.now().isAfter(endTime)){
            if(lang.equals("ENG"))
                params.put("warning", "Exam already finished " + endTime);
            else
                params.put("warning", "Заняття закінчилось " + endTime);

            return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
        }

        Iterable<StudentSession> sessions = sessionsRepo.findAllByEmail(user.getEmail());
        for(StudentSession session : sessions){
            if (session.getExam().getId().equals(examID)) { // was already generated
                logger.info("Test was already processed : " + user.getName() + " " + user.getEmail());
                if(lang.equals("ENG")) {

                    params.put("warning", "Test was already processed: \n " + session.getEmail() + "\n" + session.getName());
                } else {
                    params.put("warning", "Тест вже був оброблений: \n " + session.getEmail() + "\n" + session.getName());
                }

                return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
            }
        }

        StudentSession session = new StudentSession(user.getName(), user.getEmail(), user.getGroupName(), LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)), exam);
        String code = generateCodeForStudent(user.getName(), examID);
        session.setCode(code);
        sessionsRepo.save(session);

        List<Question> questions = generateQuestionsFor(exam);
        params.put("timeout", exam.getTimeForCompletionInMinutes() * 60);
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
            if(question.getCorrectAnswer().equals(allAnswers.get(questionText))){
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
            return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
        }

        session.setResult(mark);
        session.setResultString(resultString);
        session.setEndTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat )));
        sessionsRepo.save(session);

        Exam exam = examsRepo.findById(session.getExam().getId()).get();

        String message = "Student:  " + session.getName() + "\n" +
                "Group  :  " + session.getGroupName() + "\n" +
                "Theme  :  " + exam.getExamName() + " - " + exam.getTheme() + "\n" +
                "Start time:  " + session.getTime() + "\n" +
                "End time  :  " + session.getEndTime() + "\n" +
                "Result :  " + mark + "/" + DEFAULT_NUMBER_OF_QUESTIONS  + "\n" +
                "Thank you";

        emailService.sendSimpleMessage(session.getEmail(), "Result of test: " + exam.getExamName()+ " - " + exam.getTheme(), message);
        redirectAttr.addFlashAttribute("message",  "Your result is : " + mark + "/" + DEFAULT_NUMBER_OF_QUESTIONS);
        //params.put("message", "Your result is : " + mark + "/" + DEFAULT_NUMBER_OF_QUESTIONS);
        return new ModelAndView("redirect:/");
    }

    ArrayList<Question> generateQuestionsFor(Exam exam){

        //TODO : refactor before showing to real people
        String UKR_LETTER1 = "а";
        String UKR_LETTER2 = "о";
        String UKR_LETTER3 = "у";

        int MAX_NUM_OF_QUESTIONS = DEFAULT_NUMBER_OF_QUESTIONS;

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
                        Question candidateItem = allQuestionsForCourse.get(random.nextInt(allQuestionsForCourse.size()));
                        if (!results.contains(candidateItem))
                            results.add(candidateItem);
                    }
                }
            }
        }
        if(exam.getTheme().equals("FINAL_UKR")){

            ArrayList<Theme> allThemes = (ArrayList<Theme>) themeRepo.findAllByOrderByNameAsc();

            for(Theme theme : allThemes) {
                if (theme.getName().contains(UKR_LETTER1) || theme.getName().contains(UKR_LETTER2) || theme.getName().contains(UKR_LETTER3)) {
                    allQuestionsForCourse = (ArrayList<Question>) questionsRepo.findAllByTheme(theme.getName());

                    int MAX_NUM_OF_QUESTIONS_PER_THEME = results.size() + 1; // one question per theme
                    Random random = new Random();
                    while (results.size() < MAX_NUM_OF_QUESTIONS) {
                        Question candidateItem = allQuestionsForCourse.get(random.nextInt(allQuestionsForCourse.size()));
                        if (!results.contains(candidateItem))
                            results.add(candidateItem);
                    }
                }
            }

        }  else {
            allQuestionsForCourse = questionsRepo.findAllByTheme(exam.getTheme());

            Random random = new Random();
            while (results.size() < MAX_NUM_OF_QUESTIONS) {
                Question candidateItem = allQuestionsForCourse.get(random.nextInt(allQuestionsForCourse.size()));
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
