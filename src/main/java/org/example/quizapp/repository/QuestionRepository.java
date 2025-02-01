package org.example.quizapp.repository;

import org.example.quizapp.entity.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.domain = :domain")
    List<Question> findQuestionsByDomain(@Param("domain") String domain);

    @Query("SELECT q FROM Question q WHERE q.isActive = true")
    List<Question> findActiveQuestions();

    @Query("SELECT DISTINCT q.language FROM Question q")
    List<String> findDistinctLanguages();

    @Query("SELECT DISTINCT q.domain FROM Question q")
    List<String> findDistinctDomains();

    @Query("SELECT DISTINCT q.questionType FROM Question q")
    List<String> findDistinctQuestionTypes();

    @Query("SELECT DISTINCT q.difficultyLevel FROM Question q")
    List<String> findDistinctDifficultyLevels();

    @Query("SELECT q FROM Question q " +
            "WHERE q.language = :language " +
            "AND q.domain IN :domains " +
            "AND q.questionType IN :questionTypes " +
            "AND (:difficultyLevel IS NULL OR q.difficultyLevel IN :difficultyLevel) " +
            "ORDER BY RAND()")
    List<Question> findRandomQuestions(@Param("language") String language,
                                       @Param("domains") List<String> domains,
                                       @Param("questionTypes") List<String> questionTypes,
                                       @Param("difficultyLevel") List<String> difficultyLevel,
                                       Pageable pageable);

    @Query("SELECT DISTINCT q.domain FROM Question q " +
            "WHERE q.language = :language")
    List<String> findDomainsByLanguage(String language);

    @Query("SELECT DISTINCT q.questionType FROM Question q " +
            "WHERE q.language = :language")
    List<String> findQuestionTypesByLanguage(String language);

    @Query("SELECT DISTINCT q.difficultyLevel FROM Question q " +
            "WHERE q.language = :language")
    List<String> findDifficultyLevelsByLanguage(String language);

    @Query("SELECT DISTINCT q.questionType FROM Question q " +
            "WHERE q.domain IN :domains")
    List<String> findQuestionTypesByDomains(List<String> domains);

    @Query("SELECT DISTINCT q.difficultyLevel FROM Question q " +
            "WHERE q.questionType IN :questionTypes")
    List<String> findDifficultyLevelsByQuestionTypes(List<String> questionTypes);

    @Query("SELECT DISTINCT q.difficultyLevel FROM Question q " +
            "WHERE q.domain IN :domains")
    List<String> findDifficultyLevelsByDomains(List<String> domains);
}
