package models;

public enum ReactionType {
    LIKE("👍"),
    LOVE("❤️"),
    HAHA("😂"),
    WOW("😮"),
    SAD("😢"),
    ANGRY("😡");

    private final String emoji;

    ReactionType(String emoji) {
        this.emoji = emoji;
    }

    public String getEmoji() {
        return emoji;
    }
}
