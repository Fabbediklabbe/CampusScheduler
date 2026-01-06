package se.ltu.campusscheduler.timeedit;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class TimeEditClient {

    private final WebClient webClient;

    public TimeEditClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public String fetchScheduleJson(String url) {
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}
