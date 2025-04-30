package controllers;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import models.Logement;
import service.LogementService;

public class LogementController {

    @FXML private TextField tfTitre;
    @FXML private TextArea taDescription;
    @FXML private TextField tfLocalisation;
    @FXML private TextField tfPrix;
    @FXML private TableView<Logement> tableLogement;
    @FXML private TableColumn<Logement, Integer> colId;
    @FXML private TableColumn<Logement, String> colTitre;
    @FXML private TableColumn<Logement, String> colDescription;
    @FXML private TableColumn<Logement, String> colLocalisation;
    @FXML private TableColumn<Logement, Float> colPrix;
    @FXML private Button btnAjouter, btnModifier, btnSupprimer, btnReset;
    @FXML private VBox formSection;
    private final LogementService logementService = new LogementService();
    private ObservableList<Logement> logements = FXCollections.observableArrayList();
    private Logement selectedLogement = null;

    // Labels pour les erreurs
    @FXML private Label labelTitre;
    @FXML private Label labelDescription;
    @FXML private Label labelLocalisation;
    @FXML private Label labelPrix;

    @FXML
    private void onLoadLogementsButtonClicked() {
        loadLogements();
    }
    private void loadLogements() {
        // Initialiser les colonnes
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colTitre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
        colDescription.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDescription()));
        colLocalisation.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocalisation()));
        colPrix.setCellValueFactory(cellData -> new SimpleFloatProperty(cellData.getValue().getPrix()).asObject());

        // Charger les logements
        logements.setAll(logementService.display());
        tableLogement.setItems(logements);

        // Gérer la sélection
        tableLogement.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedLogement = newVal;
                tfTitre.setText(newVal.getTitre());
                taDescription.setText(newVal.getDescription());
                tfLocalisation.setText(newVal.getLocalisation());
                tfPrix.setText(String.valueOf(newVal.getPrix()));
            }
        });
    }

    @FXML
    private void ajouterLogement() {
        if (!isInputValid()) return;

        Logement l = new Logement(0, tfTitre.getText(), taDescription.getText(), tfLocalisation.getText(), Float.parseFloat(tfPrix.getText()));
        logementService.add(l);

        // Optionally, show success message
        showAlert(Alert.AlertType.INFORMATION, "Success", "Logement ajouté avec succès!");
    }

    private boolean isInputValid() {
        // Check if the Titre field is empty
        if (tfTitre.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Formulaire invalide", "Le titre ne peut pas être vide.");
            return false;
        }

        // Check if the Description field is empty
        if (taDescription.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Formulaire invalide", "La description ne peut pas être vide.");
            return false;
        }

        // Check if the Localisation field is empty
        if (tfLocalisation.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Formulaire invalide", "La localisation ne peut pas être vide.");
            return false;
        }

        // Check if the Prix field is empty or contains a non-numeric value
        try {
            float prix = Float.parseFloat(tfPrix.getText());
            if (prix <= 0) {
                showAlert(Alert.AlertType.ERROR, "Formulaire invalide", "Le prix doit être un nombre positif.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Formulaire invalide", "Le prix doit être un nombre valide.");
            return false;
        }

        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void modifierLogement() {
        if (selectedLogement == null || !isInputValid()) return;

        // Affichage du formulaire
        formSection.setVisible(true);

        // Remplissage du formulaire avec les données du logement sélectionné
        tfTitre.setText(selectedLogement.getTitre());
        taDescription.setText(selectedLogement.getDescription());
        tfLocalisation.setText(selectedLogement.getLocalisation());
        tfPrix.setText(String.valueOf(selectedLogement.getPrix()));
    }

    // Méthode pour mettre à jour le logement après modification
    @FXML
    private void sauvegarderLogement() {
        if (selectedLogement == null || !isInputValid()) return;

        // Mise à jour du logement avec les nouvelles informations du formulaire
        selectedLogement.setTitre(tfTitre.getText());
        selectedLogement.setDescription(taDescription.getText());
        selectedLogement.setLocalisation(tfLocalisation.getText());
        selectedLogement.setPrix(Float.parseFloat(tfPrix.getText()));

        // Sauvegarde de la modification
        logementService.update(selectedLogement);

        // Rechargement de la liste des logements
        loadLogements();
        resetForm();
    }
    @FXML
    private void supprimerLogement() {
        if (selectedLogement == null) return;

        logementService.delete(selectedLogement.getId());
        loadLogements();
        resetForm();
    }

    @FXML
    private void resetForm() {
        tfTitre.clear();
        taDescription.clear();
        tfLocalisation.clear();
        tfPrix.clear();
        selectedLogement = null;
        tableLogement.getSelectionModel().clearSelection();
    }

    @FXML
    public void initialize() {
        // Listener pour le champ Prix
        tfPrix.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!isNumeric(newValue)) {
                setError(tfPrix, labelPrix, "Le prix doit être un nombre valide.");
            } else {
                resetField(tfPrix, labelPrix);
            }
        });

        // Listener pour la Description
        taDescription.textProperty().addListener((observable, oldValue, newValue) -> {
            if (containsNumbers(newValue)) {
                setError(taDescription, labelDescription, "La description ne doit pas contenir de chiffres.");
            } else {
                resetField(taDescription, labelDescription);
            }
        });

        // Listener pour Titre
        tfTitre.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                setError(tfTitre, labelTitre, "Le titre ne peut pas être vide.");
            } else {
                resetField(tfTitre, labelTitre);
            }
        });

        // Listener pour Localisation
        tfLocalisation.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                setError(tfLocalisation, labelLocalisation, "La localisation ne peut pas être vide.");
            } else {
                resetField(tfLocalisation, labelLocalisation);
            }
        });
    }

    // Méthode pour mettre en place l'erreur sur le champ
    private void setError(Control field, Label errorLabel, String message) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 1;");
        errorLabel.setText(message);
    }

    // Méthode pour réinitialiser le champ et son label d'erreur
    private void resetField(Control field, Label errorLabel) {
        field.setStyle(null);
        errorLabel.setText("");
    }

    // Méthode pour vérifier si une chaîne est un nombre
    private boolean isNumeric(String str) {
        try {
            Float.parseFloat(str); // Tentative de conversion en nombre
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Méthode pour vérifier si une chaîne contient des chiffres
    private boolean containsNumbers(String str) {
        return str.matches(".*\\d.*"); // Regex pour vérifier s'il y a des chiffres dans la chaîne
    }

}


