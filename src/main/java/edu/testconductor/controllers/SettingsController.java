package edu.testconductor.controllers;

import edu.testconductor.domain.Role;
import edu.testconductor.domain.StudentGroup;
import edu.testconductor.domain.User;
import edu.testconductor.repos.GroupsRepo;
import edu.testconductor.repos.UserRepo;
import edu.testconductor.services.EmailServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.HashSet;


@Controller
public class SettingsController {

    @Autowired
    private UserRepo usersRepo;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private GroupsRepo groupsRepo;

    private static Logger logger = LoggerFactory.getLogger(SettingsController.class);

    @PreAuthorize("hasAuthority(2)")
    @GetMapping(value="/settings")
    public ModelAndView registration() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        return new ModelAndView("settings", params);
    }

    @PostMapping(value = "/add_teacher")
    public ModelAndView addUser(@RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam String name) {

        User userFromDB = usersRepo.findByUsername(username);

        HashMap<String, Object> params = new HashMap<String, Object>();

        if(userFromDB != null) {
            params.put("warning", "User already exists!" + username);
            return new ModelAndView("settings", params);
        }

        userFromDB = usersRepo.findByEmail(email);

        if(userFromDB != null) {
            params.put("warning", "User Email already used!" + email);
            return new ModelAndView("settings", params);
        }

        User user = new User(username, password, email, name, "", "");
        user.setActive(true);
        HashSet roles = new HashSet();
        roles.add(Role.getRoleByLabel("USER"));
        user.setRoles(roles);
        params.put("message", "User was created: " + username);

        emailService.sendSimpleMessage(email, "Access granted to Test Portal", "Access granted to test Portal: \n" + "Username: " + username + "\nPassword: " + password);

        usersRepo.save(user);

        return new ModelAndView("settings", params);
    }

    @PostMapping(value = "/add_groups")
    public ModelAndView addUser(@RequestParam String groupNames, @RequestParam String lang) {

        StudentGroup groupFromDB = null;
        String message = "";
        HashMap<String, Object> params = new HashMap<String, Object>();

        for(String group : groupNames.split(" ")) {

            groupFromDB = groupsRepo.findByGroupName(group);

            if (groupFromDB != null) {
                message = message + "\nGroup already exists!" + group;
                continue;
            }
            StudentGroup newGroup = new StudentGroup(group, lang);
            groupsRepo.save(newGroup);
            message = message + "\nGroup was added: " + group;
        }

        params.put("message", message);

        return new ModelAndView("settings", params);
    }
}