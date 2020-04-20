package edu.testconductor.controllers;

import edu.testconductor.domain.*;
import edu.testconductor.repos.GroupsRepo;
import edu.testconductor.repos.UserRepo;
import edu.testconductor.services.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;


@Controller
public class UsersController {

    @Autowired
    private UserRepo usersRepo;

    @Autowired
    private EmailServiceImpl emailService;

    @Autowired
    private GroupsRepo groupsRepo;

    @Value("${app.datetime.format}")
    private String dateTimeFormat;

    @PreAuthorize("hasAuthority(1)")
    @GetMapping(value = "/users")
    public ModelAndView filterUsers(@RequestParam("groupID") Optional<Long> groupID) {

        Long group;
        if(groupID.isPresent())
            group = groupID.get();
        else
            group = groupsRepo.findFirstByOrderById().getId();
        String groupName = groupsRepo.getOne(group).getGroupName();
        Iterable<User> users = usersRepo.findAllByGroupName(groupName);

        Iterable<StudentGroup> groups = groupsRepo.findAll();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("groupID", groupID);
        params.put("users", users);
        params.put("groups", groups);

        return new ModelAndView("users", params);
    }


    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/userEdit")
    public ModelAndView editUser(@RequestParam Long userID, @RequestParam String userEmail, @RequestParam String userName, @RequestParam String userGroup) {

        User user = usersRepo.findById(userID).get();
        user.setEmail(userEmail.replace("\r", "").replace("\n", "").trim());
        user.setName(userName.replace("\r", "").replace("\n", "").trim());
        user.setGroupName(userGroup.length()<3 ? " "+userGroup : userGroup );
        String groupName = user.getGroupName();
        usersRepo.save(user);

        Iterable<User> users = usersRepo.findAllByGroupName(groupName);
        Iterable<StudentGroup> groups = groupsRepo.findAll();

        Long groupID = groupsRepo.findByGroupName(groupName).getId();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("groupID", groupID);
        params.put("users", users);
        params.put("groups", groups);

        return new ModelAndView("redirect:/users", params);
    }

    @PreAuthorize("hasAuthority(2)")
    @PostMapping(value = "/userDelete")
    public ModelAndView deleteUser(@RequestParam Long userToDeleteID) {

        User user = usersRepo.findById(userToDeleteID).get();
        String groupName = user.getGroupName();
        Long groupID = groupsRepo.findByGroupName(groupName).getId();
        usersRepo.delete(user);


        Iterable<User> users = usersRepo.findAllWithNonEmptyGroup();
        Iterable<StudentGroup> groups = groupsRepo.findAll();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("groupID", groupID);
        params.put("users", users);
        params.put("groups", groups);

        return new ModelAndView("redirect:/users", params);
    }

    @PreAuthorize("hasAuthority(1)")
    @PostMapping(value = "/activateUser")
    public ModelAndView activateUser(@RequestParam Long userID, @RequestParam String requestedBy) {

        User user = usersRepo.findById(userID).get();
        if (user.isActive()) {
            user.setActive(false);
            user.setHistory("Disabled by " + requestedBy);
        } else {
            user.setActive(true);
            user.setHistory("Enabled by " + requestedBy);
        }
        usersRepo.save(user);

        String groupName = user.getGroupName();

        Iterable<User> users = usersRepo.findAllWithNonEmptyGroup();
        Iterable<StudentGroup> groups = groupsRepo.findAll();
        Long groupID = groupsRepo.findByGroupName(groupName).getId();

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("groupID", groupID);
        params.put("users", users);
        params.put("groups", groups);

        return new ModelAndView("redirect:/users", params);
    }

}