package models;

import java.time.LocalDateTime;
import java.util.List;

public class Vlog {
    private int id;
    private String content;
    private String image;
    private String video;
    private LocalDateTime createdAt;
    private int authorId;
    private boolean isReported; // 🚩 Nouveau champ


    public Vlog() {}

    public Vlog(String content, String image, String video, LocalDateTime createdAt, int authorId) {
        this.content = content;
        this.image = image;
        this.video = video;
        this.createdAt = createdAt;
        this.authorId = authorId;
    }

    public Vlog(String content, String image, String video, int authorId) {
        this.content = content;
        this.image = image;
        this.video = video;
        this.authorId = authorId;
    }

    public Vlog(int id, String content, String image, String video, LocalDateTime createdAt, int authorId) {
        this.id = id;
        this.content = content;
        this.image = image;
        this.video = video;
        this.createdAt = createdAt;
        this.authorId = authorId;
    }

    // Getters and Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public int getAuthorId() { return authorId; }
    public void setAuthorId(int authorId) { this.authorId = authorId; }


    @Override
    public String toString() {
        return "Vlog{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", image='" + image + '\'' +
                ", video='" + video + '\'' +
                ", createdAt=" + createdAt +
                ", authorId=" + authorId +
                '}';
    }

    public boolean isReported() { return isReported; }
    public void setReported(boolean reported) { isReported = reported; }
}
