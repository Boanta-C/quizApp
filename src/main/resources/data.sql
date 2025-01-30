DROP TABLE IF EXISTS options;
DROP TABLE IF EXISTS questions;

CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_type VARCHAR(255) NOT NULL,
    question_text TEXT NOT NULL,
    correct_answer VARCHAR(255) NOT NULL,
    question_domain VARCHAR(255) NOT NULL,
    question_language VARCHAR(255) NOT NULL,
    difficulty_level VARCHAR(255),
    is_active BOOLEAN NOT NULL
);

CREATE TABLE options (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_id BIGINT NOT NULL,
    option_text TEXT NOT NULL,
    is_correct BOOLEAN NOT NULL,
    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
);

INSERT INTO Questions (question_type, question_text, correct_answer, question_domain, question_language, difficulty_level, is_active)
VALUES
    ('Open Ended', 'Explain what a REST API is.', 'A set of rules and conventions for building and interacting with web services', 'Web', 'English', 'Medium', true),
    ('Multiple Choice', 'Which of the following is the most popular programming language?', 'A,B', 'Programming', 'English', 'Medium', true);

INSERT INTO Options (question_id, is_correct, option_text)
VALUES
    (2, false, 'A. Python'),
    (2, true, 'B. Java'),
    (2, false, 'C. HTML'),
    (2, false, 'D. CSS');