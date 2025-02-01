package service;

import org.example.QuizAppApplication;
import org.example.quizapp.entity.Option;
import org.example.quizapp.repository.OptionRepository;
import org.example.quizapp.service.OptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.stereotype.Service;
import org.springframework.test.context.ContextConfiguration;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Service
@ContextConfiguration(classes = QuizAppApplication.class)
public class OptionServiceTest {

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private OptionService optionService;

    private Option sampleOption;

    @BeforeEach
    public void setup() {
        sampleOption = new Option();
        sampleOption.setId(1L);
        sampleOption.setOptionText("4");
        sampleOption.setCorrect(true);
    }

    @Test
    public void testGetOptionsByQuestionId() {
        List<Option> options = Collections.singletonList(sampleOption);
        when(optionRepository.findByQuestionId(1L)).thenReturn(options);

        List<Option> result = optionService.getOptionsByQuestionId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("4", result.get(0).getOptionText());
        verify(optionRepository, times(1)).findByQuestionId(1L);
    }
}
