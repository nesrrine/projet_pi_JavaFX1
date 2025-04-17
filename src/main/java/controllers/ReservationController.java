package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import models.Reservation;
import service.ReservationService;

import java.time.LocalDate;

public class ReservationController {

    @FXML private TextField tfTitre;
    @FXML private DatePicker dpDateDebut;
    @FXML private DatePicker dpDateFin;
    @FXML private TextField tfStatut;
    @FXML private Label labelTitre, labelDateDebut, labelDateFin, labelStatut;
    @FXML private TableView<Reservation> tableReservation;
    @FXML private Button btnAjouter, btnReset;

    private final ReservationService reservationService = new ReservationService();
    private Reservation selectedReservation = null;

    public void initialize() {
        // Instant validation for title
        tfTitre.textProperty().addListener((observable, oldValue, newValue) -> validateForm());

        // Instant validation for date of start
        dpDateDebut.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());

        // Instant validation for date of end
        dpDateFin.valueProperty().addListener((observable, oldValue, newValue) -> validateForm());

        // Instant validation for statut
        tfStatut.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
    }

    private void validateForm() {
        boolean valid = true;

        // Validate title
        if (tfTitre.getText().isEmpty()) {
            labelTitre.setText("Le titre est obligatoire.");
            valid = false;
        } else {
            labelTitre.setText("");
        }

        // Validate start date
        if (dpDateDebut.getValue() == null) {
            labelDateDebut.setText("La date de début est obligatoire.");
            valid = false;
        } else {
            labelDateDebut.setText("");
        }

        // Validate end date
        if (dpDateFin.getValue() == null) {
            labelDateFin.setText("La date de fin est obligatoire.");
            valid = false;
        } else if (dpDateDebut.getValue() != null && dpDateDebut.getValue().isAfter(dpDateFin.getValue())) {
            labelDateFin.setText("La date de fin ne peut pas être avant la date de début.");
            valid = false;
        } else {
            labelDateFin.setText("");
        }

        // Validate statut
        if (tfStatut.getText().isEmpty()) {
            labelStatut.setText("Le statut est obligatoire.");
            valid = false;
        } else {
            labelStatut.setText("");
        }

        // Enable/disable the add button based on validation
        btnAjouter.setDisable(!valid);
    }


    @FXML
    private void ajouterReservation() {
        if (!isInputValid()) return;

        Reservation r = new Reservation(
                0,
                tfTitre.getText(),
                dpDateDebut.getValue(),
                dpDateFin.getValue(),
                tfStatut.getText()
        );

        // Add the reservation
        reservationService.add(r);

        // Show success alert
        showSuccessAlert();
    }

    private void showSuccessAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null); // No header
        alert.setContentText("La réservation a été ajoutée avec succès.");
        alert.showAndWait();
    }

    private boolean isInputValid() {
        // Additional final validation before saving the reservation
        if (labelTitre.getText().isEmpty() && labelDateDebut.getText().isEmpty() &&
                labelDateFin.getText().isEmpty() && labelStatut.getText().isEmpty()) {
            return true;
        }
        return false;
    }

    @FXML
    private void resetForm() {
        tfTitre.clear();
        dpDateDebut.setValue(null);
        dpDateFin.setValue(null);
        tfStatut.clear();
        selectedReservation = null;
        tableReservation.getSelectionModel().clearSelection();
        validateForm(); // Reset validation status
    }
}
