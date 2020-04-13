package edu.testconductor.controllers;

import edu.testconductor.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        return new ModelAndView("login", params);
    }

//    @RequestMapping(value = "/signin", method = RequestMethod.POST)
//    public ModelAndView loginProcessing(@RequestParam String username, @RequestParam String password) {
//        ModelAndView modelAndView = new ModelAndView();
//        User user = new User();
//        modelAndView.addObject("user", user);
//        modelAndView.setViewName("/");
//        return modelAndView;
//    }
}