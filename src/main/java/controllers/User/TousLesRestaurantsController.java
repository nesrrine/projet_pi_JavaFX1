package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Restaurant;
import models.User;
import service.RestaurantService;
import service.UserService;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class TousLesRestaurantsController {

    @FXML private FlowPane restaurantContainer;

    private final RestaurantService restaurantService = new RestaurantService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        loadRestaurants();
    }

    private void loadRestaurants() {
        try {
            User currentUser = Session.getCurrentUser();
            restaurantContainer.getChildren().clear();

            for (Restaurant restaurant : restaurantService.getAllRestaurants()) {
                VBox card = createRestaurantCard(restaurant, currentUser);
                restaurantContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des restaurants: " + e.getMessage());
        }
    }

    private VBox createRestaurantCard(Restaurant restaurant, User currentUser) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);");
        card.setPrefWidth(300);

        // Nom label
        Label nomLabel = new Label(restaurant.getNom());
        nomLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Owner info
        User owner = userService.getById(restaurant.getUserId());
        Label ownerLabel = new Label("Propriétaire: " + owner.getFirstName());
        ownerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        // Localisation label
        Label localisationLabel = new Label(restaurant.getLocalisation());
        localisationLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");

        // Description label
        Label descriptionLabel = new Label(restaurant.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px;");

        // Prix label
        Label prixLabel = new Label(String.format("%.2f TND", restaurant.getPrix()));
        prixLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #007bff;");

        // Images
        HBox imagesBox = new HBox(10);
        imagesBox.setStyle("-fx-alignment: center;");

        ImageView mainImage = createImageView(restaurant.getImage(), 280, 180);
        ImageView image1 = createImageView(restaurant.getImage1(), 135, 90);
        ImageView image2 = createImageView(restaurant.getImage2(), 135, 90);

        VBox thumbnails = new VBox(5);
        thumbnails.getChildren().addAll(image1, image2);
        imagesBox.getChildren().addAll(mainImage, thumbnails);

        // Réserver button
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        reserverButton.setOnAction(e -> openReservationWindow(restaurant));

        // Edit/Delete buttons for owner
        if (currentUser != null && currentUser.getId() == restaurant.getUserId()) {
            Button editBtn = new Button("Modifier");
            editBtn.getStyleClass().add("btn-edit");
            editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            editBtn.setOnAction(e -> openEditRestaurant(restaurant));

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.getStyleClass().add("btn-delete");
            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            deleteBtn.setOnAction(e -> handleDelete(restaurant));

            HBox buttonsBox = new HBox(10);
            buttonsBox.setStyle("-fx-alignment: center;");
            buttonsBox.getChildren().addAll(reserverButton, editBtn, deleteBtn);
            card.getChildren().addAll(imagesBox, nomLabel, ownerLabel, localisationLabel, descriptionLabel, prixLabel, buttonsBox);
        } else {
            card.getChildren().addAll(imagesBox, nomLabel, ownerLabel, localisationLabel, descriptionLabel, prixLabel, reserverButton);
        }

        return card;
    }

    private ImageView createImageView(String imagePath, double width, double height) {
        ImageView imageView = new ImageView();
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);

        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            }
        }

        return imageView;
    }

    private void openReservationWindow(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/ReservationRestaurant.fxml"));
            Scene scene = new Scene(loader.load());

            Stage stage = new Stage();
            stage.setTitle("Réservation de Restaurant");
            stage.setScene(scene);

            ReservationRestaurantController controller = loader.getController();
            controller.setRestaurant(restaurant);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openEditRestaurant(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/CreateRestaurant.fxml"));
            Scene scene = new Scene(loader.load());

            CreateRestaurantController controller = loader.getController();
            controller.setRestaurantToEdit(restaurant);

            Stage stage = new Stage();
            stage.setTitle("Modifier le Restaurant");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            loadRestaurants(); // Refresh after editing
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire de modification: " + e.getMessage());
        }
    }

    private void handleDelete(Restaurant restaurant) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce restaurant ?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                restaurantService.deleteRestaurant(restaurant.getId());
                loadRestaurants(); // Refresh the view
                showAlert(Alert.AlertType.INFORMATION, "Restaurant supprimé avec succès!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression: " + e.getMessage());
            }
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
