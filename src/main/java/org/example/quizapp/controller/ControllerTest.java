package org.example.quizapp.controller;

import org.example.QuizAppApplication;
import org.example.quizapp.entity.Question;
import org.example.quizapp.service.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@ContextConfiguration(classes = QuizAppApplication.class)
public class ControllerTest {

    private final QuestionService questionService;

    ControllerTest(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("/test-setup")
    public String testSetup(@RequestParam String language,
                            @RequestParam List<String> domains,
                            @RequestParam List<String> questionTypes,
                            @RequestParam int numberOfQuestions,
                            Model model) {
        List<Question> selectedQeustions = questionService.getRandomQuestions(language, domains, questionTypes,
                numberOfQuestions, null);

        model.addAttribute("questions", selectedQeustions);

        return "testQuizPage";
    }
}
