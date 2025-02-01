package org.example.quizapp.controller;

import jakarta.servlet.http.HttpSession;
import org.example.quizapp.dto.QuestionDTO;
import org.example.quizapp.entity.Question;
import org.example.quizapp.service.AnswerService;
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
    private final AnswerService answerService;

    @Autowired
    public QuizController(QuestionService questionService, AnswerService answerService) {
        this.questionService = questionService;
        this.answerService = answerService;
    }

    @GetMapping("/setup")
    public String showSetupPage(Model model) {
        model.addAttribute("languages", questionService.getDistinctLanguages());
        model.addAttribute("domains", questionService.getDistinctDomains());
        model.addAttribute("questionTypes", questionService.getDistinctQuestionTypes());
        model.addAttribute("difficultyLevels", questionService.getDistinctDifficultyLevels());

        return "quiz/quizSetup";
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
            selectedQuestionDTOS.add(questionService.convertQuestionToDto(question));
        }

        model.addAttribute("selectedQuestions", selectedQuestionDTOS);

        return "redirect:/quiz/question?index=0";
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

        return "quiz/quizQuestion";
    }

    @PostMapping("/question")
    public String sendAnswer(@RequestParam int currentIndex,
                             @RequestParam String answer,
                             @RequestParam String questionType,
                             @SessionAttribute(value = "userAnswers", required = false) List<String> userAnswers,
                             HttpSession session) {



        userAnswers = answerService.processAnswer(userAnswers,answer, questionType, currentIndex);
        session.setAttribute("userAnswers", userAnswers);

        return "redirect:/quiz/question?index=" + (currentIndex + 1);
    }

    @GetMapping("/results")
    public String showResults(@SessionAttribute("selectedQuestions") List<QuestionDTO> selectedQuestionDTOS,
                              @SessionAttribute("userAnswers") List<String> userAnswers,
                              Model model) {

        model.addAttribute("questions", selectedQuestionDTOS);
        model.addAttribute("userAnswers", userAnswers);

        return "quiz/quizResults";
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
    public List<String> getQuestionTypesByDomainsAndLanguage(@RequestParam List<String> domains,
                                                             String language) {
        return questionService.getQuestionTypesByDomainsAndLanguage(domains, language);
    }

    @GetMapping("/getDifficultyLevelsByQuestionTypes")
    @ResponseBody
    public List<String> getDifficultyLevelsByQuestionTypes(@RequestParam List<String> questionTypes, @RequestParam (required = false) List<String> domains) {
        return questionService.getDifficultyLevelsByQuestionTypes(questionTypes, domains);
    }

    @GetMapping("/getDifficultyLevelsByDomains")
    @ResponseBody
    public List<String> getDifficultyLevelsByDomains(@RequestParam List<String> domains, String language) {
        return questionService.getDifficultyLevelsByDomainsAndLanguage(domains, language);
    }

    @GetMapping("/getCountNumberOfSelectedQuestions")
    @ResponseBody
    public Integer getCountNumberOfSelectedQuestions(@RequestParam String language,
                                                     @RequestParam (required = false) List<String> domains,
                                                     @RequestParam (required = false) List<String> questionTypes,
                                                     @RequestParam (required = false) List<String> difficultyLevel) {
        return questionService.getCountNumberOfSelectedQuestions(language, domains, questionTypes, difficultyLevel);
    }
}
