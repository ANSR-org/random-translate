package org.ansrbg.randomtranslate.repos;

import org.ansrbg.randomtranslate.models.Language;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LanguageRepository extends JpaRepository<Language, Long> {
    Language findFirstByCode(String code);
    Language findFirstByName(String name);
}
