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

    public List<String> getAllDomains() {
        return questionRepository.findAllDomains();
    }

    public List<String> getAllLanguages() {
        return questionRepository.findAllLanguages();
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

    public List<String> getQuestionTypesByDomainsAndLanguage(List<String> domains, String language) {
        return questionRepository.getQuestionTypesByDomainsAndLanguage(domains, language);
    }

    public List<String> getDifficultyLevelsByDomainsAndLanguage(List<String> domains, String language) {
        return questionRepository.findDifficultyLevelsByDomainsAndLanguage(domains, language);
    }

    public List<String> getDifficultyLevelsByQuestionTypes(List<String> questionTypes, List<String> domains) {
        return questionRepository.findDifficultyLevelsByQuestionTypesAndDomains(questionTypes, domains);
    }

    public Integer getCountNumberOfSelectedQuestions(String language,
                                                     List<String> domains,
                                                     List<String> questionTypes,
                                                     List<String> difficultyLevel) {
        return questionRepository.countNumberOfSelectedQuestions(language, domains, questionTypes, difficultyLevel);
    }

    public void deleteAllQuestions() {
        questionRepository.deleteAll();
    }

    public void deleteQuestionById(Long id) {
        questionRepository.deleteById(id);
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
        List<QuestionDTO> validQuestions = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> rows = csvReader.readAll();
            int rowNumber = 2;

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
                                errors.add("Row " + rowNumber + ": an option text is empty.");
                                continue;
                            }

                            if (isCorrectStr.isEmpty()) {
                                errors.add("Row " + rowNumber + ": missing validation if answer is correct");
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
                        validQuestions.add(questionDTO);
                    }
                rowNumber++;
            }
            if (!errors.isEmpty()) {
                return errors;
            }

            for (QuestionDTO validQuestion : validQuestions) {
                addQuestion(validQuestion);
            }
        } catch (Exception e) {
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
            questionDTOS.add(convertQuestionToDto(question));
        }


        try (CSVWriter writer = new CSVWriter(new OutputStreamWriter(response.getOutputStream()))) {
            writer.writeNext(headers);

            for (QuestionDTO questionDTO : questionDTOS) {
                List<String> row = getStrings(questionDTO);

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

    public QuestionDTO convertQuestionToDto(Question question) {
        QuestionDTO questionDTO = new QuestionDTO();
        questionDTO.setId(question.getId());
        questionDTO.setQuestionText(question.getQuestionText());
        questionDTO.setCorrectAnswer(question.getCorrectAnswer());
        questionDTO.setQuestionType(question.getQuestionType());
        questionDTO.setDomain(question.getDomain());
        questionDTO.setLanguage(question.getLanguage());
        questionDTO.setDifficultyLevel(question.getDifficultyLevel());
        questionDTO.setActive(question.isActive());

        if (!questionDTO.getQuestionType().equals("Open Ended")) {
            List<OptionDTO> optionDTOs = optionService.getOptionsDTOByQuestionId(question.getId());
            questionDTO.setOptions(optionDTOs);
        }
        return questionDTO;
    }

    private static List<String> getStrings(QuestionDTO questionDTO) {
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
                if (optionCount <= 4) {
                    row.add(optionDTO.getOptionText());
                    row.add(String.valueOf(optionDTO.isCorrect()));
                    optionCount++;
                }
            }
        }
        while (row.size() < 14) {
            row.add("");
        }
        return row;
    }
}
