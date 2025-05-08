package models;

public class Logement {

    private int id;
    private String titre;
    private String description;
    private String localisation;
    private float prix;

    public Logement() {
    }

    public Logement(int id, String titre, String description, String localisation, float prix) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.localisation = localisation;
        this.prix = prix;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getLocalisation() {
        return localisation;
    }

    public float getPrix() {
        return prix;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public void setPrix(float prix) {
        this.prix = prix;
    }

    @Override
    public String toString() {
        return "Logement{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", localisation='" + localisation + '\'' +
                ", prix=" + prix +
                '}';
    }
}
