package controllers.User;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import models.Restaurant;

import java.io.File;

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

    public void setRestaurant(Restaurant restaurant) {
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
        // Cette méthode sera implémentée plus tard pour gérer la réservation
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Réservation");
        alert.setHeaderText(null);
        alert.setContentText("La fonctionnalité de réservation sera bientôt disponible!");
        alert.showAndWait();
    }
}
