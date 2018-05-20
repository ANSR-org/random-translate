package org.ansrbg.randomtranslate.repos;

import org.ansrbg.randomtranslate.models.Translation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TranslationRepository extends JpaRepository<Translation, Long> {

    @Query("SELECT t FROM Translation t WHERE t.fromLanguage = :from AND t.toLanguage = :to AND t.phrase LIKE %:phrase% ORDER BY RAND()")
    List<Translation> findRandom(@Param("from") String from, @Param("to") String to, @Param("phrase") String phrase);

}
