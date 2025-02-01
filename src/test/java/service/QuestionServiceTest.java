package service;

import org.example.QuizAppApplication;
import org.example.quizapp.entity.Question;
import org.example.quizapp.repository.QuestionRepository;
import org.example.quizapp.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ContextConfiguration(classes = QuizAppApplication.class)
public class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @InjectMocks
    private QuestionService questionService;

    private Question sampleQuestion;

    @BeforeEach
    public void setup() {
        sampleQuestion = new Question();
        sampleQuestion.setId(1L);
        sampleQuestion.setQuestionText("What is 2+2?");
        sampleQuestion.setCorrectAnswer("4");
        sampleQuestion.setQuestionType("Single-Choice");
        sampleQuestion.setDomain("Math");
        sampleQuestion.setLanguage("English");
        sampleQuestion.setDifficultyLevel("Easy");
        sampleQuestion.setActive(true);
    }

    @Test
    public void testGetAllQuestions() {
        List<Question> mockQuestions = Collections.singletonList(sampleQuestion);
        when(questionRepository.findAll()).thenReturn(mockQuestions);

        List<Question> result = questionService.getAllQuestions();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("What is 2+2?", result.get(0).getQuestionText());
        verify(questionRepository, times(1)).findAll();
    }

    @Test
    public void testGetQuestionById() {
        when(questionRepository.findById(1L)).thenReturn(Optional.of(sampleQuestion));

        Optional<Question> result = questionService.getQuestionById(1L);

        assertTrue(result.isPresent());
        assertEquals("What is 2+2?", result.get().getQuestionText());
        verify(questionRepository, times(1)).findById(1L);
    }
}
