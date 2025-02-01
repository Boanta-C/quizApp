package org.example.quizapp.controller;

import org.example.quizapp.dto.QuestionDTO;
import org.example.quizapp.entity.Question;
import org.example.quizapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/quiz")
@SessionAttributes("selectedQuestions")
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
                            @RequestParam(required = false) List<String> difficultyLevels,
                            Model model) {

        List<Question> selectedQuestions;
        List<QuestionDTO> selectedQuestionDTOS = new ArrayList<>();

        selectedQuestions = questionService.getRandomQuestions(language, domains, questionTypes,
                numberOfQuestions, difficultyLevels);

        for (Question question : selectedQuestions) {
            selectedQuestionDTOS.add(questionService.convertToQuestionDTO(question));
        }

        model.addAttribute("selectedQuestions", selectedQuestionDTOS);

        return "redirect:/quiz/question?index=0";
    }

    @GetMapping("/getDomainsByLanguage")
    @ResponseBody
    public List<String> getDomainsByLanguage(@RequestParam String language) {
        return questionService.getDomainsByLanguage(language);
    }

    @GetMapping("/getQuestionTypesByLanguage")
    @ResponseBody
    public List<String> getQuestionTypesByLanguages(@RequestParam String language) {
        return questionService.getQuestionTypesByLanguage(language);
    }

    @GetMapping("/getDifficultyLevelsByLanguage")
    @ResponseBody
    public List<String> getDifficultyLevelByLanguages(@RequestParam String language) {
        return questionService.getDifficultyLevelByLanguage(language);
    }

    @GetMapping("/getQuestionTypesByDomains")
    @ResponseBody
    public List<String> getQuestionTypesByDomains(@RequestParam List<String> domains) {
        return questionService.getQuestionTypesByDomains(domains);
    }

    @GetMapping("/getDifficultyLevelsByQuestionTypes")
    @ResponseBody
    public List<String> getDifficultyLevelsByQuestionTypes(@RequestParam List<String> questionTypes) {
        return questionService.getDifficultyLevelsByQuestionTypes(questionTypes);
    }

    @GetMapping("/getDifficultyLevelsByDomains")
    @ResponseBody
    public List<String> getDifficultyLevelsByDomains(@RequestParam List<String> domains) {
        return questionService.getDifficultyLevelsByDomains(domains);
    }

    @GetMapping("/question")
    public String showQuestion(@SessionAttribute("selectedQuestions") List<QuestionDTO> selectedQuestionDTOS,
                               @RequestParam(defaultValue = "0") int index,
                               Model model) {

        if (index >= selectedQuestionDTOS.size()) {
            return "redirect:/quiz/results"; // If no more questions, go to results page
        }

        model.addAttribute("question", selectedQuestionDTOS.get(index));
        model.addAttribute("currentIndex", index);
        model.addAttribute("totalQuestions", selectedQuestionDTOS.size());

        return "quizQuestion";
    }

    @PostMapping("/question")
    public String nextQuestion(@RequestParam int currentIndex) {
        return "redirect:/quiz/question?index=" + (currentIndex + 1);
    }
}
