package org.example.quizapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.quizapp.dto.OptionDTO;
import org.example.quizapp.dto.QuestionDTO;
import org.example.quizapp.entity.Option;
import org.example.quizapp.entity.Question;
import org.example.quizapp.service.OptionService;
import org.example.quizapp.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    private final OptionService optionService;

    @Autowired
    public QuestionController(QuestionService questionService, OptionService optionService) {
        this.questionService = questionService;
        this.optionService = optionService;
    }


    @GetMapping("/all")
    public String getAllQuestions(Model model) {
        List<QuestionDTO> questionsDTO = new ArrayList<>();
        List<Question> questions = questionService.getAllQuestions();

        for (Question question : questions) {
            QuestionDTO questionDTO = new QuestionDTO();
            questionDTO.setId(question.getId());
            questionDTO.setQuestionText(question.getQuestionText());
            questionDTO.setCorrectAnswer(question.getCorrectAnswer());
            questionDTO.setQuestionType(question.getQuestionType());
            questionDTO.setDomain(question.getDomain());
            questionDTO.setLanguage(question.getLanguage());
            questionDTO.setDifficultyLevel(question.getDifficultyLevel());
            questionDTO.setActive(question.isActive());

            List<Option> options = optionService.getOptionsByQuestionId(question.getId());

            List<OptionDTO> optionDTOs = options.stream()
                    .map(option -> {
                        OptionDTO optionDTO = new OptionDTO();
                        optionDTO.setOptionText(option.getOptionText());
                        optionDTO.setCorrect(option.isCorrect());
                        return optionDTO;
                    })
                    .collect(Collectors.toList());

            questionDTO.setOptions(optionDTOs);

            questionsDTO.add(questionDTO);
        }

        model.addAttribute("questions", questionsDTO);
        return "allQuestions";
    }

    @GetMapping("/edit/{id}")
    public String showEditQuestion(@PathVariable("id") Long id, Model model) {
        Optional<Question> question = questionService.getQuestionById(id);
        QuestionDTO questionDTO = new QuestionDTO();
        if(question.isPresent()) {
            questionDTO.setId(question.get().getId());
            questionDTO.setQuestionText(question.get().getQuestionText());
            questionDTO.setCorrectAnswer(question.get().getCorrectAnswer());
            questionDTO.setQuestionType(question.get().getQuestionType());
            questionDTO.setDomain(question.get().getDomain());
            questionDTO.setLanguage(question.get().getLanguage());
            questionDTO.setDifficultyLevel(question.get().getDifficultyLevel());
            questionDTO.setActive(question.get().isActive());

        List<Option> options = optionService.getOptionsByQuestionId(id);
        List<OptionDTO> optionDTOs = options.stream()
                .map(option -> {OptionDTO optionDTO = new OptionDTO();
                    optionDTO.setOptionText(option.getOptionText());
                    optionDTO.setCorrect(option.isCorrect());
                    return optionDTO;}).toList();
            questionDTO.setOptions(optionDTOs);
        }
        model.addAttribute("questionDTO", questionDTO);

        if(questionDTO.getQuestionType().equals("Open Ended")) {
            return "editOpenQuestion";
        } else
            return "editChoiceQuestion";

    }

    @PostMapping("/edit/{id}")
    public String editQuestion(@ModelAttribute @Valid QuestionDTO questionDTO, Model model, RedirectAttributes redirectAttributes) {

        questionService.editQuestion(questionDTO);
        model.addAttribute("questionDTO", questionDTO);
        redirectAttributes.addFlashAttribute("question", questionDTO);
        return "redirect:/questions/question-edited";
    }

    @PostMapping("/delete/{id}")
    public String deleteQuestion(@PathVariable Long id) {

        questionService.deleteQuestionById(id);
        return "redirect:/questions/all";
    }

    @PostMapping("/deleteAllQuestions")
    public String deleteAllQuestions() {
        questionService.deleteAllQuestions();
        return "redirect:/questions/all";
    }

    @GetMapping("/exportAllQuestions")
    public void exportQuestions(HttpServletResponse response) throws IOException {
        questionService.exportQuestionsToCSV(response);
    }

    @PostMapping("/toggleActive/{id}")
    public String toggleActiveStatus(@PathVariable Long id) {
        questionService.toggleActiveStatus(id);
        return "redirect:/questions/all";  // Redirect back to the questions list
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

    @GetMapping("/question-edited")
    public String showQuestionEditedPage() {
        return "questionEdited";
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

    @GetMapping("/download-template")
    public void downloadCsvTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=question_template.csv");

        PrintWriter writer = response.getWriter();

        writer.println("Question Type, Domain, Language, Difficulty Level, Question Text, Correct Answer, First Answer, Is Correct, Second Answer,  Is Correct, Third Answer, Is Correct, Fourth Answer, Is Correct");

        writer.flush();
        writer.close();
    }

}
