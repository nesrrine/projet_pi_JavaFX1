package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Restaurant;
import models.User;
import service.RestaurantService;
import service.UserService;
import utils.Session;
import utils.PermissionManager;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

public class TousLesRestaurantsController {

    @FXML private FlowPane restaurantContainer;
    @FXML private Slider prixSlider;
    @FXML private Label prixLabel;
    @FXML private Button filtrerButton;
    @FXML private Button resetButton;

    private final RestaurantService restaurantService = new RestaurantService();
    private final UserService userService = new UserService();
    private List<Restaurant> allRestaurants;
    private double maxPrix = 200.0;

    @FXML
    public void initialize() {
        // Initialize price slider with the highest restaurant price
        try {
            double highestPrice = restaurantService.getHighestRestaurantPrice();
            // Add 10% buffer to the highest price
            highestPrice = Math.ceil(highestPrice * 1.1);
            maxPrix = highestPrice;
            prixSlider.setMax(highestPrice);
            prixSlider.setValue(highestPrice);
            prixLabel.setText(String.format("%.0f TND", highestPrice));
        } catch (SQLException e) {
            // Default to 200 if there's an error
            maxPrix = 200.0;
            prixSlider.setMax(200.0);
            prixSlider.setValue(200.0);
            prixLabel.setText("200 TND");
        }
        
        // Initialize price slider listener
        prixSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            maxPrix = newVal.doubleValue();
            prixLabel.setText(String.format("%.0f TND", maxPrix));
        });
        
        // Set up filter button
        filtrerButton.setOnAction(e -> filterRestaurantsByPrice());
        
        // Set up reset button
        resetButton.setOnAction(e -> {
            try {
                double highestPrice = restaurantService.getHighestRestaurantPrice();
                highestPrice = Math.ceil(highestPrice * 1.1);
                prixSlider.setValue(highestPrice);
                maxPrix = highestPrice;
            } catch (SQLException e1) {
                prixSlider.setValue(200);
                maxPrix = 200.0;
            }
            loadAllRestaurants();
        });
        
        // Load all restaurants initially
        loadAllRestaurants();
    }
    
    private void loadAllRestaurants() {
        try {
            allRestaurants = restaurantService.getAllRestaurants();
            displayRestaurants(allRestaurants);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des restaurants: " + e.getMessage());
        }
    }
    
    private void filterRestaurantsByPrice() {
        if (allRestaurants == null) {
            loadAllRestaurants();
            return;
        }
        
        List<Restaurant> filteredRestaurants = allRestaurants.stream()
                .filter(restaurant -> restaurant.getPrix() <= maxPrix)
                .collect(Collectors.toList());
        
        displayRestaurants(filteredRestaurants);
    }
    
    private void displayRestaurants(List<Restaurant> restaurants) {
        User currentUser = Session.getCurrentUser();
        restaurantContainer.getChildren().clear();
        
        if (restaurants.isEmpty()) {
            Label noResultsLabel = new Label("Aucun restaurant ne correspond à vos critères");
            noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
            restaurantContainer.getChildren().add(noResultsLabel);
            return;
        }

        for (Restaurant restaurant : restaurants) {
            VBox card = createRestaurantCard(restaurant);
            restaurantContainer.getChildren().add(card);
        }
    }

    private VBox createRestaurantCard(Restaurant restaurant) {
        VBox card = new VBox(10);
        card.setPadding(new javafx.geometry.Insets(15));
        card.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
        card.setMinWidth(320);
        card.setMaxWidth(320);
        
        // Create a stack pane for the image and promotion tag
        StackPane imageContainer = new StackPane();
        imageContainer.setMinHeight(200);
        imageContainer.setMaxHeight(200);
        
        // Restaurant image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(320);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        
        String imagePath = restaurant.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            }
        }
        
        imageContainer.getChildren().add(imageView);
        
        // Add promotion tag if applicable
        if (restaurant.isPromotion()) {
            Label promotionTag = new Label("PROMO");
            promotionTag.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; " +
                                 "-fx-padding: 5 10; -fx-background-radius: 3; -fx-font-size: 14px;");
            StackPane.setAlignment(promotionTag, javafx.geometry.Pos.TOP_RIGHT);
            StackPane.setMargin(promotionTag, new javafx.geometry.Insets(10));
            imageContainer.getChildren().add(promotionTag);
        }
        
        // Restaurant name
        Label nameLabel = new Label(restaurant.getNom());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        nameLabel.setWrapText(true);
        nameLabel.setMinHeight(30);
        
        // Restaurant location
        Label locationLabel = new Label(restaurant.getLocalisation());
        locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        locationLabel.setWrapText(true);
        
        // Restaurant description
        Label descriptionLabel = new Label(restaurant.getDescription());
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMinHeight(60);
        descriptionLabel.setMaxHeight(60);
        
        // Restaurant price with promotion indicator
        HBox priceBox = new HBox(10);
        priceBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        
        Label priceLabel = new Label(String.format("%.2f TND", restaurant.getPrix()));
        priceLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + 
                          (restaurant.isPromotion() ? "#e74c3c" : "#2980b9") + ";");
        
        priceBox.getChildren().add(priceLabel);
        
        if (restaurant.isPromotion()) {
            Label promoLabel = new Label("Prix en promotion!");
            promoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-font-style: italic;");
            priceBox.getChildren().add(promoLabel);
        }
        
        // Buttons container
        VBox buttonsBox = new VBox(8);
        buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Get current user role
        User currentUser = Session.getCurrentUser();
        String userRole = currentUser != null ? currentUser.getRole() : null;
        
        // First row of buttons
        HBox topButtonsRow = new HBox(8);
        topButtonsRow.setAlignment(javafx.geometry.Pos.CENTER);
        
        // Reserve button - All users except Restaurant owners can reserve
        if (!PermissionManager.ROLE_RESTAURANT.equals(userRole)) {
            Button reserveButton = new Button("Réserver");
            reserveButton.setPrefWidth(140);
            reserveButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
            reserveButton.setOnAction(e -> openReservationWindow(restaurant));
            topButtonsRow.getChildren().add(reserveButton);
        }
        
        // Map button - All users can view location
        Button mapButton = new Button("Voir sur la carte");
        mapButton.setPrefWidth(140);
        mapButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
        mapButton.setOnAction(e -> handleViewLocation(restaurant));
        topButtonsRow.getChildren().add(mapButton);
        
        // Add edit button for restaurant owners and admins if they own this restaurant
        if (PermissionManager.canEditRestaurantItems() && 
            (PermissionManager.isAdmin() || (currentUser != null && restaurant.getUserId() == currentUser.getId()))) {
            Button editButton = new Button("Modifier");
            editButton.setPrefWidth(140);
            editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
            editButton.setOnAction(e -> openEditRestaurant(restaurant));
            
            // If we have 2 buttons already, create a new row
            if (topButtonsRow.getChildren().size() >= 2) {
                buttonsBox.getChildren().add(topButtonsRow);
                topButtonsRow = new HBox(8);
                topButtonsRow.setAlignment(javafx.geometry.Pos.CENTER);
            }
            
            topButtonsRow.getChildren().add(editButton);
        }
        
        // Add delete button for restaurant owners and admins if they own this restaurant
        if (PermissionManager.canEditRestaurantItems() && 
            (PermissionManager.isAdmin() || (currentUser != null && restaurant.getUserId() == currentUser.getId()))) {
            Button deleteButton = new Button("Supprimer");
            deleteButton.setPrefWidth(140);
            deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
            deleteButton.setOnAction(e -> handleDelete(restaurant));
            
            // If we have 2 buttons already, create a new row
            if (topButtonsRow.getChildren().size() >= 2) {
                buttonsBox.getChildren().add(topButtonsRow);
                topButtonsRow = new HBox(8);
                topButtonsRow.setAlignment(javafx.geometry.Pos.CENTER);
            }
            
            topButtonsRow.getChildren().add(deleteButton);
        }
        
        // Add the top buttons row if it has any buttons
        if (!topButtonsRow.getChildren().isEmpty()) {
            buttonsBox.getChildren().add(topButtonsRow);
        }
        
        // Google Maps button - All users can open Google Maps
        Button googleMapsButton = new Button("Ouvrir dans Google Maps");
        googleMapsButton.setPrefWidth(290);
        googleMapsButton.setStyle("-fx-background-color: #4285F4; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 8 15;");
        googleMapsButton.setOnAction(e -> openInGoogleMaps(restaurant));
        buttonsBox.getChildren().add(googleMapsButton);
        
        // Add all elements to the card
        card.getChildren().addAll(
                imageContainer,
                nameLabel,
                locationLabel,
                descriptionLabel,
                priceBox,
                buttonsBox
        );
        
        return card;
    }

    private void handleViewLocation(Restaurant restaurant) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/MapView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            MapController mapController = loader.getController();
            
            // Load directions map with restaurant coordinates
            mapController.loadDirectionsMap(
                restaurant.getLat(), 
                restaurant.getLng(), 
                restaurant.getNom(), 
                restaurant.getLocalisation()
            );
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Itinéraire vers " + restaurant.getNom());
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la carte: " + e.getMessage());
        }
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

            loadAllRestaurants(); // Refresh after editing
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
                loadAllRestaurants(); // Refresh the view
                showAlert(Alert.AlertType.INFORMATION, "Restaurant supprimé avec succès!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void openInGoogleMaps(Restaurant restaurant) {
        double lat = restaurant.getLat();
        double lng = restaurant.getLng();
        
        if (lat == 0 && lng == 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur: Coordonnées du restaurant non définies");
            return;
        }
        
        // Create Google Maps URL
        String googleMapsUrl = String.format(
            "https://www.google.com/maps/search/?api=1&query=%f,%f", 
            lat, 
            lng
        );
        
        try {
            // Open URL in default browser
            Desktop.getDesktop().browse(new URI(googleMapsUrl));
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du navigateur: " + e.getMessage());
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
