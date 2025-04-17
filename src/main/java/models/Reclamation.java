package models;

import java.time.LocalDateTime;

public class Reclamation {
    private int id;
    private int auteurId;
    private int cibleId;
    private String titre;
    private String description;
    private String statut;
    private LocalDateTime dateSoumission;
    private String photo;
    private String document;
    private String categorie;

    public Reclamation() {}

    public Reclamation(int auteurId, int cibleId, String titre, String description, String statut, String photo, String document, String categorie) {
        this.auteurId = auteurId;
        this.cibleId = cibleId;
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.photo = photo;
        this.document = document;
        this.categorie = categorie;
    }

    public Reclamation(int id, int auteurId, int cibleId, String titre, String description, String statut, LocalDateTime dateSoumission, String photo, String document, String categorie) {
        this.id = id;
        this.auteurId = auteurId;
        this.cibleId = cibleId;
        this.titre = titre;
        this.description = description;
        this.statut = statut;
        this.dateSoumission = dateSoumission;
        this.photo = photo;
        this.document = document;
        this.categorie = categorie;
    }

    // Getters and Setters

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getAuteurId() { return auteurId; }
    public void setAuteurId(int auteurId) { this.auteurId = auteurId; }

    public int getCibleId() { return cibleId; }
    public void setCibleId(int cibleId) { this.cibleId = cibleId; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatut() { return statut; }
    public void setStatut(String statut) { this.statut = statut; }

    public LocalDateTime getDateSoumission() { return dateSoumission; }
    public void setDateSoumission(LocalDateTime dateSoumission) { this.dateSoumission = dateSoumission; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getDocument() { return document; }
    public void setDocument(String document) { this.document = document; }

    public String getCategorie() { return categorie; }
    public void setCategorie(String categorie) { this.categorie = categorie; }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", auteurId=" + auteurId +
                ", cibleId=" + cibleId +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", statut='" + statut + '\'' +
                ", dateSoumission=" + dateSoumission +
                ", photo='" + photo + '\'' +
                ", document='" + document + '\'' +
                ", categorie='" + categorie + '\'' +
                '}';
    }
}
