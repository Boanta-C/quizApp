package org.example.quizapp.controller;

import org.example.QuizAppApplication;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@ContextConfiguration(classes = QuizAppApplication.class)
public class HomeController {

    @GetMapping("/")
    public String redirectHome() {
        return "redirect:/home";
    }


    @GetMapping("/home")
    public String home() {
        return "home"; // A template for the home page with links to other parts of the application
    }

}
