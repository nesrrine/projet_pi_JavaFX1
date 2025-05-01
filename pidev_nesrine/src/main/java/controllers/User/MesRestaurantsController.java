package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Restaurant;
import models.User;
import service.RestaurantService;
import utils.Session;

import java.io.File;
import java.sql.SQLException;

public class MesRestaurantsController {

    @FXML private FlowPane restaurantContainer;

    private final RestaurantService restaurantService = new RestaurantService();

    @FXML
    public void initialize() {
        try {
            User currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Vous devez être connecté pour voir vos restaurants.");
                return;
            }

            restaurantContainer.getChildren().clear();

            for (Restaurant restaurant : restaurantService.getRestaurantsByUserId(currentUser.getId())) {
                VBox card = new VBox(10);
                card.getStyleClass().add("restaurant-card");
                card.setPrefWidth(300);
                card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

                // Title with name
                Label title = new Label(restaurant.getNom());
                title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

                // Location
                Label location = new Label("📍 " + restaurant.getLocalisation());
                location.setStyle("-fx-font-size: 14;");

                // Description
                Label description = new Label(restaurant.getDescription());
                description.setStyle("-fx-font-size: 14;");
                description.setWrapText(true);

                // Price
                Label price = new Label(String.format("Prix moyen: %.2f TND", restaurant.getPrix()));
                price.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                VBox content = new VBox(10, title, location, description, price);

                // Main image
                if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
                    File imageFile = new File(restaurant.getImage());
                    if (imageFile.exists()) {
                        ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
                        imageView.setFitWidth(270);
                        imageView.setPreserveRatio(true);
                        content.getChildren().add(1, imageView); // Add after title
                    }
                }

                // Additional images in a horizontal container
                HBox additionalImages = new HBox(10);
                additionalImages.setStyle("-fx-alignment: center;");

                if (restaurant.getImage1() != null && !restaurant.getImage1().isEmpty()) {
                    File image1File = new File(restaurant.getImage1());
                    if (image1File.exists()) {
                        ImageView image1View = new ImageView(new Image(image1File.toURI().toString()));
                        image1View.setFitWidth(130);
                        image1View.setPreserveRatio(true);
                        additionalImages.getChildren().add(image1View);
                    }
                }

                if (restaurant.getImage2() != null && !restaurant.getImage2().isEmpty()) {
                    File image2File = new File(restaurant.getImage2());
                    if (image2File.exists()) {
                        ImageView image2View = new ImageView(new Image(image2File.toURI().toString()));
                        image2View.setFitWidth(130);
                        image2View.setPreserveRatio(true);
                        additionalImages.getChildren().add(image2View);
                    }
                }

                if (!additionalImages.getChildren().isEmpty()) {
                    content.getChildren().add(additionalImages);
                }

                // Edit/Delete buttons
                Button editBtn = new Button("Modifier");
                editBtn.getStyleClass().add("btn-edit");
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                editBtn.setOnAction(e -> openEditRestaurant(restaurant));

                Button deleteBtn = new Button("Supprimer");
                deleteBtn.getStyleClass().add("btn-delete");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> handleDelete(restaurant));

                HBox buttons = new HBox(10, editBtn, deleteBtn);
                buttons.setStyle("-fx-alignment: center;");
                content.getChildren().add(buttons);

                card.getChildren().add(content);
                restaurantContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des restaurants: " + e.getMessage());
        }
    }

    private void openEditRestaurant(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/CreateRestaurant.fxml"));
            Parent root = loader.load();

            CreateRestaurantController controller = loader.getController();
            controller.setRestaurantToEdit(restaurant);

            Stage stage = new Stage();
            stage.setTitle("Modifier Restaurant");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            initialize(); // Refresh after editing
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
                initialize(); // Refresh the view
                showAlert(Alert.AlertType.INFORMATION, "Restaurant supprimé avec succès!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
