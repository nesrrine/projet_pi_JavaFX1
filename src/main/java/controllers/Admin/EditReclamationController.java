package controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import models.Reclamation;
import service.ReclamationService;

public class EditReclamationController {

    @FXML private ComboBox<String> statutComboBox;

    private Reclamation reclamation;
    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        statutComboBox.getItems().addAll("En attente", "En cours", "Résolue");
    }

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
        statutComboBox.setValue(reclamation.getStatut());
    }

    @FXML
    private void handleSave() {
        if (reclamation == null || statutComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un statut.");
            return;
        }

        reclamation.setStatut(statutComboBox.getValue());
        reclamationService.update(reclamation);
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) statutComboBox.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
