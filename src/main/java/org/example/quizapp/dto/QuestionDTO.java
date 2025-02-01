package org.example.quizapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class QuestionDTO {

    private Long id;

    @NotBlank(message =  "Question text cannot be empty")
    private String questionText;

    @NotBlank(message = "Correct answer cannot be empty")
    private String correctAnswer;

    @NotNull(message = "Question type cannot be null")
    private String questionType = "";

    @NotNull(message = "Domain cannot be null")
    private String domain = "";

    @NotNull(message = "Language cannot be null")
    private String language = "";

    @NotNull(message = "difficultyLevel cannot be null")
    private String difficultyLevel = "";

    private boolean isActive;

    private List<OptionDTO> options;

}
