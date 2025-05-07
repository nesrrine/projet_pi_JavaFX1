package models;

import java.time.LocalDateTime;

public class LoginHistory {
    private int id;
    private int userId;
    private String userName; // Pour l'affichage, combinaison de prénom et nom
    private LocalDateTime loginTime;
    private String ipAddress;
    private boolean success;
    private String userAgent;

    public LoginHistory(int id, int userId, LocalDateTime loginTime, String ipAddress, boolean success, String userAgent) {
        this.id = id;
        this.userId = userId;
        this.loginTime = loginTime;
        this.ipAddress = ipAddress;
        this.success = success;
        this.userAgent = userAgent;
    }

    public LoginHistory(int id, int userId, String userName, LocalDateTime loginTime, String ipAddress, boolean success, String userAgent) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.loginTime = loginTime;
        this.ipAddress = ipAddress;
        this.success = success;
        this.userAgent = userAgent;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(LocalDateTime loginTime) {
        this.loginTime = loginTime;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    @Override
    public String toString() {
        return "LoginHistory{" +
                "id=" + id +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", loginTime=" + loginTime +
                ", ipAddress='" + ipAddress + '\'' +
                ", success=" + success +
                ", userAgent='" + userAgent + '\'' +
                '}';
    }
}
