package org.example.quizapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "questions")
@Getter
@Setter
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "question_type", nullable = false)
    private String questionType;
    @Column(name = "question_text", nullable = false)
    private String questionText;
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;
    @Column(name = "question_domain", nullable = false)
    private String domain;
    @Column(name = "question_language", nullable = false)
    private String language;
    @Column(name = "difficulty_level")
    private String difficultyLevel;
    @Column(name = "is_active", nullable = false)
    private boolean isActive;

}
