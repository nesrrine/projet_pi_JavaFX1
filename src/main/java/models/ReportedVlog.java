package models;

import java.time.LocalDateTime;

public class ReportedVlog {
    private int id;
    private int vlogId;
    private int userId;
    private LocalDateTime reportDate;

    public ReportedVlog(int vlogId, int userId) {
        this.vlogId = vlogId;
        this.userId = userId;
        this.reportDate = LocalDateTime.now();  // Date du signalement
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVlogId() {
        return vlogId;
    }

    public void setVlogId(int commentId) {
        this.vlogId = commentId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public LocalDateTime getReportDate() {
        return reportDate;
    }

    public void setReportDate(LocalDateTime reportDate) {
        this.reportDate = reportDate;
    }
}
