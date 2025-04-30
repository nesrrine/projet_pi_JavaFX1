package controllers.User;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
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
    @FXML private TextArea descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField imageField;
    @FXML private TextField image1Field;
    @FXML private TextField image2Field;
    @FXML private Button submitButton;
    @FXML private Button chooseImageButton;
    @FXML private Button chooseImage1Button;
    @FXML private Button chooseImage2Button;
    @FXML private Label titleLabel;

    private final RestaurantService restaurantService = new RestaurantService();
    private final UserService userService = new UserService();
    private Restaurant restaurantToEdit;
    private File selectedFile;
    private File selectedFile1;
    private File selectedFile2;

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
    }

    private void disableEditing() {
        nomField.setDisable(true);
        localisationField.setDisable(true);
        descriptionField.setDisable(true);
        prixField.setDisable(true);
        imageField.setDisable(true);
        image1Field.setDisable(true);
        image2Field.setDisable(true);
        submitButton.setVisible(false);
        chooseImageButton.setVisible(false);
        chooseImage1Button.setVisible(false);
        chooseImage2Button.setVisible(false);
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
            Restaurant restaurant = restaurantToEdit != null ? restaurantToEdit : new Restaurant();
            restaurant.setNom(nomField.getText().trim());
            restaurant.setLocalisation(localisationField.getText().trim());
            restaurant.setDescription(descriptionField.getText().trim());
            restaurant.setPrix(Double.parseDouble(prixField.getText().trim()));
            restaurant.setImage(imageField.getText().trim());
            restaurant.setImage1(image1Field.getText().trim());
            restaurant.setImage2(image2Field.getText().trim());
            
            if (restaurantToEdit == null) {
                restaurant.setUserId(dbUser.getId());
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
                clearFields();
                closeWindow();
            }
        } catch (SQLException e) {
            showError("Erreur lors de l'opération", e.getMessage());
            System.err.println("SQL Error: " + e.getMessage());
            e.printStackTrace();
        } catch (NumberFormatException e) {
            showError("Erreur de format", "Le prix doit être un nombre valide");
        }
    }

    public void setRestaurantToEdit(Restaurant restaurant) {
        // Check user role before allowing edit
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            showError("Accès refusé", "Les voyageurs ne peuvent pas modifier les restaurants.");
            return;
        }

        this.restaurantToEdit = restaurant;
        populateFields();
        submitButton.setText("Modifier");
        titleLabel.setText("Modifier le Restaurant");
    }

    private void populateFields() {
        if (restaurantToEdit != null) {
            nomField.setText(restaurantToEdit.getNom());
            localisationField.setText(restaurantToEdit.getLocalisation());
            descriptionField.setText(restaurantToEdit.getDescription());
            prixField.setText(String.valueOf(restaurantToEdit.getPrix()));
            imageField.setText(restaurantToEdit.getImage());
            image1Field.setText(restaurantToEdit.getImage1());
            image2Field.setText(restaurantToEdit.getImage2());
            validateAndStyle();
        }
    }

    private List<String> validateFields() {
        List<String> errors = new ArrayList<>();
        
        // Nom validation
        String nom = nomField.getText().trim();
        if (nom.isEmpty()) {
            errors.add("Le nom est requis");
        } else if (nom.length() < 3) {
            errors.add("Le nom doit contenir au moins 3 caractères");
        }

        // Localisation validation
        String localisation = localisationField.getText().trim();
        if (localisation.isEmpty()) {
            errors.add("La localisation est requise");
        } else if (localisation.length() < 5) {
            errors.add("La localisation doit contenir au moins 5 caractères");
        }

        // Description validation
        String description = descriptionField.getText().trim();
        if (description.isEmpty()) {
            errors.add("La description est requise");
        } else if (description.length() < 10) {
            errors.add("La description doit contenir au moins 10 caractères");
        }

        // Prix validation
        String prix = prixField.getText().trim();
        if (prix.isEmpty()) {
            errors.add("Le prix est requis");
        } else {
            try {
                double prixValue = Double.parseDouble(prix);
                if (prixValue <= 0) {
                    errors.add("Le prix doit être supérieur à 0");
                }
            } catch (NumberFormatException e) {
                errors.add("Le prix doit être un nombre valide");
            }
        }

        // Image principale validation
        String imagePath = imageField.getText().trim();
        if (imagePath.isEmpty()) {
            errors.add("L'image principale est requise");
        } else {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                errors.add("Le fichier image principale n'existe pas");
            }
        }

        // Images additionnelles validation (optionnelles)
        validateOptionalImage(image1Field.getText().trim(), "deuxième", errors);
        validateOptionalImage(image2Field.getText().trim(), "troisième", errors);

        return errors;
    }

    private void validateOptionalImage(String path, String imageNumber, List<String> errors) {
        if (!path.isEmpty()) {
            File imageFile = new File(path);
            if (!imageFile.exists()) {
                errors.add("La " + imageNumber + " image n'existe pas");
            }
        }
    }

    private void validateAndStyle() {
        List<String> errors = validateFields();
        
        // Update field styles
        updateFieldStyle(nomField, errors.stream().anyMatch(e -> e.contains("nom")));
        updateFieldStyle(localisationField, errors.stream().anyMatch(e -> e.contains("localisation")));
        updateFieldStyle(descriptionField, errors.stream().anyMatch(e -> e.contains("description")));
        updateFieldStyle(prixField, errors.stream().anyMatch(e -> e.contains("prix")));
        updateFieldStyle(imageField, errors.stream().anyMatch(e -> e.contains("principale")));
        updateFieldStyle(image1Field, errors.stream().anyMatch(e -> e.contains("deuxième")));
        updateFieldStyle(image2Field, errors.stream().anyMatch(e -> e.contains("troisième")));
    }

    private void updateFieldStyle(TextInputControl field, boolean hasError) {
        if (hasError) {
            field.getStyleClass().add("error");
        } else {
            field.getStyleClass().remove("error");
        }
    }

    private void clearFields() {
        nomField.clear();
        localisationField.clear();
        descriptionField.clear();
        prixField.clear();
        imageField.clear();
        image1Field.clear();
        image2Field.clear();
        selectedFile = null;
        selectedFile1 = null;
        selectedFile2 = null;
        restaurantToEdit = null;
        validateAndStyle();
    }

    private void closeWindow() {
        nomField.getScene().getWindow().hide();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
