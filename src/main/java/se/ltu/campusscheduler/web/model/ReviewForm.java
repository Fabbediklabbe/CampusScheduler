package se.ltu.campusscheduler.web.model;

import java.util.ArrayList;
import java.util.List;

public class ReviewForm {

    private String contextCode;
    private List<ScheduleEventForm> events = new ArrayList<>();
    private boolean exportToMyCalendar;

    public String getContextCode() {
        return contextCode;
    }

    public void setContextCode(String contextCode) {
        this.contextCode = contextCode;
    }

    public List<ScheduleEventForm> getEvents() {
        return events;
    }

    public void setEvents(List<ScheduleEventForm> events) {
        this.events = events;
    }

    public boolean isExportToMyCalendar() {
        return exportToMyCalendar;
    }

    public void setExportToMyCalendar(boolean exportToMyCalendar) {
        this.exportToMyCalendar = exportToMyCalendar;
    }
}
