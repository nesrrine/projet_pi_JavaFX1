package controllers.Admin;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import models.Restaurant;
import service.RestaurantService;

import java.sql.SQLException;
import java.util.List;

public class GestionRestaurantsController {

    @FXML private VBox restaurantListContainer;

    private final RestaurantService restaurantService = new RestaurantService();

    @FXML
    private void initialize() {
        // Load restaurants
        loadRestaurants();
    }

    private void loadRestaurants() {
        try {
            // Clear existing items
            restaurantListContainer.getChildren().clear();
            
            // Get all restaurants
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();
            
            if (restaurants.isEmpty()) {
                Label emptyLabel = new Label("Aucun restaurant trouvé");
                emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
                emptyLabel.setPadding(new Insets(20));
                restaurantListContainer.getChildren().add(emptyLabel);
                return;
            }
            
            // Create a card for each restaurant
            for (Restaurant restaurant : restaurants) {
                restaurantListContainer.getChildren().add(createRestaurantCard(restaurant));
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des restaurants", e.getMessage());
        }
    }
    
    private VBox createRestaurantCard(Restaurant restaurant) {
        // Main card container
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5px; -fx-padding: 15px;");
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        
        // Restaurant name
        Label nameLabel = new Label(restaurant.getNom());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        nameLabel.setStyle("-fx-text-fill: #3498db;");
        
        // Restaurant details
        HBox detailsBox = new HBox(20);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        
        // Location
        VBox locationBox = new VBox(5);
        Text locationTitle = new Text("Localisation");
        locationTitle.setStyle("-fx-font-weight: bold; -fx-fill: #555;");
        Text locationValue = new Text(restaurant.getLocalisation());
        locationBox.getChildren().addAll(locationTitle, locationValue);
        
        // Price
        VBox priceBox = new VBox(5);
        Text priceTitle = new Text("Prix");
        priceTitle.setStyle("-fx-font-weight: bold; -fx-fill: #555;");
        Text priceValue = new Text(String.format("%.2f DT", restaurant.getPrix()));
        priceBox.getChildren().addAll(priceTitle, priceValue);
        
        detailsBox.getChildren().addAll(locationBox, priceBox);
        
        // Description
        VBox descriptionBox = new VBox(5);
        Text descriptionTitle = new Text("Description");
        descriptionTitle.setStyle("-fx-font-weight: bold; -fx-fill: #555;");
        Text descriptionValue = new Text(restaurant.getDescription());
        descriptionValue.setWrappingWidth(600);
        descriptionBox.getChildren().addAll(descriptionTitle, descriptionValue);
        
        // Action buttons
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("button-edit");
        editButton.setOnAction(e -> handleEdit(restaurant));
        
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("button-delete");
        deleteButton.setOnAction(e -> handleDelete(restaurant));
        
        actionsBox.getChildren().addAll(editButton, deleteButton);
        
        // Add all elements to card
        card.getChildren().addAll(nameLabel, detailsBox, descriptionBox, actionsBox);
        
        return card;
    }

    private void handleDelete(Restaurant restaurant) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le restaurant");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce restaurant ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    restaurantService.deleteRestaurant(restaurant.getId());
                    loadRestaurants();
                } catch (SQLException e) {
                    showError("Erreur lors de la suppression", e.getMessage());
                }
            }
        });
    }

    private void handleEdit(Restaurant restaurant) {
        // TODO: Implement edit functionality
        System.out.println("Edit restaurant: " + restaurant.getId());
    }
    


    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
