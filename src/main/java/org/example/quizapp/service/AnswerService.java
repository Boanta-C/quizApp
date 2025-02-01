package org.example.quizapp.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnswerService {

    public List<String> processAnswer(List<String> userAnswers, String answer, String questionType, int currentIndex) {

        if (userAnswers == null) {
            userAnswers = new ArrayList<>();
        }

        String processedAnswer = answer;
        if ("Single Choice".equals(questionType)) {
            processedAnswer = (answer.split("\\.")[0]);
        } else if ("Multiple Choice".equals(questionType)) {
            processedAnswer = Arrays.stream(answer.split(","))
                    .map(a -> a.split("\\.")[0])
                    .collect(Collectors.joining(", "));
        }

        if (currentIndex < userAnswers.size()) {
            userAnswers.set(currentIndex, processedAnswer); // Update existing answer
        } else {
            userAnswers.add(processedAnswer); // Add new answer
        }
        return userAnswers;
    }

}
