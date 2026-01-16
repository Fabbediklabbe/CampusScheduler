package se.ltu.campusscheduler.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import se.ltu.campusscheduler.canvas.CanvasClient;
import se.ltu.campusscheduler.canvas.CanvasProperties;
import se.ltu.campusscheduler.web.model.ExportResultRow;
import se.ltu.campusscheduler.web.model.ReviewForm;
import se.ltu.campusscheduler.web.model.ScheduleEventForm;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ExportController {

    private final CanvasClient canvasClient;
    private final CanvasProperties canvasProps;

    public ExportController(CanvasClient canvasClient, CanvasProperties canvasProps) {
        this.canvasClient = canvasClient;
        this.canvasProps = canvasProps;
    }

    @PostMapping("/export")
    public String export(@ModelAttribute("reviewForm") ReviewForm reviewForm, Model model) {
        String contextCode;

        if (reviewForm.isExportToMyCalendar()) {
            long myUserId = canvasClient.getMyUserId();
            contextCode = "user_" + myUserId;
        } else {
            contextCode = normalizeContextCode(reviewForm.getContextCode());
        }

        if (!reviewForm.isExportToMyCalendar() && (contextCode == null || contextCode.isBlank())) {
            model.addAttribute("contextCode", "");
            model.addAttribute("results", List.of());
            model.addAttribute("errorMessage", "Context code is required when exporting to a course calendar.");
            return "export-result";
        }


        List<ExportResultRow> rows = new ArrayList<>();
        for (ScheduleEventForm ev : reviewForm.getEvents()) {
            ExportResultRow row = new ExportResultRow(ev.getTitle(), ev.getStartIso(), ev.getEndIso());
            rows.add(row);

            try {
                Map<String, Object> payload = toCanvasCalendarEvent(contextCode, ev);
                String canvasId = canvasClient.createCalendarEvent(payload);
                row.setSuccess(true);
                row.setCanvasEventId(canvasId);
            } catch (Exception ex) {
                row.setSuccess(false);
                row.setError(ex.getMessage());
            }
        }

        model.addAttribute("contextCode", contextCode);
        model.addAttribute("results", rows);
        return "export-result";
    }

    private Map<String, Object> toCanvasCalendarEvent(String contextCode, ScheduleEventForm ev) {
        // Konvertera start/end till timezone-aware ISO (Europe/Stockholm)
        ZoneId zone = ZoneId.of(canvasProps.getTimezone());

        ZonedDateTime start = parseLocalDateTime(ev.getStartIso()).atZone(zone);
        ZonedDateTime end = parseLocalDateTime(ev.getEndIso()).atZone(zone);

        Map<String, Object> ce = new HashMap<>();
        ce.put("context_code", contextCode);
        ce.put("title", ev.getTitle());
        ce.put("start_at", start.toOffsetDateTime().toString());
        ce.put("end_at", end.toOffsetDateTime().toString());

        if (ev.getLocation() != null && !ev.getLocation().isBlank()) {
            ce.put("location_name", ev.getLocation());
        }
        if (ev.getDescription() != null && !ev.getDescription().isBlank()) {
            ce.put("description", ev.getDescription());
        }
        return ce;
    }

    private static LocalDateTime parseLocalDateTime(String value) {
        return LocalDateTime.parse(value.trim());
    }

    private static String normalizeContextCode(String raw) {
        if (raw == null) return "";
        String s = raw.trim();
        if (s.isBlank()) return "";

        // Om användaren skriver bara siffror, anta course_<id>
        if (s.matches("\\d+")) {
            return "course_" + s;
        }
        return s;
    }
}