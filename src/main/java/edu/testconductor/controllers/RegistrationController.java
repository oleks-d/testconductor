package edu.testconductor.controllers;

import edu.testconductor.domain.*;
import edu.testconductor.repos.GroupsRepo;
import edu.testconductor.repos.QuestionsRepo;
import edu.testconductor.repos.UserRepo;
import edu.testconductor.services.EmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
public class RegistrationController {

    @Autowired
    private UserRepo usersRepo;

    @Autowired
    private GroupsRepo groupsRepo;

    @Autowired
    private EmailServiceImpl emailService;

    @Value("${app.datetime.format}")
    private String dateTimeFormat;

    private static Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @GetMapping(value="/registration")
    public ModelAndView registration(HttpServletRequest request) {
        String warning = (String)request.getSession().getAttribute("warning");

        HashMap<String, Object> params = new HashMap<String, Object>();
        if (warning != null && !warning.equals("")){
            request.getSession().setAttribute("warning", "");
            params.put("waring", "");
        }

        Iterable<StudentGroup> groups = groupsRepo.findAllByOrderByGroupNameAsc();
        params.put("groups", groups);
        return new ModelAndView("register", params);
    }

    @PostMapping(value = "/registration")
    public ModelAndView addStudent(@RequestParam String password, @RequestParam String email, @RequestParam String studentName, @RequestParam Long groupID) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        if(!email.toLowerCase().trim().matches("^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]+$")){
            params.put("warning", "Неправильний формат Email / Wrong format of Email \n" + email);
            Iterable<StudentGroup> groups = groupsRepo.findAllByOrderByGroupNameAsc();
            params.put("groups", groups);
            return new ModelAndView("register", params);
        }

        email= email.toLowerCase().trim();

        User userFromDB = usersRepo.findByUsername(email);


        if(userFromDB != null) {
            params.put("warning", "Користувач вже існує / User already exists! \n" + email);
            Iterable<StudentGroup> groups = groupsRepo.findAllByOrderByGroupNameAsc();
            params.put("groups", groups);
            return new ModelAndView("register", params);
        }

        userFromDB = usersRepo.findByEmail(email);

        if(userFromDB != null) {
            params.put("warning", "Такий Email вже використано / Specified Email already used \n" + email);
            Iterable<StudentGroup> groups = groupsRepo.findAllByOrderByGroupNameAsc();
            params.put("groups", groups);
            return new ModelAndView("register", params);
        }
        String code = generateRegistrationCodeForStudent(studentName, email);
        String groupName = groupsRepo.getOne(groupID).getGroupName();
        if(groupName.contains(Character.toString((char)160))) {
            logger.warn("Found byaka for: " + email);
            groupName = groupName.replace(Character.toString((char)160), " ");
        }
        User user = new User(email, password, email, studentName, groupName, code);
        user.setActive(false);
        HashSet roles = new HashSet();
        roles.add(Role.getRoleByLabel("STUDENT"));
        user.setRoles(roles);

        String lang = groupsRepo.findByGroupName(user.getGroupName()).getLang();
        if(lang.equals("UKR"))
            params.put("message", "Студент створений : ПОВІДОМТЕ ВЧИТЕЛЯ ");// Код активації вислано " + email);
        else
            params.put("message", "User was created :  NOTIFY YOUR TEACHER "); //Activation code sent " + email);


        String message = "";
        String title = "";
        if(lang.equals("UKR")) {
             message = "Натисніть щоб завершити реєстрацію: " +
                    getURLforStartTest(code) + "\n" +

                    "Дякуємо";
             title = "Доступ до Тестів";
        } else {
             message = "Please use click on following link to finish registration: " +
                    getURLforStartTest(code) + "\n" +

                    "Thank you";
             title = "Access to Test Portal";
        }

        //TODO TMP EMAIL SERVER
        //emailService.sendSimpleMessage(email, title, message);

        usersRepo.save(user);

        params.put("email", email);
        params.put("lang", lang);
        return new ModelAndView("login", params);
        //return new ModelAndView("registrationInfo", params);
    }

    private String getURLforStartTest(String code) {
        ServletUriComponentsBuilder builder = ServletUriComponentsBuilder.fromCurrentContextPath();
        URI newUri = builder.build().toUri();
        return newUri.toString() + "/register_student?code=" + code;
    }

    private String generateRegistrationCodeForStudent(String studentName, String studentEmail) {
        String code = Base64.getEncoder().encodeToString(studentName.getBytes()) +
                Base64.getEncoder().encodeToString(studentEmail.toString().getBytes()) +
                Base64.getEncoder().encodeToString(LocalDateTime.now().format(DateTimeFormatter.ofPattern(dateTimeFormat)).getBytes());
        return code.replace("+", "");
    }

    @GetMapping(value="/register_student", params = "code")
    public ModelAndView finishRegistration(@RequestParam String code, Model model) {
        User user = usersRepo.findByCode(code);

        HashMap<String, Object> params = new HashMap<String, Object>();
        if( user != null) {
            user.setActive(true);
            usersRepo.save(user);
            params.put("message", "Користувач активований / User was activated");
        } else {
            params.put("warning", "Користувач не знайдений / User was not found in DB");
        }
        return new ModelAndView("login", params);
    }
}