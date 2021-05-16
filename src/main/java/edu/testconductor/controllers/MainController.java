package edu.testconductor.controllers;

import edu.testconductor.domain.*;
import edu.testconductor.repos.*;
import edu.testconductor.services.MainPageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private UserRepo usersRepo;

    @Autowired
    private ExamsRepo examsRepo;

    @Autowired
    private GroupsRepo groupsRepo;

    @Autowired
    private StudentSessionRepo sessionsRepo;

    @Autowired
    private MainPageService mainPageService;

    @Value("${app.datetime.format}")
    private String dateTimeFormat;

    //TODO make custom
    int DEFAULT_NUMBER_OF_QUESTIONS = 10;
    public static int DEFAULT_NUMBER_OF_QUESTIONS_FOR_EXAM = 20;

    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping(value="/")
    public ModelAndView showIndex(@RequestParam Map<String,String> parameters, HttpServletRequest request, Model model) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = usersRepo.findByUsername(username);
        StudentGroup group = groupsRepo.findByGroupName(user.getGroupName());
        String language = "UKR";
        if(group != null && !group.getLang().equals(""))
            language = group.getLang();

        request.getSession().setAttribute("currentUserName", user.getName());
        request.getSession().setAttribute("lang",language);

        String message = (String)model.asMap().get("message");
        HashMap<String, Object> params = new HashMap<String, Object>();
        parameters.keySet().forEach(key -> params.put(key, parameters.get(key)));
        params.put("message", message);
        params.put("teacherName", username);
        params.put("showOnlyMine", true);

        return searchExam("", username);

        //return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
    }

    @PreAuthorize("hasAuthority(1)")
    @GetMapping(value = "/search")
    public ModelAndView searchExam(@RequestParam String examName, @RequestParam String teacherName) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        if(!teacherName.equals("")) {
            teacherName = usersRepo.findByUsername(teacherName).getName();
            params.put("showOnlyMine", true);
        } else {
            params.put("showOnlyMine", false);
        }

        if(!examName.equals("REWORK") && !examName.equals("")){
            examName = groupsRepo.getOne(Long.valueOf(examName)).getGroupName(); // group name as exam name
        }

        Iterable<Exam> exams = null;
        if(examName.equals("")){
            if (teacherName.equals(""))
                exams = examsRepo.findAllByOrderByStartDateTimeDesc();
            else
                exams = examsRepo.findAllByTeacherOrderByStartDateTimeDesc(teacherName);
        } else {

            if (teacherName.equals(""))
                exams = examsRepo.findAllByExamNameOrderByStartDateTimeDesc(examName);
            else
                exams = examsRepo.findAllByExamNameAndTeacherOrderByStartDateTimeDesc(examName, teacherName);
        }

        params.put("examName", examName);
        params.put("exams", exams);

        return new ModelAndView("index", mainPageService.generateIndexPageParams(params));
    }

    @PreAuthorize("hasAuthority(1)")
    //@PostMapping(value = "/", params = "add_exam")
    @PostMapping(value = "/add_exam")
    public ModelAndView addExam(
            @RequestParam String examName,
            @RequestParam String theme,
            @RequestParam String courseStartTime,
            @RequestParam int courseDurationInMinutes,
            @RequestParam int timeForCompletionInMinutes,
            @RequestParam String teacher,
            RedirectAttributes redirectAttr) {

        LocalDateTime courseEndTimeDate = LocalDateTime.parse(courseStartTime, DateTimeFormatter.ofPattern(dateTimeFormat));
        String courseEndTime = courseEndTimeDate.plusMinutes(courseDurationInMinutes).format(DateTimeFormatter.ofPattern(dateTimeFormat));

        String teacherName = usersRepo.findByUsername(teacher).getName();
        if(!examName.equals("REWORK")){
            examName = groupsRepo.getOne(Long.valueOf(examName)).getGroupName(); // group name as exam name
        }

        int numberOfQuestions = DEFAULT_NUMBER_OF_QUESTIONS;
        if (theme.matches("FINAL.*"))
            numberOfQuestions = DEFAULT_NUMBER_OF_QUESTIONS_FOR_EXAM;
        examsRepo.save(new Exam(examName, courseStartTime, courseEndTime, timeForCompletionInMinutes, numberOfQuestions, theme, teacherName));

        redirectAttr.addFlashAttribute("message", "Заняття для " + examName +" з Теми " + theme + " додано");
        return new ModelAndView("redirect:/");
    }

    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/delete_exam")
    public ModelAndView deleteExam(@RequestParam Long examID, RedirectAttributes redirectAttr) {
        sessionsRepo.deleteAllByExamId(examID);
        examsRepo.deleteById(examID);

        redirectAttr.addFlashAttribute("message", "Exam was deleted (check schedule)");
        return new ModelAndView("redirect:/");
    }

}
