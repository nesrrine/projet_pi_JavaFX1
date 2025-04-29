package controllers.User;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Transport;
import models.TransportReservation;
import models.User;
import service.PDFService;
import service.TransportReservationService;
import service.WhatsAppService;
import utils.Session;

import java.awt.Desktop;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReservationTransportController {
    @FXML private Label typeLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label prixLabel;
    @FXML private ImageView transportImage;
    @FXML private DatePicker datePicker;
    @FXML private TextField nombrePersonnesField;
    @FXML private Button reserverButton;
    
    private Transport transport;
    private final PDFService pdfService = new PDFService();
    private final WhatsAppService whatsAppService = new WhatsAppService();
    private final TransportReservationService reservationService = new TransportReservationService();

    @FXML
    public void initialize() {
        // Create the reservation table if it doesn't exist
        try {
            reservationService.createTableIfNotExists();
        } catch (SQLException e) {
            System.err.println("Error creating reservation table: " + e.getMessage());
        }
        
        // Set default date to today
        LocalDate today = LocalDate.now();
        LocalDate oneWeekLater = today.plusDays(7);
        datePicker.setValue(today);
        
        // Disable past dates and dates more than one week in the future in the DatePicker
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(today) || date.isAfter(oneWeekLater));
                
                // Add visual indication for dates outside the allowed range
                if (date.isBefore(today) || date.isAfter(oneWeekLater)) {
                    setStyle("-fx-background-color: #f8d7da;");
                }
            }
        });
        
        // Add validation for number of people
        nombrePersonnesField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                nombrePersonnesField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        // Add validation for date selection
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (transport != null && newValue != null) {
                checkDateAvailability(newValue);
            }
        });
    }

    public void setTransport(Transport transport) {
        this.transport = transport;
        typeLabel.setText(transport.getType());
        descriptionLabel.setText(transport.getDescription());
        prixLabel.setText(String.format("%.2f TND", transport.getPrix()));

        // Load image if exists
        String imagePath = transport.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                transportImage.setImage(image);
            }
        }

        // Initialize reservation button action
        reserverButton.setOnAction(e -> handleReservation());
        
        // Check availability for the default date
        if (datePicker.getValue() != null) {
            checkDateAvailability(datePicker.getValue());
        }
    }
    
    private void checkDateAvailability(LocalDate date) {
        try {
            boolean isReserved = reservationService.isTransportReservedOnDate(transport.getId(), date);
            if (isReserved) {
                reserverButton.setDisable(true);
                reserverButton.setText("Non disponible");
                reserverButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                
                showAlert(Alert.AlertType.WARNING, 
                    "Ce véhicule est déjà réservé pour cette date. Veuillez choisir une autre date."
                );
            } else {
                reserverButton.setDisable(false);
                reserverButton.setText("Réserver");
                reserverButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                      "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
            }
        } catch (SQLException e) {
            System.err.println("Error checking transport availability: " + e.getMessage());
        }
    }

    private void handleReservation() {
        // Validate inputs
        if (datePicker.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Veuillez sélectionner une date pour la réservation.");
            return;
        }
        
        if (nombrePersonnesField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Veuillez indiquer le nombre de personnes.");
            return;
        }
        
        // Get reservation details
        LocalDate reservationDate = datePicker.getValue();
        int nombrePersonnes = Integer.parseInt(nombrePersonnesField.getText());
        
        // Check if date is valid (not in the past and not more than a week in the future)
        LocalDate today = LocalDate.now();
        LocalDate oneWeekLater = today.plusDays(7);
        
        if (reservationDate.isBefore(today)) {
            showAlert(Alert.AlertType.ERROR, "La date de réservation ne peut pas être dans le passé.");
            return;
        }
        
        if (reservationDate.isAfter(oneWeekLater)) {
            showAlert(Alert.AlertType.ERROR, "La date de réservation ne peut pas être plus d'une semaine dans le futur.");
            return;
        }
        
        // Get current user
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Vous devez être connecté pour effectuer une réservation.");
            return;
        }
        
        // Check if transport is already reserved for this date
        try {
            if (reservationService.isTransportReservedOnDate(transport.getId(), reservationDate)) {
                showAlert(Alert.AlertType.ERROR, 
                    "Ce véhicule est déjà réservé pour cette date. Veuillez choisir une autre date."
                );
                return;
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la vérification de la disponibilité: " + e.getMessage());
            return;
        }
        
        // Format the date for display
        String formattedDate = reservationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de réservation");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText(
            "Voulez-vous confirmer la réservation suivante?\n\n" +
            "Type de transport: " + transport.getType() + "\n" +
            "Date: " + formattedDate + "\n" +
            "Nombre de personnes: " + nombrePersonnes
        );
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Generate PDF
                try {
                    String pdfPath = pdfService.generateTransportReservationPDF(
                        transport, 
                        currentUser, 
                        reservationDate, 
                        nombrePersonnes
                    );
                    
                    // Create reservation object
                    TransportReservation reservation = new TransportReservation(
                        transport.getId(),
                        currentUser.getId(),
                        reservationDate,
                        nombrePersonnes,
                        pdfPath
                    );
                    
                    // Save to database
                    try {
                        reservationService.addReservation(reservation);
                        System.out.println("Reservation saved to database with ID: " + reservation.getId());
                    } catch (SQLException e) {
                        System.err.println("Error saving reservation to database: " + e.getMessage());
                        showAlert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement de la réservation: " + e.getMessage());
                        return;
                    }
                    
                    // Show success message with PDF path
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                    successAlert.setTitle("Réservation confirmée");
                    successAlert.setHeaderText(null);
                    successAlert.setContentText(
                        "Votre réservation a été confirmée avec succès!\n\n" +
                        "Un PDF de confirmation a été généré à:\n" + pdfPath
                    );
                    
                    // Add button to open PDF
                    ButtonType openPdfButton = new ButtonType("Ouvrir le PDF", ButtonBar.ButtonData.OK_DONE);
                    ButtonType closeButton = new ButtonType("Fermer", ButtonBar.ButtonData.CANCEL_CLOSE);
                    successAlert.getButtonTypes().setAll(openPdfButton, closeButton);
                    
                    successAlert.showAndWait().ifPresent(buttonType -> {
                        if (buttonType == openPdfButton) {
                            try {
                                File pdfFile = new File(pdfPath);
                                Desktop.getDesktop().open(pdfFile);
                            } catch (Exception e) {
                                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du PDF: " + e.getMessage());
                            }
                        }
                    });
                    
                    // Send WhatsApp confirmation if available
                    try {
                        String phoneNumber = currentUser.getPhone();
                        if (phoneNumber != null && !phoneNumber.isEmpty()) {
                            String formattedPhone = whatsAppService.formatPhoneNumber(phoneNumber);
                            whatsAppService.sendMessage(formattedPhone, null);
                        }
                    } catch (Exception e) {
                        System.err.println("Error sending WhatsApp message: " + e.getMessage());
                    }
                    
                    // Close the reservation window
                    Stage stage = (Stage) reserverButton.getScene().getWindow();
                    stage.close();
                    
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de la génération du PDF: " + e.getMessage());
                }
            }
        });
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
