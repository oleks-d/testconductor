package edu.testconductor.controllers;

import edu.testconductor.domain.Exam;
import edu.testconductor.domain.Question;
import edu.testconductor.domain.Theme;
import edu.testconductor.repos.QuestionsRepo;
import edu.testconductor.repos.ExamsRepo;
import edu.testconductor.repos.ThemeRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;


@Controller
public class QuestionsController {

    @Autowired
    private QuestionsRepo questionsRepo;

    @Autowired
    private ExamsRepo examsRepo;

    @Autowired
    private ThemeRepo themeRepo;

    private static Logger logger = LoggerFactory.getLogger(QuestionsController.class);

    @PreAuthorize("hasAuthority(2)")
    @GetMapping(value = "/questions")
    public ModelAndView showQuestions() {

        Iterable<Question> questions = questionsRepo.findAll();
        //Iterable<Exam> exams = examsRepo.findAll();

        Iterable<Theme> themes = themeRepo.findAllByOrderByNameAsc();

        HashMap<String, Object> params = new HashMap<String, Object>();
        //params.put("questions", questions);
        params.put("themes", themes);

        return new ModelAndView("questions", params);
    }

//    @PostMapping(value = "/questions", params = "add_questions")
//    public ModelAndView addQuestion(@RequestParam String questionText, @RequestParam String answersText, @RequestParam String correctAnswerText, @RequestParam String courseName) {
//        questionsRepo.save(new Question(questionText, answersText, correctAnswerText, courseName));
//
//        Iterable<Question> questions = questionsRepo.findAll();
//        Iterable<Course> courses = coursesRepo.findAll();
//
//        HashMap<String, Object> params = new HashMap<String, Object>();
//        params.put("questions", questions);
//        params.put("courses", courses);
//
//        return new ModelAndView("editQuestions", params);
//    }

    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/questions", params = "show")
    public ModelAndView showQuestions(@RequestParam String themeName) {

        Iterable<Question> questions = questionsRepo.findAllByTheme(themeName);
        Iterable<Theme> themes = themeRepo.findAllByOrderByNameAsc();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("selectedTheme", themeName);
        params.put("questions", questions);
        params.put("themes", themes);

        return new ModelAndView("questions", params);
    }


    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/questions", params = "add_questions")
    public ModelAndView addQuestions(@RequestParam String questionsText, @RequestParam String themeName) {

        ArrayList<Question> questionsFromTextArea = getListOfQuestionsFromTextArea(questionsText, themeName);
        for(Question question: questionsFromTextArea) {
            questionsRepo.save(question);
        };

        if(themeRepo.findByName(themeName) == null)
            themeRepo.save(new Theme(themeName));
        Iterable<Question> questions = questionsRepo.findAllByTheme(themeName);
        Iterable<Theme> themes = themeRepo.findAllByOrderByNameAsc();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("selectedTheme", themeName);
        params.put("questions", questions);
        params.put("themes", themes);

        return new ModelAndView("questions", params);
    }

    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/add_question")
    public ModelAndView addQuestion(@RequestParam String questionText, @RequestParam String questionAnswers, @RequestParam String correctAnswer, @RequestParam String themeName) {

        Question newQuestion = new Question(questionText, questionAnswers, correctAnswer, themeName);
        questionsRepo.save(newQuestion);

        Iterable<Question> questions = questionsRepo.findAllByTheme(themeName);
        Iterable<Theme> themes = themeRepo.findAllByOrderByNameAsc();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("selectedTheme", themeName);
        params.put("questions", questions);
        params.put("themes", themes);

        return new ModelAndView("questions", params);
    }

    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/questions", params = "edit")
    public ModelAndView editQuestion(@RequestParam Long questionID, @RequestParam String questionText, @RequestParam String questionAnswers, @RequestParam String correctAnswer) {

        Question question = questionsRepo.findById(questionID).get();
        question.setCorrectAnswer(correctAnswer.replace("\r", "").replace("\n", "").trim());
        question.setAnswers(questionAnswers.replace("\r\n\r\n", "\r\n"));
        question.setText(questionText.replace("\r", "").replace("\n", "").trim());
        String themeName = question.getTheme();
        questionsRepo.save(question);

        Iterable<Question> questions = questionsRepo.findAllByTheme(themeName);
        Iterable<Theme> themes = themeRepo.findAllByOrderByNameAsc();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("selectedTheme", themeName);
        params.put("questions", questions);
        params.put("themes", themes);

        return new ModelAndView("questions", params);
    }

    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/questions", params = "delete")
    public ModelAndView deleteQuestion(@RequestParam Long questionToDeleteID) {

        Question question = questionsRepo.findById(questionToDeleteID).get();
        String themeName = question.getTheme();
        questionsRepo.delete(question);

        Iterable<Question> questions = questionsRepo.findAllByTheme(themeName);
        Iterable<Theme> themes = themeRepo.findAllByOrderByNameAsc();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("selectedTheme", themeName);
        params.put("questions", questions);
        params.put("themes", themes);

        return new ModelAndView("questions", params);
    }

    ArrayList<Question> getListOfQuestionsFromTextArea(String questionsText, String examName) {
        String QUESTION_LABEL = "Q:";
        String ANSWER_LABEL = "A:";
        ArrayList<Question> questionsFromTextArea = new ArrayList<Question>();
        String[] allLines = questionsText.replace("\r", "").split("\n");
        Question currentQuestion = null;
        for(String line : allLines){
            if(line.trim().equals("\n") || line.trim().equals(""))
                continue;
            if(line.trim().startsWith(QUESTION_LABEL)){
                if(currentQuestion != null)
                    questionsFromTextArea.add(currentQuestion);
                currentQuestion = new Question();
                currentQuestion.setTheme(examName);
                currentQuestion.setText(line.trim().substring(2,line.trim().length()));
            } else if(line.trim().startsWith(ANSWER_LABEL)){
                currentQuestion.setCorrectAnswer(line.trim().substring(2,line.trim().length()));
                String correctOption = line.trim().substring(2,line.trim().length());
                currentQuestion.setAnswers(currentQuestion.getAnswers()==null ? correctOption : currentQuestion.getAnswers() + "\n" + correctOption);
            } else {
                currentQuestion.setAnswers(currentQuestion.getAnswers()==null ? line.trim(): currentQuestion.getAnswers()  + "\n" + line.trim());
            }

        };
        questionsFromTextArea.add(currentQuestion);

        return questionsFromTextArea;
    }
}