DROP TABLE IF EXISTS options;
DROP TABLE IF EXISTS questions;

CREATE TABLE questions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    question_type VARCHAR(255) NOT NULL,
    question_text TEXT NOT NULL,
    correct_answer TEXT NOT NULL,
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