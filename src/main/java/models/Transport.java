package models;

public class Transport {
    private int id;
    private String type;
    private String description;
    private double prix;
    private boolean disponibilite;
    private String image;
    private int userId;

    // Constructeur par défaut
    public Transport() {
    }

    // Constructeur avec paramètres
    public Transport(String type, String description, double prix, boolean disponibilite, String image, int userId) {
        this.type = type;
        this.description = description;
        this.prix = prix;
        this.disponibilite = disponibilite;
        this.image = image;
        this.userId = userId;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Méthode toString pour l'affichage
    @Override
    public String toString() {
        return "Transport{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", disponibilite=" + disponibilite +
                ", image='" + image + '\'' +
                ", userId=" + userId +
                '}';
    }
}