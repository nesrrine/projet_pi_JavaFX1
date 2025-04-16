package controllers.User;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Transport;

import java.io.File;

public class ReservationTransportController {
    @FXML private Label typeLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label prixLabel;
    @FXML private ImageView transportImage;
    @FXML private DatePicker datePicker;
    @FXML private TextField nombrePersonnesField;
    @FXML private Button reserverButton;

    public void setTransport(Transport transport) {
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
    }

    private void handleReservation() {
        // Cette méthode sera implémentée plus tard pour gérer la réservation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Réservation");
        alert.setHeaderText(null);
        alert.setContentText("La fonctionnalité de réservation sera bientôt disponible!");
        alert.showAndWait();
    }
}
