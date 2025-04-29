package models;

public class Restaurant {
    private int id;
    private String nom;
    private String localisation;
    private String image;
    private String description;
    private double prix;
    private double lat;
    private double lng;
    private String image1;
    private String image2;
    private int userId;
    private boolean promotion;

    // Constructeurs
    public Restaurant() {
    }

    public Restaurant(String nom, String localisation, String image, String description, double prix, double lat, double lng, String image1, String image2, int userId, boolean promotion) {
        this.nom = nom;
        this.localisation = localisation;
        this.image = image;
        this.description = description;
        this.prix = prix;
        this.lat = lat;
        this.lng = lng;
        this.image1 = image1;
        this.image2 = image2;
        this.userId = userId;
        this.promotion = promotion;
    }

    public Restaurant(String nom, String localisation, String image, String description, double prix, double lat, double lng, String image1, String image2, int userId) {
        this.nom = nom;
        this.localisation = localisation;
        this.image = image;
        this.description = description;
        this.prix = prix;
        this.lat = lat;
        this.lng = lng;
        this.image1 = image1;
        this.image2 = image2;
        this.userId = userId;
        this.promotion = false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getImage1() {
        return image1;
    }

    public void setImage1(String image1) {
        this.image1 = image1;
    }

    public String getImage2() {
        return image2;
    }

    public void setImage2(String image2) {
        this.image2 = image2;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public boolean isPromotion() {
        return promotion;
    }
    
    public void setPromotion(boolean promotion) {
        this.promotion = promotion;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", localisation='" + localisation + '\'' +
                ", image='" + image + '\'' +
                ", description='" + description + '\'' +
                ", prix=" + prix +
                ", lat=" + lat +
                ", lng=" + lng +
                ", image1='" + image1 + '\'' +
                ", image2='" + image2 + '\'' +
                ", userId=" + userId +
                ", promotion=" + promotion +
                '}';
    }
}