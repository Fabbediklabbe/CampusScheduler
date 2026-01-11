package se.ltu.campusscheduler.timeedit;

public class TimeEditResponse {
    private final int status;
    private final String contentType;
    private final String body;

    public TimeEditResponse(int status, String contentType, String body) {
        this.status = status;
        this.contentType = contentType;
        this.body = body;
    }

    public int getStatus() { return status; }
    public String getContentType() { return contentType; }
    public String getBody() { return body; }
}
