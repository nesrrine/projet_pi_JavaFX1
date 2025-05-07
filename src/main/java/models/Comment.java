package models;

public class Comment {
    private int id;
    private int vlogId;
    private int userId;
    private String content;
    private String timestamp;
    private int parentId;  // Le parentId pour les commentaires imbriqués
    private boolean isReported; // Champ ajouté pour le signalement

    // Constructeur pour les commentaires principaux
    public Comment(int vlogId, int userId, String content) {
        this(vlogId, userId, content, 0);  // parentId est 0 pour un commentaire principal
    }

    // Constructeur pour les réponses aux commentaires
    public Comment(int vlogId, int userId, String content, int parentId) {
        this.vlogId = vlogId;
        this.userId = userId;
        this.content = content;
        this.timestamp = String.valueOf(System.currentTimeMillis());  // Timestamp en millisecondes
        this.parentId = parentId;  // Le parentId pour les réponses
    }

    // Getters et Setters
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }
    public boolean isReported() { return isReported; }
    public void setReported(boolean reported) { this.isReported = reported; }
}
