package models;


public class Reaction {
    private int id;
    private int vlogId;
    private int userId;
    private ReactionType type;

    public Reaction(int vlogId, int userId, ReactionType type) {
        this.vlogId = vlogId;
        this.userId = userId;
        this.type = type;
    }

    // Getters et Setters
    public int getVlogId() { return vlogId; }
    public int getUserId() { return userId; }
    public ReactionType getType() { return type; }
}
