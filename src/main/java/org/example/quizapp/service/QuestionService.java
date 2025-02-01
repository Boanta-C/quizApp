package org.example.quizapp.service;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.example.quizapp.dto.OptionDTO;
import org.example.quizapp.dto.QuestionDTO;
import org.example.quizapp.entity.Option;
import org.example.quizapp.entity.Question;
import org.example.quizapp.repository.OptionRepository;
import org.example.quizapp.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

    @Autowired
    private OptionService optionService;

    @Autowired
    public QuestionService(QuestionRepository questionRepository,
                           OptionRepository optionRepository) {
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public List<Question> getQuestionsByDomain(String domain) {
        return questionRepository.findQuestionsByDomain(domain);
    }

    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }

    public List<String> getDistinctLanguages() {
        return questionRepository.findDistinctLanguages();
    }

    public List<String> getDistinctDomains() {
        return questionRepository.findDistinctDomains();
    }

    public List<String> getDistinctQuestionTypes() {
        return questionRepository.findDistinctQuestionTypes();
    }

    public List<String> getDistinctDifficultyLevels() {
        return questionRepository.findDistinctDifficultyLevels();
    }

    public List<Question> getRandomQuestions(String language,
                                             List<String> domains,
                                             List<String> questionTypes,
                                             int numberOfQuestions,
                                             List<String> difficultyLevels) {
        Pageable pageable = PageRequest.of(0, numberOfQuestions);
        if(difficultyLevels.isEmpty()) {
            difficultyLevels = null;
        }
        return questionRepository.findRandomQuestions(language,domains, questionTypes, difficultyLevels, pageable);
    }

    @Transactional
    public void addQuestion(QuestionDTO questionDTO) {
        Question question = new Question();
        question.setLanguage(questionDTO.getLanguage());
        question.setDomain(questionDTO.getDomain());
        question.setQuestionText(questionDTO.getQuestionText());
        question.setQuestionType(questionDTO.getQuestionType());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        question.setDifficultyLevel(questionDTO.getDifficultyLevel());
        question.setActive(true);

        questionRepository.save(question);

        if("Single Choice".equals(questionDTO.getQuestionType()) ||
                "Multiple Choice".equals(questionDTO.getQuestionType())) {
            char letter = 'A';
            for (OptionDTO optionDTO : questionDTO.getOptions()) {
                Option option = new Option();
                option.setOptionText(letter + ". " + optionDTO.getOptionText());
                option.setCorrect(optionDTO.isCorrect());
                option.setQuestion(question);
                optionRepository.save(option);
                letter++;
            }
        }
    }

    @Transactional
    public void editQuestion(QuestionDTO questionDTO) {
        Question question = new Question();
        question.setId(questionDTO.getId());
        question.setLanguage(questionDTO.getLanguage());
        question.setDomain(questionDTO.getDomain());
        question.setQuestionText(questionDTO.getQuestionText());
        question.setQuestionType(questionDTO.getQuestionType());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        question.setDifficultyLevel(questionDTO.getDifficultyLevel());
        question.setActive(true);

        questionRepository.save(question);

        if("Single Choice".equals(questionDTO.getQuestionType()) ||
                "Multiple Choice".equals(questionDTO.getQuestionType())) {
            char letter = 'A';
            optionRepository.deleteByQuestionId(questionDTO.getId());
            for (OptionDTO optionDTO : questionDTO.getOptions()) {
                Option option = new Option();
                option.setOptionText(letter + ". " + optionDTO.getOptionText());  // Prepend letter here
                option.setCorrect(optionDTO.isCorrect());
                option.setQuestion(question);
                optionRepository.save(option);
                letter++;
            }
        }
    }

    public List<String> uploadQuestionFromCSV(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> rows = csvReader.readAll();
            int rowNumber = 1;

            if (!rows.isEmpty() && "Question Type".equalsIgnoreCase(rows.get(0)[0].trim())) {
                rows = rows.subList(1, rows.size());
            }

            for (String[] row : rows) {
                if(row.length < 6) {
                    errors.add("Row " + rowNumber + ": Missing fields.");
                    rowNumber++;
                    continue;
                }

                String questionType = row[0].trim();
                String domain = row[1].trim();
                String language = row[2].trim();
                String difficultyLevel = row[3].trim();
                String questionText = row[4].trim();
                String correctAnswer = row[5].trim();

                if (questionText.isEmpty()) {
                    errors.add("Row " + rowNumber + ": 'Question Text' is empty.");
                } else if (correctAnswer.isEmpty()) {
                    errors.add("Row " + rowNumber + ": 'Correct Answer' is empty.");
                } else if (questionType.isEmpty()) {
                    errors.add("Row " + rowNumber + ": 'Question Type' is empty.");
                } else if (domain.isEmpty()) {
                    errors.add("Row " + rowNumber + ": 'Domain' is empty.");
                } else if (language.isEmpty()) {
                    errors.add("Row " + rowNumber + ": 'Language' is empty.");
                } else if (difficultyLevel.isEmpty()) {
                    errors.add("Row " + rowNumber + ": 'Difficulty Level' is empty.");
                }

                if(!List.of("Open Ended", "Multiple Choice", "Single Choice").contains(questionType)) {
                    errors.add("Row " + rowNumber + ": Invalid question type. Please use one of the following: " +
                            "Open Ended, Multiple Choice, Single Choice");
                }

                if(!List.of("Easy", "Medium", "Hard").contains(difficultyLevel)) {
                    errors.add("Row " + rowNumber + ": Invalid difficulty level. Please use one of the following: " +
                            "Easy, Medium, Hard");
                }

                if (("Single Choice".equals(questionType) || "Multiple Choice".equals(questionType)) && row.length < 14) {
                    errors.add("Row " + rowNumber + ": Missing fields for options.");
                }

                if(errors.isEmpty()) {
                    QuestionDTO questionDTO = new QuestionDTO();
                    questionDTO.setQuestionType(row[0]);
                    questionDTO.setDomain(row[1]);
                    questionDTO.setLanguage(row[2]);
                    questionDTO.setDifficultyLevel(row[3]);
                    questionDTO.setQuestionText(row[4]);
                    questionDTO.setCorrectAnswer(row[5]);
                    if ("Single Choice".equals(questionType) || "Multiple Choice".equals(questionType)) {
                        List<OptionDTO> optionDTOs = new ArrayList<>();
                        for (int i = 6; i < row.length; i += 2) {
                            String optionText = row[i].trim();
                            String isCorrectStr = row[i + 1].trim();

                            if (optionText.isEmpty()) {
                                errors.add("Row " + rowNumber + ": Option text is empty.");
                                continue;
                            }

                            boolean isCorrect = "true".equalsIgnoreCase(isCorrectStr);
                            OptionDTO optionDTO = new OptionDTO();
                            optionDTO.setOptionText(optionText);
                            optionDTO.setCorrect(isCorrect);
                            optionDTOs.add(optionDTO);
                        }
                        questionDTO.setOptions(optionDTOs);
                    }

                    if (errors.isEmpty()) {
                        addQuestion(questionDTO);
                    }
                }
                rowNumber++;
            }
        }catch (Exception e) {
            throw new IOException("Error processing .CSV file", e);
        }
        return errors;
    }

    public void exportQuestionsToCSV(HttpServletResponse response) throws IOException {
        List<Question> questions = questionRepository.findAll();
        List<QuestionDTO> questionDTOS = new ArrayList<>();

        String[] headers = {"Question Type", "Domain", "Language", "Difficulty Level", "Question Text", "Correct Answer", "Option 1 Text", "Option 1 Correct", "Option 2 Text", "Option 2 Correct", "Option 3 Text", "Option 3 Correct", "Option 4 Text", "Option 4 Correct"};
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=questions.csv");

        for (Question question : questions) {
            questionDTOS.add(convertToQuestionDTO(question));
        }


        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.writeNext(headers);

            for (QuestionDTO questionDTO : questionDTOS) {
                List<String> row = new ArrayList<>();
                row.add(questionDTO.getQuestionType());
                row.add(questionDTO.getDomain());
                row.add(questionDTO.getLanguage());
                row.add(questionDTO.getDifficultyLevel());
                row.add(questionDTO.getQuestionText());
                row.add(questionDTO.getCorrectAnswer());

                if (questionDTO.getOptions() != null) {
                    int optionCount = 1;
                    for (OptionDTO optionDTO : questionDTO.getOptions()) {
                        // Ensure the option count doesn't exceed 4 (if you want to limit to 4 options)
                        if (optionCount <= 4) {
                            row.add(optionDTO.getOptionText());
                            row.add(String.valueOf(optionDTO.isCorrect()));
                            optionCount++;
                        }
                    }
                }

                // Fill in empty columns for missing options (e.g., if there are fewer than 4 options)
                while (row.size() < 14) {
                    row.add(""); // Add empty values for missing options
                }

                writer.writeNext(row.toArray(new String[0]));
            }
        } catch (IOException e) {
            throw new IOException("Error exporting questions to CSV", e);
        }
    }

    public void toggleActiveStatus(Long id) {
        Optional<Question> optionalQuestion = questionRepository.findById(id);
        if (optionalQuestion.isPresent()) {
            Question question = optionalQuestion.get();
            question.setActive(!question.isActive());  // Toggle the status
            questionRepository.save(question);  // Save the updated status
        }
    }

    public void deleteQuestionById(Long id) {
        questionRepository.deleteById(id);
    }

    public QuestionDTO convertToQuestionDTO(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(question.getId());
        questionDTO.setQuestionType(question.getQuestionType());
        questionDTO.setQuestionText(question.getQuestionText());
        questionDTO.setDomain(question.getDomain());
        questionDTO.setLanguage(question.getLanguage());
        questionDTO.setCorrectAnswer(question.getCorrectAnswer());
        questionDTO.setDifficultyLevel(question.getDifficultyLevel());
        questionDTO.setOptions(optionService.convertByQuestionIdToOptionDTO(question.getId()));

        return questionDTO;
    }

    public List<String> getDomainsByLanguage(String language) {
        return questionRepository.findDomainsByLanguage(language);
    }

    public List<String> getQuestionTypesByLanguage(String language) {
        return questionRepository.findQuestionTypesByLanguage(language);
    }

    public List<String> getDifficultyLevelByLanguage(String language) {
        return questionRepository.findDifficultyLevelsByLanguage(language);
    }

    public List<String> getQuestionTypesByDomains(List<String> domains) {
        return questionRepository.findQuestionTypesByDomains(domains);

    }

    public List<String> getDifficultyLevelsByQuestionTypes(List<String> questionTypes) {
        return questionRepository.findDifficultyLevelsByQuestionTypes(questionTypes);
    }

    public List<String> getDifficultyLevelsByDomains(List<String> domains) {
        return questionRepository.findDifficultyLevelsByDomains(domains);
    }

    public void deleteAllQuestions() {
        questionRepository.deleteAll();
    }
}
