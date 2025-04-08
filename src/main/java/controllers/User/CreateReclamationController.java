package controllers.User;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Reclamation;
import models.User;
import service.ReclamationService;
import service.UserService;
import utils.Session;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class CreateReclamationController {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<String> categorieComboBox;
    @FXML private ComboBox<String> utilisateurConcerneComboBox;
    @FXML private Label photoLabel;
    @FXML private Label documentLabel;

    @FXML private Label titreError;
    @FXML private Label descriptionError;
    @FXML private Label categorieError;
    @FXML private Label utilisateurError;

    private File selectedPhoto;
    private File selectedDocument;

    private final UserService userService = new UserService();
    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    private void initialize() {
        categorieComboBox.getItems().addAll("Hôte", "Transporteur", "Restaurant", "Voyageur");

        categorieComboBox.setOnAction(e -> {
            String selected = categorieComboBox.getValue();
            if (selected != null) {
                List<User> matching = userService.display().stream()
                        .filter(u -> selected.equalsIgnoreCase(u.getRole()))
                        .collect(Collectors.toList());
                utilisateurConcerneComboBox.getItems().clear();
                utilisateurConcerneComboBox.getItems().addAll(
                        matching.stream().map(User::getEmail).toList()
                );
            }
        });
    }

    @FXML
    private void handleChoosePhoto() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une photo");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg"));
        selectedPhoto = chooser.showOpenDialog(getStage());
        if (selectedPhoto != null) photoLabel.setText(selectedPhoto.getName());
    }

    @FXML
    private void handleChooseDocument() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir un document");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx"));
        selectedDocument = chooser.showOpenDialog(getStage());
        if (selectedDocument != null) documentLabel.setText(selectedDocument.getName());
    }

    @FXML
    private void handleSubmit() {
        boolean valid = true;

        String titre = titreField.getText().trim();
        String description = descriptionField.getText().trim();
        String categorie = categorieComboBox.getValue();
        String emailCible = utilisateurConcerneComboBox.getValue();

        // Reset styles and error messages
        clearValidation();

        if (titre.isEmpty()) {
            titreField.setStyle("-fx-border-color: red;");
            titreError.setText("Le titre est requis.");
            valid = false;
        }

        if (description.isEmpty()) {
            descriptionField.setStyle("-fx-border-color: red;");
            descriptionError.setText("La description est requise.");
            valid = false;
        }

        if (categorie == null) {
            categorieComboBox.setStyle("-fx-border-color: red;");
            categorieError.setText("La catégorie est requise.");
            valid = false;
        }

        if (emailCible == null) {
            utilisateurConcerneComboBox.setStyle("-fx-border-color: red;");
            utilisateurError.setText("L'utilisateur concerné est requis.");
            valid = false;
        }

        if (!valid) return;

        User cible = userService.getByEmail(emailCible);
        Reclamation r = new Reclamation();
        r.setTitre(titre);
        r.setDescription(description);
        r.setCategorie(categorie);
        r.setStatut("En cours");
        r.setDateSoumission(LocalDateTime.now());
        r.setAuteurId(Session.getCurrentUser().getId());
        r.setCibleId(cible.getId());
        r.setPhoto(selectedPhoto != null ? selectedPhoto.getAbsolutePath() : "");
        r.setDocument(selectedDocument != null ? selectedDocument.getAbsolutePath() : "");

        reclamationService.add(r);
        showAlert(Alert.AlertType.INFORMATION, "Réclamation enregistrée avec succès !");
        clearForm();
    }

    private void clearValidation() {
        titreField.setStyle(null);
        descriptionField.setStyle(null);
        categorieComboBox.setStyle(null);
        utilisateurConcerneComboBox.setStyle(null);

        titreError.setText("");
        descriptionError.setText("");
        categorieError.setText("");
        utilisateurError.setText("");
    }

    private void clearForm() {
        clearValidation();
        titreField.clear();
        descriptionField.clear();
        categorieComboBox.setValue(null);
        utilisateurConcerneComboBox.getItems().clear();
        photoLabel.setText("Aucune photo sélectionnée");
        documentLabel.setText("Aucun document sélectionné");
        selectedPhoto = null;
        selectedDocument = null;
    }

    private Stage getStage() {
        return (Stage) titreField.getScene().getWindow();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
