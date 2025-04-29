package controllers.User;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Restaurant;
import models.User;
import service.RestaurantService;
import service.UserService;
import utils.Session;

import java.io.File;

public class RestaurantDetailsController {

    @FXML private Label titleLabel;
    @FXML private Label ownerLabel;
    @FXML private Label localisationLabel;
    @FXML private Label prixLabel;
    @FXML private TextArea descriptionArea;
    @FXML private ImageView mainImageView;
    @FXML private ImageView image1View;
    @FXML private ImageView image2View;
    @FXML private Button reserveButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button locationButton;
    @FXML private Button closeButton;
    
    private Restaurant restaurant;
    private final UserService userService = new UserService();
    private final RestaurantService restaurantService = new RestaurantService();
    
    @FXML
    public void initialize() {
        // Hide edit/delete buttons by default
        editButton.setVisible(false);
        deleteButton.setVisible(false);
        
        // Set up close button
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });
        
        // Set up location button
        locationButton.setOnAction(event -> handleViewLocation());
        
        // Set up reserve button
        reserveButton.setOnAction(event -> handleReservation());
        
        // Set up edit button
        editButton.setOnAction(event -> handleEdit());
        
        // Set up delete button
        deleteButton.setOnAction(event -> handleDelete());
    }
    
    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
        
        // Populate UI with restaurant data
        titleLabel.setText(restaurant.getNom());
        
        // Get owner info
        User owner = userService.getById(restaurant.getUserId());
        ownerLabel.setText("Propriétaire: " + owner.getFirstName() + " " + owner.getLastName());
        
        localisationLabel.setText("Adresse: " + restaurant.getLocalisation());
        prixLabel.setText(String.format("Prix moyen: %.2f TND", restaurant.getPrix()));
        descriptionArea.setText(restaurant.getDescription());
        
        // Load images
        loadImage(mainImageView, restaurant.getImage(), 400, 250);
        loadImage(image1View, restaurant.getImage1(), 200, 120);
        loadImage(image2View, restaurant.getImage2(), 200, 120);
        
        // Check if current user is the owner
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && currentUser.getId() == restaurant.getUserId()) {
            editButton.setVisible(true);
            deleteButton.setVisible(true);
        }
    }
    
    private void loadImage(ImageView imageView, String imagePath, double width, double height) {
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(true);
        
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                    return;
                }
            }
            // Use default image if path is invalid or file doesn't exist
            imageView.setImage(new Image("/images/default-restaurant.jpg"));
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
            imageView.setImage(new Image("/images/default-restaurant.jpg"));
        }
    }
    
    private void handleViewLocation() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/User/MapView.fxml"));
            javafx.scene.Parent root = loader.load();
            
            MapController mapController = loader.getController();
            
            // Load directions map with restaurant coordinates
            mapController.loadDirectionsMap(
                restaurant.getLat(), 
                restaurant.getLng(), 
                restaurant.getNom(), 
                restaurant.getLocalisation()
            );
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Itinéraire vers " + restaurant.getNom());
            stage.setScene(scene);
            stage.show();
            
        } catch (java.io.IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la carte: " + e.getMessage());
        }
    }
    
    private void handleReservation() {
        // Implement reservation functionality
        showAlert(Alert.AlertType.INFORMATION, "La fonctionnalité de réservation sera bientôt disponible!");
    }
    
    private void handleEdit() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/User/CreateRestaurant.fxml"));
            javafx.scene.Parent root = loader.load();
            
            CreateRestaurantController controller = loader.getController();
            controller.setRestaurantToEdit(restaurant);
            
            javafx.scene.Scene scene = new javafx.scene.Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modifier le restaurant");
            stage.setScene(scene);
            stage.show();
            
            // Close current window
            Stage currentStage = (Stage) editButton.getScene().getWindow();
            currentStage.close();
            
        } catch (java.io.IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire de modification: " + e.getMessage());
        }
    }
    
    private void handleDelete() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation de suppression");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce restaurant?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    restaurantService.deleteRestaurant(restaurant.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Restaurant supprimé avec succès!");
                    
                    // Close window
                    Stage stage = (Stage) deleteButton.getScene().getWindow();
                    stage.close();
                    
                } catch (java.sql.SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression: " + e.getMessage());
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
