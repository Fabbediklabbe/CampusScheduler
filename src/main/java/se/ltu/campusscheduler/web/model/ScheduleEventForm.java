package se.ltu.campusscheduler.web.model;

public class ScheduleEventForm {

    private String sourceId;
    private String title;

    private String startIso;
    private String endIso;

    private String location;
    private String description;

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartIso() {
        return startIso;
    }

    public void setStartIso(String startIso) {
        this.startIso = startIso;
    }

    public String getEndIso() {
        return endIso;
    }

    public void setEndIso(String endIso) {
        this.endIso = endIso;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
