package models;

public class Like {
    private int id;
    private int vlogId;
    private int userId;
    private boolean isLike;

    // Constructor with all parameters
    public Like() {
        // Default values can be set here if needed, e.g.
        // this.id = 0;
        // this.vlogId = 0;
        // this.userId = 0;
        // this.isLike = false;
    }
    public Like(int id, int vlogId, int userId, boolean isLike) {
        this.id = id;
        this.vlogId = vlogId;
        this.userId = userId;
        this.isLike = isLike;
    }

    // Constructor without ID (typically for inserting new Like records)
    public Like(int vlogId, int userId, boolean isLike) {
        this.vlogId = vlogId;
        this.userId = userId;
        this.isLike = isLike;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVlogId() {
        return vlogId;
    }

    public void setVlogId(int vlogId) {
        this.vlogId = vlogId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public boolean isLike() {
        return isLike;
    }

    // Renamed the setter to follow convention
    public void setIsLike(boolean isLike) {
        this.isLike = isLike;
    }
}
