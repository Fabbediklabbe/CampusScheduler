package se.ltu.campusscheduler.timeedit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import se.ltu.campusscheduler.web.model.ScheduleEventForm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class TimeEditParser {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final ObjectMapper objectMapper;

    public TimeEditParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ScheduleEventForm> parseReservations(String rawJson) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            JsonNode reservations = root.path("reservations");
            List<ScheduleEventForm> result = new ArrayList<>();
            if (!reservations.isArray()) {
                return result;
            }

            for (JsonNode r : reservations) {
                ScheduleEventForm ev = new ScheduleEventForm();

                // id kan heta olika, så vi tar några möjliga
                String id = firstNonBlank(
                        textOrNull(r, "id"),
                        textOrNull(r, "reservationid"),
                        textOrNull(r, "bookingid")
                );
                ev.setSourceId(id != null ? id : "");

                LocalDateTime start = parseDateTime(
                        textOrNull(r, "startdate"),
                        textOrNull(r, "starttime")
                );
                LocalDateTime end = parseDateTime(
                        textOrNull(r, "enddate"),
                        textOrNull(r, "endtime")
                );

                ev.setStartIso(start != null ? start.toString() : "");
                ev.setEndIso(end != null ? end.toString() : "");

                // columns är ofta en array av objekt; vi försöker plocka ut meningsfull text
                List<String> colTexts = extractColumnTexts(r.path("columns"));
                String title = buildTitle(colTexts);
                ev.setTitle(title);

                // Fyll förslag men gör fälten redigerbara i UI
                ev.setLocation(suggestLocation(colTexts));
                ev.setDescription(suggestDescription(colTexts));

                result.add(ev);
            }

            return result;
        } catch (Exception e) {
            throw new IllegalArgumentException("Kunde inte parsa TimeEdit-JSON: " + e.getMessage(), e);
        }
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode v = node.get(field);
        if (v == null || v.isNull()) return null;
        String s = v.asText();
        return (s == null || s.isBlank()) ? null : s;
    }

    private static LocalDateTime parseDateTime(String yyyymmdd, String hhmm) {
        if (yyyymmdd == null || hhmm == null) return null;
        LocalDate d = LocalDate.parse(yyyymmdd, DATE_FMT);
        LocalTime t = LocalTime.parse(hhmm, TIME_FMT);
        return LocalDateTime.of(d, t);
    }

    private static List<String> extractColumnTexts(JsonNode columnsNode) {
        List<String> out = new ArrayList<>();
        if (columnsNode == null || !columnsNode.isArray()) return out;

        Iterator<JsonNode> it = columnsNode.elements();
        while (it.hasNext()) {
            JsonNode c = it.next();

            // Vanliga fält: "text" eller "value"
            String s = firstNonBlank(
                    textOrNull(c, "text"),
                    textOrNull(c, "value")
            );

            // Ibland kan column vara en ren sträng
            if (s == null && c.isTextual()) {
                s = c.asText();
            }

            if (s != null && !s.isBlank()) {
                out.add(s.trim());
            }
        }
        return out;
    }

    private static String buildTitle(List<String> colTexts) {
        // Heuristik: ta 1–2 första "rimliga" texterna
        List<String> cleaned = colTexts.stream()
                .filter(s -> s.length() >= 2)
                .toList();

        if (cleaned.isEmpty()) return "TimeEdit event";
        if (cleaned.size() == 1) return cleaned.get(0);
        return cleaned.get(0) + " - " + cleaned.get(1);
    }

    private static String suggestLocation(List<String> colTexts) {
        // Enkel heuristik: leta efter något som ser ut som lokal/rum/Zoom
        for (String s : colTexts) {
            String lower = s.toLowerCase();
            if (lower.contains("zoom") || lower.contains("teams")) return s;
            if (lower.matches(".*\\b[a-z]\\d{3,4}\\b.*")) return s; // typ A1301
        }
        return "";
    }

    private static String suggestDescription(List<String> colTexts) {
        // Spara övrig info i beskrivning som startförslag
        if (colTexts.isEmpty()) return "";
        return String.join("\n", colTexts);
    }

    private static String firstNonBlank(String... values) {
        if (values == null) return null;
        for (String v : values) {
            if (v != null && !v.isBlank()) return v;
        }
        return null;
    }
}
