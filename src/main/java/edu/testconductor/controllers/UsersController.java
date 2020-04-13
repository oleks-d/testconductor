package edu.testconductor.controllers;

import edu.testconductor.domain.*;
import edu.testconductor.repos.GroupsRepo;
import edu.testconductor.repos.UserRepo;
import edu.testconductor.services.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;


@Controller
public class UsersController {

    @Autowired
    private UserRepo usersRepo;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private GroupsRepo groupsRepo;

    @PreAuthorize("hasAuthority(1)")
    @GetMapping(value="/users")
    public ModelAndView viewUsers() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        Iterable<User> users = usersRepo.findAllWithNonEmptyGroup();
        Iterable<StudentGroup> groups = groupsRepo.findAll();
        params.put("users", users);
        params.put("groups", groups);
        return new ModelAndView("users", params);
    }

    @PreAuthorize("hasAuthority(1)")
    @PostMapping(value = "/users")
    public ModelAndView addUser(@RequestParam String username, @RequestParam String password, @RequestParam String email, @RequestParam String teacherName) {

        User userFromDB = usersRepo.findByUsername(username);

        HashMap<String, Object> params = new HashMap<String, Object>();

        if(userFromDB != null) {
            params.put("warning", "User already exists!" + username);
            return new ModelAndView("users", params);
        }

        userFromDB = usersRepo.findByEmail(email);

        if(userFromDB != null) {
            params.put("warning", "User Email already used!" + email);
            return new ModelAndView("users", params);
        }

        User user = new User(username, password, email, teacherName, "", "");
        user.setActive(true);
        HashSet roles = new HashSet();
        roles.add(Role.getRoleByLabel("USER"));
        user.setRoles(roles);
        params.put("message", "User was created: " + username);

        emailService.sendSimpleMessage(email, "Access granted to Test Portal", "Access granted to test Portal: \n" + "Username: " + username + "\nPassword: " + password);

        usersRepo.save(user);

        return new ModelAndView("users", params);
    }

    @PreAuthorize("hasAuthority(1)")
    @PostMapping(value = "/users", params = "show")
    public ModelAndView filterUsers(@RequestParam Long groupID) {

        String groupName = groupsRepo.getOne(groupID).getGroupName();
        Iterable<User> users = usersRepo.findAllByGroupName(groupName);

        Iterable<StudentGroup> groups = groupsRepo.findAll();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("selectedGroup", groupName);
        params.put("users", users);
        params.put("groups", groups);

        return new ModelAndView("users", params);
    }

    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/users", params = "edit")
    public ModelAndView editUser(@RequestParam Long userID, @RequestParam String userEmail, @RequestParam String userName, @RequestParam String userGroup) {

        User user = usersRepo.findById(userID).get();
        user.setEmail(userEmail.replace("\r", "").replace("\n", "").trim());
        user.setName(userName.replace("\r", "").replace("\n", "").trim());
        user.setGroupName(userGroup.length()<3 ? " "+userGroup : userGroup );
        String groupName = user.getGroupName();
        usersRepo.save(user);

        Iterable<User> users = usersRepo.findAllByGroupName(groupName);
        Iterable<StudentGroup> groups = groupsRepo.findAll();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("selectedGroup", groupName);
        params.put("users", users);
        params.put("groups", groups);

        return new ModelAndView("users", params);
    }

    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/users", params = "delete")
    public ModelAndView deleteUser(@RequestParam Long userToDeleteID) {

        User user = usersRepo.findById(userToDeleteID).get();
        String groupName = user.getGroupName();
        usersRepo.delete(user);


        Iterable<User> users = usersRepo.findAllWithNonEmptyGroup();
        Iterable<StudentGroup> groups = groupsRepo.findAll();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("selectedGroup", groupName);
        params.put("users", users);
        params.put("groups", groups);

        return new ModelAndView("users", params);
    }


}