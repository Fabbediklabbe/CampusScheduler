package se.ltu.campusscheduler.web.model;

import jakarta.validation.constraints.NotBlank;

public class ImportForm {

    private boolean includeHolidays;

    public boolean isIncludeHolidays() {
        return includeHolidays;
    }

    public void setIncludeHolidays(boolean includeHolidays) {
        this.includeHolidays = includeHolidays;
    }

    @NotBlank(message = "TimeEdit-länk måste anges")
    private String timeEditUrl;

    private String contextCode;

    public String getTimeEditUrl() {
        return timeEditUrl;
    }

    public void setTimeEditUrl(String timeEditUrl) {
        this.timeEditUrl = timeEditUrl;
    }

    public String getContextCode() {
        return contextCode;
    }

    public void setContextCode(String contextCode) {
        this.contextCode = contextCode;
    }
}