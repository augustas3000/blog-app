package com.blog.blogapp.controller;

import com.blog.blogapp.exception.NotFoundException;
import com.blog.blogapp.model.User;
import com.blog.blogapp.service.UserService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class BaseControllerAdvice {

    private final UserService userService;

    public BaseControllerAdvice(UserService userService) {
        this.userService = userService;
    }

    @ExceptionHandler(NotFoundException.class)
    public String handledNotFoundException(NotFoundException e, Model
            model) {
        model.addAttribute("status", 400);
        model.addAttribute("exception", e);

        return "common/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception e, Model model) {
        model.addAttribute("status", 500);
        model.addAttribute("exception", e);

        return "common/error";
    }

    @ModelAttribute
    public void addCommonAttributes(
//            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
//        if (userDetails != null) {
//            User user = userService.getByUsername(userDetails.getUsername());
        User user = new User();
        user.setUsername("Augustas");
        model.addAttribute("user", user);
//        }
    }
}


