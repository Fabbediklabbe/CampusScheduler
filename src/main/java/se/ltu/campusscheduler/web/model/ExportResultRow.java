package se.ltu.campusscheduler.web.model;

public class ExportResultRow {

    private String title;
    private String startIso;
    private String endIso;

    private boolean success;
    private String canvasEventId;
    private String error;

    public ExportResultRow() {}

    public ExportResultRow(String title, String startIso, String endIso) {
        this.title = title;
        this.startIso = startIso;
        this.endIso = endIso;
    }

    public String getTitle() {
        return title;
    }

    public String getStartIso() {
        return startIso;
    }

    public String getEndIso() {
        return endIso;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getCanvasEventId() {
        return canvasEventId;
    }

    public void setCanvasEventId(String canvasEventId) {
        this.canvasEventId = canvasEventId;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}