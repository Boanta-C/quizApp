package org.example.quizapp.controller;

import jakarta.validation.Valid;
import org.example.quizapp.dto.QuestionDTO;
import org.example.quizapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/add-single-question")
    public String showAddQuestionForm(Model model) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setLanguage("");
        questionDTO.setDomain("");
        questionDTO.setQuestionType("");
        questionDTO.setDifficultyLevel("");
        model.addAttribute("questionDTO", questionDTO);
        return "addQuestion";
    }

    @PostMapping("/add-single-question")
    public String addQuestion(@ModelAttribute @Valid QuestionDTO questionDTO, Model model, RedirectAttributes redirectAttributes) {

        questionService.addQuestion(questionDTO);
        model.addAttribute("questionDTO", questionDTO);
        redirectAttributes.addFlashAttribute("question", questionDTO);
        return "redirect:/questions/question-added";
    }

    @GetMapping("/question-added")
    public String showQuestionAddedPage() {
        return "questionAdded";
    }

    @GetMapping("/upload-questions")
    public String showUploadForm(Model model) {
        List<String> existingLanguages = questionService.getDistinctLanguages();
        List<String> existingDomains = questionService.getDistinctDomains();
        model.addAttribute("existingLanguages", existingLanguages);
        model.addAttribute("existingDomains", existingDomains);
        return "uploadQuestionsFromFile";
    }

    @PostMapping("/upload-questions")
    public String uploadQuestions(@RequestParam("file") MultipartFile file,
                                  RedirectAttributes redirectAttributes) throws IOException {
        String fileName = file.getOriginalFilename();

        if (!fileName.endsWith(".csv")) {
            redirectAttributes.addFlashAttribute("error", "Invalid file type. Please upload a .csv file.");
            return "redirect:/questions/upload-questions";
        }

        List<String> errors = questionService.uploadQuestionFromCSV(file);

        if (!errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("error","Errors found in the .csv file: :");
            redirectAttributes.addFlashAttribute("errorDetails", errors);
            return "redirect:/questions/upload-questions";
        }
        redirectAttributes.addFlashAttribute("message","Questions uploaded successfully.");
        return "redirect:/questions/upload-questions";

    }

}
