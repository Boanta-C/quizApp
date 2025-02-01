package repository;

import org.example.QuizAppApplication;
import org.example.quizapp.entity.Option;
import org.example.quizapp.entity.Question;
import org.example.quizapp.repository.OptionRepository;
import org.example.quizapp.repository.QuestionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ContextConfiguration(classes = QuizAppApplication.class)
public class RepositoryTest {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private OptionRepository optionRepository;

    @Test
    public void testFindQuestionsByDomain() {
        List<Question> scienceQuestions = questionRepository.findQuestionsByDomain("Computer Science");
        assertNotNull(scienceQuestions);
        assertEquals(1, scienceQuestions.size());
        assertEquals("Which of the following is the most popular programming language?", scienceQuestions.get(0).getQuestionText());
    }

    @Test
    public void testFindOptionsByQuestionId() {
        List<Option> options = optionRepository.findByQuestionId(2L); // Replace 1L with a valid ID
        assertNotNull(options);
        assertEquals(4, options.size()); // Adjust based on your test data
    }

}

