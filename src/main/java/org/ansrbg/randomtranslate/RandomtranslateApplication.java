package org.ansrbg.randomtranslate;

import com.google.cloud.translate.Language;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.ansrbg.randomtranslate.repos.LanguageRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class RandomtranslateApplication {

    private final String googleApiKey;
    private final LanguageRepository languageRepository;

    public RandomtranslateApplication(@Value("${GOOGLE_API_KEY}") String googleApiKey, LanguageRepository languageRepository) {
        this.googleApiKey = googleApiKey;
        this.languageRepository = languageRepository;
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(RandomtranslateApplication.class, args);
    }

    @PostConstruct
    public void init() {
        System.setProperty("GOOGLE_API_KEY", this.googleApiKey);
        if (this.languageRepository.count() > 100) {
            return;
        }
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translate.LanguageListOption target = Translate.LanguageListOption.targetLanguage("en");
        translate.listSupportedLanguages(target)
                .forEach(L -> {
                    if (this.languageRepository.findFirstByCode(L.getCode()) == null) {
                        org.ansrbg.randomtranslate.models.Language language = new org.ansrbg.randomtranslate.models.Language();
                        language.setCode(L.getCode());
                        language.setName(L.getName());
                        this.languageRepository.saveAndFlush(language);
                    }
                });
    }
}
