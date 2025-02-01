package org.example.quizapp.service;

import org.example.quizapp.dto.OptionDTO;
import org.example.quizapp.entity.Option;
import org.example.quizapp.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptionService {

    private final OptionRepository optionRepository;

    @Autowired
    public OptionService(OptionRepository optionRepository) {
        this.optionRepository = optionRepository;
    }

    public List<Option> getOptionsByQuestionId(Long questionId) {
        return optionRepository.findByQuestionId(questionId);
    }

    public List<OptionDTO> getOptionsDTOByQuestionId(Long id) {
        return optionRepository.findByQuestionId(id)
                .stream()
                .map(option -> new OptionDTO(option.getOptionText(), option.isCorrect()))
                .toList();
    }
}
