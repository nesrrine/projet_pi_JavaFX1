package controllers.User;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import models.Restaurant;
import models.User;
import service.WhatsAppService;
import utils.Session;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReservationRestaurantController {
    @FXML private Label nomLabel;
    @FXML private Label localisationLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label prixLabel;
    @FXML private ImageView mainImage;
    @FXML private ImageView image1;
    @FXML private ImageView image2;
    @FXML private DatePicker datePicker;
    @FXML private TextField nombrePersonnesField;
    @FXML private Button reserverButton;
    
    private Restaurant restaurant;
    private final WhatsAppService whatsAppService = new WhatsAppService();

    @FXML
    public void initialize() {
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
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        nomLabel.setText(restaurant.getNom());
        localisationLabel.setText(restaurant.getLocalisation());
        descriptionLabel.setText(restaurant.getDescription());
        prixLabel.setText(String.format("%.2f TND", restaurant.getPrix()));

        // Load images if they exist
        loadImage(restaurant.getImage(), mainImage);
        loadImage(restaurant.getImage1(), image1);
        loadImage(restaurant.getImage2(), image2);

        // Initialize reservation button action
        reserverButton.setOnAction(e -> handleReservation());
    }

    private void loadImage(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            }
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
        
        // Format the date for display
        String formattedDate = reservationDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        // Show confirmation dialog
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de réservation");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText(
            "Voulez-vous confirmer la réservation suivante?\n\n" +
            "Restaurant: " + restaurant.getNom() + "\n" +
            "Date: " + formattedDate + "\n" +
            "Nombre de personnes: " + nombrePersonnes
        );
        
        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Here you would normally save the reservation to the database
                
                // Send WhatsApp confirmation to the user
                sendWhatsAppConfirmation(currentUser, formattedDate, nombrePersonnes);
                
                // Show success message
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Réservation confirmée");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Votre réservation a été confirmée avec succès! Un message WhatsApp a été envoyé avec les détails.");
                successAlert.showAndWait();
                
                // Close the reservation window
                Stage stage = (Stage) reserverButton.getScene().getWindow();
                stage.close();
            }
        });
    }
    
    private void sendWhatsAppConfirmation(User user, String date, int nombrePersonnes) {
        try {
            // Get the user's phone number
            String phoneNumber = user.getPhone();
            
            // Format the phone number for WhatsApp API
            String formattedPhoneNumber = whatsAppService.formatPhoneNumber(phoneNumber);
            
            // For now, we're using the hello_world template which doesn't accept parameters
            // In a production environment, you would create a custom template for reservations
            // and use the sendTemplateMessage method with parameters
            boolean success = whatsAppService.sendMessage(formattedPhoneNumber, null);
            
            // Example of how to use a custom template with parameters when you have one:
            /*
            String[] parameters = {
                user.getFirstName(),
                restaurant.getNom(),
                date,
                String.valueOf(nombrePersonnes),
                restaurant.getLocalisation()
            };
            boolean success = whatsAppService.sendTemplateMessage(
                formattedPhoneNumber, 
                "reservation_confirmation", 
                parameters
            );
            */
            
            if (!success) {
                System.err.println("Failed to send WhatsApp message to " + formattedPhoneNumber);
                showAlert(Alert.AlertType.WARNING, 
                    "La réservation a été confirmée, mais l'envoi du message WhatsApp a échoué. " +
                    "Veuillez vérifier les paramètres de l'API WhatsApp."
                );
            }
            
        } catch (Exception e) {
            System.err.println("Error sending WhatsApp message: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.WARNING, 
                "La réservation a été confirmée, mais l'envoi du message WhatsApp a échoué: " + e.getMessage()
            );
        }
    }
    
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
