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

    @Query("SELECT COUNT(q.id) FROM Question q " +
            "WHERE q.language = :language " +
            "AND (:domains IS NULL OR q.domain IN :domains)")
    Integer countNumberOfSelectedQuestions(@Param("language") String language,
                                           @Param("domains") List<String> domains);

    @Query("SELECT COUNT(q.id) FROM Question q " +
            "WHERE q.language = :language " +
            "AND (:domains IS NULL OR q.domain IN :domains) " +
            "AND (:questionTypes IS NULL OR q.questionType IN :questionTypes)")
    Integer countNumberOfSelectedQuestions(@Param("language") String language,
                                           @Param("domains") List<String> domains,
                                           @Param("questionTypes") List<String> questionTypes);

    @Query("SELECT COUNT(q.id) FROM Question q " +
            "WHERE q.language = :language " +
            "AND (:domains IS NULL OR q.domain IN :domains) " +
            "AND (:questionTypes IS NULL OR q.questionType IN :questionTypes) " +
            "AND (:difficultyLevel IS NULL OR q.difficultyLevel IN :difficultyLevel)")
    Integer countNumberOfSelectedQuestions(@Param("language") String language,
                                           @Param("domains") List<String> domains,
                                           @Param("questionTypes") List<String> questionTypes,
                                           @Param("difficultyLevel") List<String> difficultyLevel);



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
            "WHERE q.domain IN :domains " +
            "AND (q.language IS NULL OR q.language = :language)")
    List<String> getQuestionTypesByDomainsAndLanguage(List<String> domains, String language);

    @Query("SELECT DISTINCT q.difficultyLevel FROM Question q " +
            "WHERE q.questionType IN :questionTypes " +
            "AND (:domains IS NULL OR q.domain  IN :domains)")
    List<String> findDifficultyLevelsByQuestionTypesAndDomains(@Param("questionTypes") List<String> questionTypes, @Param("domains") List<String> domains);

    @Query("SELECT DISTINCT q.difficultyLevel FROM Question q " +
            "WHERE q.domain IN :domains " +
            "AND (q.language IS NULL OR q.language = :language)")
    List<String> findDifficultyLevelsByDomainsAndLanguage(List<String> domains, String language);

    @Query("SELECT DISTINCT q.domain FROM Question q ")
    List<String> findAllDomains();

    @Query("SELECT DISTINCT q.language FROM Question q ")
    List<String> findAllLanguages();
}
