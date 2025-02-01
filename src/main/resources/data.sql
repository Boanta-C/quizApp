INSERT INTO Questions (question_type, question_text, correct_answer, question_domain, question_language, difficulty_level, is_active)
VALUES ('Open-Ended', 'Explain what a REST API is.', 'A set of rules and conventions for building and interacting with web services', 'Web Development', 'English', 'Medium', true);

INSERT INTO Questions (question_type, question_text, correct_answer, question_domain, question_language, difficulty_level, is_active)
VALUES ('Multiple-Choice', 'Which of the following is the most popular programming language?', 'A,B', 'Computer Science', 'English', 'Medium', true);

SET @last_question_id = LAST_INSERT_ID();

INSERT INTO Options (question_id, is_correct, options_text)
VALUES
    (@last_question_id, true, 'A. Python', true),
    (@last_question_id, true, 'B. Java', true),
    (@last_question_id, false, 'C. HTML', false),
    (@last_question_id, false, 'D. CSS', false);