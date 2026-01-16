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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TimeEditParser {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATE_TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ObjectMapper objectMapper;

    public TimeEditParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<ScheduleEventForm> parseReservations(String rawJson, boolean includeHolidays) {
        try {
            JsonNode root = objectMapper.readTree(rawJson);

            Map<String, Integer> headerIndex = buildHeaderIndex(root.path("columnheaders"));

            JsonNode reservations = root.path("reservations");
            List<ScheduleEventForm> result = new ArrayList<>();
            if (!reservations.isArray()) {
                return result;
            }

            for (JsonNode r : reservations) {
                List<String> cols = extractColumnsAsList(r.path("columns"));

                String activity = getCol(cols, headerIndex, "Aktivitet");
                String place = getCol(cols, headerIndex, "Plats, Lokal");
                String courseCode = getCol(cols, headerIndex, "Kurskod");
                String courseName = getCol(cols, headerIndex, "Kursnamn");
                String staffStudents = getCol(cols, headerIndex, "Anställd, Student");
                String comment = getCol(cols, headerIndex, "Kommentar, Kommentar");
                String meetingLink = getCol(cols, headerIndex, "Möteslänk");
                String campus = getCol(cols, headerIndex, "Campus");

                // Filtrera bort helgdagar/icke-kurs-händelser
                boolean isProbablyHoliday = (courseCode == null || courseCode.isBlank())
                        && (place == null || place.isBlank())
                        && (activity != null && !activity.isBlank());
                if (!includeHolidays && isProbablyHoliday) {
                    continue;
                }


                ScheduleEventForm ev = new ScheduleEventForm();

                // id
                String id = textOrNull(r, "id");
                ev.setSourceId(id != null ? id : "");

                // start/end
                LocalDateTime start = parseDateTime(textOrNull(r, "startdate"), textOrNull(r, "starttime"));
                LocalDateTime end = parseDateTime(textOrNull(r, "enddate"), textOrNull(r, "endtime"));

                ev.setStartIso(start != null ? start.toString() : "");
                ev.setEndIso(end != null ? end.toString() : "");


                // title
                String title = (activity != null ? activity : "TimeEdit event");
                if (place != null && !place.isBlank()) {
                    title = title + " - " + place;
                }
                ev.setTitle(title);

                // location
                ev.setLocation(place != null ? place : "");

                // description
                StringBuilder desc = new StringBuilder();
                appendLabeledLine(desc, courseCode, courseName);              // "D0023E Forskningsmetoder..."
                appendLabeledLine(desc, "Campus", campus);                   // "Campus: Luleå"
                appendLabeledLine(desc, "Staff/Students", staffStudents);    // "Staff/Students: ..."
                appendLabeledLine(desc, "Comment", comment);                 // "Comment: ..."
                appendLabeledLine(desc, "Meeting link", meetingLink);        // "Meeting link: https://..."
                ev.setDescription(desc.toString().trim());

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

    private static LocalDateTime parseDateTime(String dateRaw, String timeRaw) {
        if (dateRaw == null || timeRaw == null) return null;
        LocalDate d = LocalDate.parse(dateRaw.trim(), DATE_FMT);
        LocalTime t = LocalTime.parse(timeRaw.trim(), TIME_FMT);
        return LocalDateTime.of(d, t);
    }

    private static Map<String, Integer> buildHeaderIndex(JsonNode headersNode) {
        Map<String, Integer> map = new HashMap<>();
        if (headersNode != null && headersNode.isArray()) {
            for (int i = 0; i < headersNode.size(); i++) {
                String h = headersNode.get(i).asText();
                if (h != null && !h.isBlank()) {
                    map.put(h.trim(), i);
                }
            }
        }
        return map;
    }

    private static List<String> extractColumnsAsList(JsonNode columnsNode) {
        List<String> out = new ArrayList<>();
        if (columnsNode == null || !columnsNode.isArray()) return out;
        for (int i = 0; i < columnsNode.size(); i++) {
            JsonNode c = columnsNode.get(i);
            out.add(c == null || c.isNull() ? "" : c.asText(""));
        }
        return out;
    }

    private static String getCol(List<String> cols, Map<String, Integer> headerIndex, String header) {
        Integer idx = headerIndex.get(header);
        if (idx == null) return null;
        if (idx < 0 || idx >= cols.size()) return null;
        String v = cols.get(idx);
        return (v == null || v.isBlank()) ? null : v.trim();
    }

    private static void appendLine(StringBuilder sb, String value1, String value2) {
        if ((value1 == null || value1.isBlank()) && (value2 == null || value2.isBlank())) return;
        if (value1 != null && !value1.isBlank() && value2 != null && !value2.isBlank()) {
            sb.append(value1).append(" ").append(value2).append("\n");
        } else if (value1 != null && !value1.isBlank()) {
            sb.append(value1).append("\n");
        } else {
            sb.append(value2).append("\n");
        }
    }

    private static void appendLabeledLine(StringBuilder sb, String label, String value) {
        if (value == null || value.isBlank()) return;
        sb.append(label).append(": ").append(value).append("\n");
    }
}
