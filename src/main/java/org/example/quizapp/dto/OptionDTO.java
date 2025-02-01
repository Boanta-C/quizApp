package org.example.quizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OptionDTO {

    @NotBlank(message = "Option text cannot be empty")
    private String optionText;

    @NotNull(message = "Should be either correct or incorrect")
    private boolean isCorrect;

}
