package org.ansrbg.randomtranslate.controllers;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import org.ansrbg.randomtranslate.models.Translation;
import org.ansrbg.randomtranslate.repos.LanguageRepository;
import org.ansrbg.randomtranslate.repos.TranslationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TranslationController {

    private final TranslationRepository translationRepository;
    private final LanguageRepository languageRepository;


    public TranslationController(TranslationRepository translationRepository, LanguageRepository languageRepository) {
        this.translationRepository = translationRepository;
        this.languageRepository = languageRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("langs", this.languageRepository.findAll());
        if (!model.containsAttribute("result")) {
            model.addAttribute("result", new Translation());
        }
        return "index";
    }

    @GetMapping(value = "/translate")
    public String translate(@RequestParam("from") String from,
                            @RequestParam("to") String to,
                            @RequestParam("phrase") String phrase,
                            Model model) {
        List<Translation> translations = this.translationRepository.findRandom(from, to, phrase);
        Translation translation = translations.size() > 0 ? translations.get(0) : null;
        if (translation == null) {
            Translate translate = TranslateOptions.getDefaultInstance().getService();

            com.google.cloud.translate.Translation externalTranslation =
                    translate.translate(
                            phrase,
                            Translate.TranslateOption.sourceLanguage(from),
                            Translate.TranslateOption.targetLanguage(to));
            translation = new Translation();
            translation.setFromLanguage(from);
            translation.setToLanguage(to);
            translation.setPhrase(phrase);
            translation.setResult(externalTranslation.getTranslatedText());
            this.translationRepository.saveAndFlush(translation);
        }
        model.addAttribute("langs", this.languageRepository.findAll());
        model.addAttribute("result", translation);
        return "index";
    }

    @GetMapping("/suggest")
    public String suggest(Model model) {
        model.addAttribute("langs", this.languageRepository.findAll());
        model.addAttribute("translation", new Translation());
        return "suggest";
    }

    @PostMapping("/suggest")
    public String suggest(@ModelAttribute Translation translation, RedirectAttributes attributes) {
        this.translationRepository.saveAndFlush(translation);
        attributes.addFlashAttribute("result", translation);
        return "redirect:/";
    }


}
