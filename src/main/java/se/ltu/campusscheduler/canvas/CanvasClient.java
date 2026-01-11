package se.ltu.campusscheduler.canvas;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.Map;

@Component
public class CanvasClient {

    private final WebClient webClient;
    private final CanvasProperties props;
    private final ObjectMapper objectMapper;


    public CanvasClient(WebClient webClient, CanvasProperties props, ObjectMapper objectMapper) {
        this.webClient = webClient;
        this.props = props;
        this.objectMapper = objectMapper;
    }

    public String createCalendarEvent(Map<String, Object> calendarEvent) {
        if (props.getToken() == null || props.getToken().isBlank()) {
            throw new IllegalStateException("CANVAS_TOKEN saknas. Sätt environment variable CANVAS_TOKEN.");
        }
        if (props.getBaseUrl() == null || props.getBaseUrl().isBlank()) {
            throw new IllegalStateException("CANVAS_BASE_URL saknas (t.ex. https://canvas.ltu.se).");
        }

        String url = props.getBaseUrl().replaceAll("/+$", "") + "/api/v1/calendar_events";

        try {
            String body = webClient.post()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .bodyValue(Map.of("calendar_event", calendarEvent))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (body == null || body.isBlank()) {
                throw new IllegalArgumentException("Canvas returned empty response when creating calendar event.");
            }

            JsonNode node = objectMapper.readTree(body);

            if (node.has("id")) {
                return node.get("id").asText();
            }

            throw new IllegalArgumentException("Canvas response missing id. Body: " + body);

        } catch (WebClientResponseException e) {
            throw new IllegalArgumentException(
                    "Canvas API error: HTTP " + e.getStatusCode().value()
                            + " - " + e.getResponseBodyAsString(),
                    e
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Failed to parse Canvas create-event response: " + e.getMessage(),
                    e
            );
        }
    }

    public long getMyUserId() {
        String url = props.getBaseUrl().replaceAll("/+$", "") + "/api/v1/users/self/profile";

        try {
            String body = webClient.get()
                    .uri(url)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + props.getToken())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (body == null || body.isBlank()) {
                throw new IllegalArgumentException("Canvas profile response was empty.");
            }

            JsonNode node = objectMapper.readTree(body);
            if (node.has("id")) {
                return node.get("id").asLong();
            }

            throw new IllegalArgumentException("Canvas profile response did not contain 'id'. Body: " + body);
        } 
        
        catch (WebClientResponseException e) {
            throw new IllegalArgumentException("Canvas profile error: HTTP " + e.getStatusCode().value()
                    + " - " + e.getResponseBodyAsString(), e);
        } 
        catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse Canvas profile JSON: " + e.getMessage(), e);
        }
    }
}