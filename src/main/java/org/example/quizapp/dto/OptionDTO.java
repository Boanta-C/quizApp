package org.example.quizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionDTO {

    @NotBlank(message = "Option text cannot be empty")
    private String optionText;

    @NotNull(message = "Should be either correct or incorrect")
    private boolean isCorrect;

}
