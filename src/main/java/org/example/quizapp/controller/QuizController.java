package org.example.quizapp.controller;

import org.example.quizapp.entity.Question;
import org.example.quizapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuestionService questionService;

    @Autowired
    public QuizController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/setup")
    public String showSetupPage(Model model) {
        model.addAttribute("languages", questionService.getDistinctLanguages());
        model.addAttribute("domains", questionService.getDistinctDomains());
        model.addAttribute("questionTypes", questionService.getDistinctQuestionTypes());
        model.addAttribute("difficultyLevels", questionService.getDistinctDifficultyLevels());

        return "quizSetup";
    }

    @PostMapping("/setup")
    public String startQuiz(@RequestParam String language,
                            @RequestParam List<String> domains,
                            @RequestParam List<String> questionTypes,
                            @RequestParam int numberOfQuestions,
                            @RequestParam(required = false) String difficultyLevels,
                            Model model) {

        List<Question> selectedQuestions;

        selectedQuestions = questionService.getRandomQuestions(language, domains, questionTypes,
                numberOfQuestions, difficultyLevels);

        model.addAttribute("questions", selectedQuestions);

        return "quizPage";
    }
}
