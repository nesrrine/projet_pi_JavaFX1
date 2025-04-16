package controllers.User;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import models.Transport;
import models.User;
import service.TransportService;
import service.UserService;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CreateTransportController {

    @FXML private TextField typeField;
    @FXML private TextArea descriptionField;
    @FXML private TextField prixField;
    @FXML private TextField imageField;
    @FXML private Button submitButton;
    @FXML private Button chooseImageButton;

    private final TransportService transportService = new TransportService();
    private final UserService userService = new UserService();
    private Transport transportToEdit;
    private File selectedFile;

    @FXML
    public void initialize() {
        // Check user role and disable editing for "Voyageur"
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            disableEditing();
        }

        // Add listeners for real-time validation
        typeField.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        prixField.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
        imageField.textProperty().addListener((observable, oldValue, newValue) -> validateAndStyle());
    }

    private void disableEditing() {
        typeField.setDisable(true);
        descriptionField.setDisable(true);
        prixField.setDisable(true);
        imageField.setDisable(true);
        submitButton.setVisible(false);
        chooseImageButton.setVisible(false);
    }

    @FXML
    private void handleChooseImage() {
        // Check user role
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            showError("Accès refusé", "Les voyageurs ne peuvent pas modifier les transports.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File file = fileChooser.showOpenDialog(chooseImageButton.getScene().getWindow());
        if (file != null) {
            selectedFile = file;
            imageField.setText(file.getAbsolutePath());
            validateAndStyle();
        }
    }

    @FXML
    private void handleCreate() {
        // Check user role
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            showError("Accès refusé", "Les voyageurs ne peuvent pas créer ou modifier les transports.");
            return;
        }

        List<String> errors = validateFields();
        if (!errors.isEmpty()) {
            showError("Erreurs de validation", String.join("\n", errors));
            return;
        }

        // Check if user is logged in
        if (currentUser == null) {
            showError("Erreur d'authentification", "Vous devez être connecté pour créer un transport.");
            return;
        }

        // Verify user exists in database
        User dbUser = userService.getById(currentUser.getId());
        if (dbUser == null) {
            showError("Erreur d'authentification", "Utilisateur non trouvé dans la base de données.");
            return;
        }

        try {
            Transport transport = transportToEdit != null ? transportToEdit : new Transport();
            transport.setType(typeField.getText().trim());
            transport.setDescription(descriptionField.getText().trim());
            transport.setPrix(Double.parseDouble(prixField.getText().trim()));
            transport.setImage(imageField.getText().trim());
            
            if (transportToEdit == null) {
                transport.setUserId(dbUser.getId());
                transportService.addTransport(transport);
                showSuccess("Transport créé avec succès!");
                
                // Fermer la fenêtre actuelle
                Stage currentStage = (Stage) typeField.getScene().getWindow();
                currentStage.close();

                // Ouvrir UserInterface seulement pour l'ajout
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/UserInterface.fxml"));
                    Scene scene = new Scene(loader.load());
                    Stage stage = new Stage();
                    stage.setTitle("Interface Utilisateur");
                    stage.setScene(scene);
                    
                    // Récupérer le contrôleur et naviguer vers la section des transports
                    UserInterfaceController controller = loader.getController();
                    controller.initialize();
                    controller.handleAllTransports();
                    
                    stage.show();
                } catch (IOException e) {
                    showError("Erreur", "Erreur lors de l'ouverture de l'interface utilisateur: " + e.getMessage());
                }
            } else {
                transportService.updateTransport(transport);
                showSuccess("Transport modifié avec succès!");
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

    public void setTransportToEdit(Transport transport) {
        // Check user role before allowing edit
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            showError("Accès refusé", "Les voyageurs ne peuvent pas modifier les transports.");
            return;
        }

        this.transportToEdit = transport;
        populateFields();
        submitButton.setText("Modifier");
    }

    private List<String> validateFields() {
        List<String> errors = new ArrayList<>();
        
        // Type validation
        String type = typeField.getText().trim();
        if (type.isEmpty()) {
            errors.add("Le type est requis");
        } else if (type.length() < 3) {
            errors.add("Le type doit contenir au moins 3 caractères");
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

        // Image validation
        String imagePath = imageField.getText().trim();
        if (imagePath.isEmpty()) {
            errors.add("L'image est requise");
        } else {
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                errors.add("Le fichier image n'existe pas");
            }
        }

        return errors;
    }

    private void validateAndStyle() {
        List<String> errors = validateFields();
        
        // Update field styles
        updateFieldStyle(typeField, errors.stream().anyMatch(e -> e.contains("type")));
        updateFieldStyle(descriptionField, errors.stream().anyMatch(e -> e.contains("description")));
        updateFieldStyle(prixField, errors.stream().anyMatch(e -> e.contains("prix")));
        updateFieldStyle(imageField, errors.stream().anyMatch(e -> e.contains("image")));
    }

    private void updateFieldStyle(TextInputControl field, boolean hasError) {
        if (hasError) {
            field.getStyleClass().add("error");
        } else {
            field.getStyleClass().remove("error");
        }
    }

    private void clearFields() {
        typeField.clear();
        descriptionField.clear();
        prixField.clear();
        imageField.clear();
        selectedFile = null;
        transportToEdit = null;
        validateAndStyle();
    }

    private void closeWindow() {
        typeField.getScene().getWindow().hide();
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

    private void populateFields() {
        if (transportToEdit != null) {
            typeField.setText(transportToEdit.getType());
            descriptionField.setText(transportToEdit.getDescription());
            prixField.setText(String.valueOf(transportToEdit.getPrix()));
            imageField.setText(transportToEdit.getImage());
            validateAndStyle();
        }
    }
}
