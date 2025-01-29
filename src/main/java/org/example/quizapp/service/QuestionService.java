package org.example.quizapp.service;

import com.opencsv.CSVReader;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;

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
                                             String difficultyLevels) {
        Pageable pageable = PageRequest.of(0, numberOfQuestions);
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
            for (OptionDTO optionDTO : questionDTO.getOptions()) {
                Option option = new Option();
                option.setOptionText(optionDTO.getOptionText());
                option.setCorrect(optionDTO.isCorrect());
                option.setQuestion(question);
                optionRepository.save(option);
            }
        }
    }

    public List<String> uploadQuestionFromCSV(MultipartFile file) throws IOException {
        List<String> errors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> rows = csvReader.readAll();
            int rowNumber = 1;

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

                if(errors.isEmpty()) {
                    List<OptionDTO> optionDTOs = new ArrayList<>();
                    QuestionDTO questionDTO = new QuestionDTO();
                    questionDTO.setQuestionType(row[0]);
                    questionDTO.setDomain(row[1]);
                    questionDTO.setLanguage(row[2]);
                    questionDTO.setDifficultyLevel(row[3]);
                    questionDTO.setQuestionText(row[4]);
                    questionDTO.setCorrectAnswer(row[5]);
                    addQuestion(questionDTO);
                }
                rowNumber++;
            }
        }catch (Exception e) {
            throw new IOException();
        }
        return errors;
    }
}
