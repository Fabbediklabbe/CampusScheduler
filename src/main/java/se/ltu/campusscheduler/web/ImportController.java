package se.ltu.campusscheduler.web;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import se.ltu.campusscheduler.timeedit.TimeEditClient;
import se.ltu.campusscheduler.timeedit.TimeEditParser;
import se.ltu.campusscheduler.timeedit.TimeEditUrlNormalizer;
import se.ltu.campusscheduler.web.model.ImportForm;
import se.ltu.campusscheduler.web.model.ReviewForm;

@Controller
public class ImportController {

    private final TimeEditUrlNormalizer normalizer;
    private final TimeEditClient client;
    private final TimeEditParser parser;

    public ImportController(TimeEditUrlNormalizer normalizer, TimeEditClient client, TimeEditParser parser) {
        this.normalizer = normalizer;
        this.client = client;
        this.parser = parser;
    }

    @GetMapping("/")
    public String home(Model model) {
        if (!model.containsAttribute("importForm")) {
            model.addAttribute("importForm", new ImportForm());
        }
        return "home";
    }

    @PostMapping("/import")
    public String importSchedule(
            @Valid @ModelAttribute("importForm") ImportForm importForm,
            BindingResult bindingResult,
            Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "home";
        }

        String jsonUrl = normalizer.toJsonUrl(importForm.getTimeEditUrl());
        String rawJson = client.fetchScheduleJson(jsonUrl);

        ReviewForm reviewForm = new ReviewForm();
        reviewForm.setContextCode(importForm.getContextCode());
        reviewForm.setEvents(parser.parseReservations(rawJson));

        model.addAttribute("reviewForm", reviewForm);
        return "review";
    }
}
