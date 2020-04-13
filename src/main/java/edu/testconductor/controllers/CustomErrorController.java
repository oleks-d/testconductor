package edu.testconductor.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@Controller
public class CustomErrorController implements ErrorController {

    private static Logger logger = LoggerFactory.getLogger(CustomErrorController.class);
    @RequestMapping("/error")
    public ModelAndView handleError(HttpServletRequest request) {
        logger.warn("Processing error");
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        String exceptionMessage = "";
        String errorMessage = "";
        try {
            exceptionMessage = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION).toString();
        }catch (Exception e){
            //TODO
        };

        try {
            errorMessage = request.getAttribute(RequestDispatcher.ERROR_MESSAGE).toString();
        }catch (Exception e){
            //TODO
        };


        HashMap<String,String> params = new HashMap<>();

        //if (status != null) {
        Integer statusCode = Integer.valueOf(status.toString());

        params.put("errorStatus", String.valueOf(statusCode));
        params.put("errorMessage", errorMessage + " -- " +exceptionMessage);
        return new ModelAndView("error", params);
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}