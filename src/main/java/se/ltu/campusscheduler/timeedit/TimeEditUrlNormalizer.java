package se.ltu.campusscheduler.timeedit;

import org.springframework.stereotype.Component;

@Component
public class TimeEditUrlNormalizer {

    public String toJsonUrl(String input) {
        if (input == null) return null;
        String url = input.trim();

        if (url.contains(".html")) {
            return url.replace(".html", ".json");
        }

        // Om den redan är .json eller .xml låter vi den vara
        return url;
    }
}
