package se.ltu.campusscheduler.timeedit;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class TimeEditClient {

    private final WebClient webClient;

    public TimeEditClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public TimeEditResponse fetch(String url) {
        try {
            var entity = webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(String.class)
                    .block();

            String contentType = entity.getHeaders().getContentType() != null
                    ? entity.getHeaders().getContentType().toString()
                    : null;

            return new TimeEditResponse(entity.getStatusCode().value(), contentType, entity.getBody());

        } catch (WebClientResponseException e) {
            String contentType = e.getHeaders().getContentType() != null
                    ? e.getHeaders().getContentType().toString()
                    : null;
            return new TimeEditResponse(e.getStatusCode().value(), contentType, e.getResponseBodyAsString());
        }
    }
}
