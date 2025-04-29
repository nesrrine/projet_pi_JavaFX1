package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
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
import java.util.ArrayList;
import java.util.List;

public class CreateRestaurantController {

    @FXML private TextField nomField;
    @FXML private TextField localisationField;
    @FXML private TextField latField;
    @FXML private TextField lngField;
    @FXML private TextArea descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField imageField;
    @FXML private TextField image1Field;
    @FXML private TextField image2Field;
    @FXML private Button submitButton;
    @FXML private Button chooseImageButton;
    @FXML private Button chooseImage1Button;
    @FXML private Button chooseImage2Button;
    @FXML private Button selectLocationButton;
    @FXML private Button cancelButton;
    @FXML private Label titleLabel;
    @FXML private CheckBox promotionCheckBox;

    private final RestaurantService restaurantService = new RestaurantService();
    private final UserService userService = new UserService();
    private Restaurant restaurantToEdit;
    private File selectedFile;
    private File selectedFile1;
    private File selectedFile2;
    private double latitude = 0;
    private double longitude = 0;

    @FXML
    public void initialize() {
        // Check user role and disable editing for "Voyageur"
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            disableEditing();
        }

        // Add listeners for real-time validation
        nomField.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        localisationField.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        prixField.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        imageField.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        image1Field.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        image2Field.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        
        // Add listeners for latitude and longitude fields
        latField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    latitude = Double.parseDouble(newValue);
                }
                validateAndStyle();
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        });
        
        lngField.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                if (!newValue.isEmpty()) {
                    longitude = Double.parseDouble(newValue);
                }
                validateAndStyle();
            } catch (NumberFormatException e) {
                // Ignore invalid input
            }
        });
        
        // Set up cancel button
        cancelButton.setOnAction(event -> {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();
        });
        
        // Initialize fields if editing an existing restaurant
        if (restaurantToEdit != null) {
            latitude = restaurantToEdit.getLat();
            longitude = restaurantToEdit.getLng();
            latField.setText(String.valueOf(latitude));
            lngField.setText(String.valueOf(longitude));
        }
    }

    private void disableEditing() {
        nomField.setDisable(true);
        localisationField.setDisable(true);
        descriptionField.setDisable(true);
        prixField.setDisable(true);
        imageField.setDisable(true);
        image1Field.setDisable(true);
        image2Field.setDisable(true);
        latField.setDisable(true);
        lngField.setDisable(true);
        submitButton.setVisible(false);
        chooseImageButton.setVisible(false);
        chooseImage1Button.setVisible(false);
        chooseImage2Button.setVisible(false);
        selectLocationButton.setVisible(false);
    }
    
    @FXML
    private void handleSelectLocation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/MapView.fxml"));
            Parent root = loader.load();
            
            MapController mapController = loader.getController();
            
            // Get current coordinates if available
            double initialLat = 0;
            double initialLng = 0;
            
            if (!latField.getText().isEmpty() && !lngField.getText().isEmpty()) {
                try {
                    initialLat = Double.parseDouble(latField.getText());
                    initialLng = Double.parseDouble(lngField.getText());
                } catch (NumberFormatException e) {
                    // Use default coordinates
                    System.err.println("Error parsing coordinates: " + e.getMessage());
                }
            }
            
            System.out.println("Opening map with initial coordinates: " + initialLat + ", " + initialLng);
            
            // Create a new stage for the map
            Stage stage = new Stage();
            stage.setTitle("Sélectionner l'emplacement du restaurant");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            
            // Set up callback to receive selected location
            mapController.loadLocationPicker(initialLat, initialLng, (lat, lng) -> {
                System.out.println("Location callback received: " + lat + ", " + lng);
                
                // Update the latitude and longitude fields directly
                latitude = lat;
                longitude = lng;
                latField.setText(String.format("%.6f", lat));
                lngField.setText(String.format("%.6f", lng));
                validateAndStyle();
                
                System.out.println("Updated latitude/longitude fields: " + latField.getText() + ", " + lngField.getText());
            });
            
            // Show the map and wait for it to close
            stage.showAndWait();
            
        } catch (IOException e) {
            showError("Erreur lors de l'ouverture de la carte", "Erreur lors de l'ouverture de la carte: " + e.getMessage());
        }
    }

    @FXML
    private void handleChooseImage() {
        // Check user role
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            showError("Accès refusé", "Les voyageurs ne peuvent pas modifier les restaurants.");
            return;
        }

        File file = showImageFileChooser("Image principale");
        if (file != null) {
            selectedFile = file;
            imageField.setText(file.getAbsolutePath());
            validateAndStyle();
        }
    }

    @FXML
    private void handleChooseImage1() {
        // Check user role
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            showError("Accès refusé", "Les voyageurs ne peuvent pas modifier les restaurants.");
            return;
        }

        File file = showImageFileChooser("Deuxième image");
        if (file != null) {
            selectedFile1 = file;
            image1Field.setText(file.getAbsolutePath());
            validateAndStyle();
        }
    }

    @FXML
    private void handleChooseImage2() {
        // Check user role
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            showError("Accès refusé", "Les voyageurs ne peuvent pas modifier les restaurants.");
            return;
        }

        File file = showImageFileChooser("Troisième image");
        if (file != null) {
            selectedFile2 = file;
            image2Field.setText(file.getAbsolutePath());
            validateAndStyle();
        }
    }

    private File showImageFileChooser(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner " + title);
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        return fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
    }

    @FXML
    private void handleCreate() {
        // Check user role
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            showError("Accès refusé", "Les voyageurs ne peuvent pas créer ou modifier les restaurants.");
            return;
        }

        List<String> errors = validateFields();
        if (!errors.isEmpty()) {
            showError("Erreurs de validation", String.join("\n", errors));
            return;
        }

        // Check if user is logged in
        if (currentUser == null) {
            showError("Erreur d'authentification", "Vous devez être connecté pour gérer les restaurants.");
            return;
        }

        // Verify user exists in database
        User dbUser = userService.getById(currentUser.getId());
        if (dbUser == null) {
            showError("Erreur d'authentification", "Utilisateur non trouvé dans la base de données.");
            return;
        }

        try {
            String nom = nomField.getText().trim();
            String localisation = localisationField.getText().trim();
            String image = imageField.getText().trim();
            String description = descriptionField.getText().trim();
            double prix = Double.parseDouble(prixField.getText().trim());
            String image1 = image1Field.getText().trim();
            String image2 = image2Field.getText().trim();
            boolean promotion = promotionCheckBox.isSelected();
            
            Restaurant restaurant;
            if (restaurantToEdit == null) {
                restaurant = new Restaurant(nom, localisation, image, description, prix, latitude, longitude, image1, image2, currentUser.getId(), promotion);
            } else {
                restaurant = restaurantToEdit;
                restaurant.setNom(nom);
                restaurant.setLocalisation(localisation);
                restaurant.setImage(image);
                restaurant.setDescription(description);
                restaurant.setPrix(prix);
                restaurant.setLat(latitude);
                restaurant.setLng(longitude);
                restaurant.setImage1(image1);
                restaurant.setImage2(image2);
                restaurant.setPromotion(promotion);
            }
            
            if (restaurantToEdit == null) {
                restaurantService.addRestaurant(restaurant);
                showSuccess("Restaurant créé avec succès!");
                
                // Fermer la fenêtre actuelle
                Stage currentStage = (Stage) nomField.getScene().getWindow();
                currentStage.close();

                // Ouvrir UserInterface seulement pour l'ajout
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/UserInterface.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = new Stage();
                    stage.setTitle("Interface Utilisateur");
                    stage.setScene(scene);
                    
                    // Récupérer le contrôleur et naviguer vers la section des restaurants
                    UserInterfaceController controller = loader.getController();
                    controller.initialize();
                    controller.handleAllRestaurants();
                    
                    stage.show();
                } catch (IOException e) {
                    showError("Erreur", "Erreur lors de l'ouverture de l'interface utilisateur: " + e.getMessage());
                }
            } else {
                restaurantService.updateRestaurant(restaurant);
                showSuccess("Restaurant modifié avec succès!");
                
                // Fermer la fenêtre actuelle
                Stage currentStage = (Stage) nomField.getScene().getWindow();
                currentStage.close();
            }
        } catch (SQLException e) {
            showError("Erreur de base de données", "Erreur lors de l'enregistrement du restaurant: " + e.getMessage());
        } catch (NumberFormatException e) {
            showError("Erreur de format", "Le prix doit être un nombre valide.");
        }
    }

    private List<String> validateFields() {
        List<String> errors = new ArrayList<>();
        
        if (nomField.getText().trim().length() < 3) {
            errors.add("Le nom doit contenir au moins 3 caractères.");
        }
        
        if (localisationField.getText().trim().length() < 5) {
            errors.add("La localisation doit contenir au moins 5 caractères.");
        }
        
        if (descriptionField.getText().trim().length() < 10) {
            errors.add("La description doit contenir au moins 10 caractères.");
        }
        
        if (imageField.getText().trim().isEmpty()) {
            errors.add("L'image principale est obligatoire.");
        }
        
        try {
            double prix = Double.parseDouble(prixField.getText().trim());
            if (prix <= 0) {
                errors.add("Le prix doit être un nombre positif.");
            }
        } catch (NumberFormatException e) {
            errors.add("Le prix doit être un nombre valide.");
        }
        
        if (latitude == 0 && longitude == 0) {
            errors.add("Veuillez sélectionner l'emplacement du restaurant sur la carte.");
        }
        
        return errors;
    }

    private void validateAndStyle() {
        boolean isValid = validateFields().isEmpty();
        submitButton.setDisable(!isValid);
    }

    public void setRestaurantToEdit(Restaurant restaurant) {
        this.restaurantToEdit = restaurant;
        
        if (restaurant != null) {
            titleLabel.setText("Modifier un Restaurant");
            submitButton.setText("Mettre à jour");
            
            nomField.setText(restaurant.getNom());
            localisationField.setText(restaurant.getLocalisation());
            imageField.setText(restaurant.getImage());
            descriptionField.setText(restaurant.getDescription());
            prixField.setText(String.valueOf(restaurant.getPrix()));
            image1Field.setText(restaurant.getImage1());
            image2Field.setText(restaurant.getImage2());
            latitude = restaurant.getLat();
            longitude = restaurant.getLng();
            latField.setText(String.valueOf(latitude));
            lngField.setText(String.valueOf(longitude));
            promotionCheckBox.setSelected(restaurant.isPromotion());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
