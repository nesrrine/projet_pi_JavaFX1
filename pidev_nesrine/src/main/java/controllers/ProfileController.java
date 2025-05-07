package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import service.UserService;
import utils.RoleUtils;
import utils.Session;

import java.io.IOException;

public class ProfileController {

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField phoneField;
    @FXML private DatePicker birthDatePicker;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private PasswordField passwordField;
    @FXML
    private VBox mainBox;
    private final UserService userService = new UserService();
    private User userToEdit;
    private boolean isEditMode = false;

    @FXML
    private void initialize() {
        // Initialiser le ComboBox des rôles
        roleComboBox.getItems().addAll(RoleUtils.getAvailableRoles());

        // Si aucun utilisateur n'est passé en paramètre, utiliser l'utilisateur courant
        if (userToEdit == null) {
            User user = Session.getCurrentUser();
            if (user != null) {
                loadUserData(user);
            }
        }
    }

    /**
     * Initialise le contrôleur avec un utilisateur spécifique pour édition
     * @param user L'utilisateur à éditer
     * @param editMode true si en mode édition, false sinon
     */
    public void initData(User user, boolean editMode) {
        this.userToEdit = user;
        this.isEditMode = editMode;

        if (user != null) {
            loadUserData(user);
        }
    }

    /**
     * Charge les données d'un utilisateur dans le formulaire
     * @param user L'utilisateur dont les données doivent être chargées
     */
    private void loadUserData(User user) {
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
        addressField.setText(user.getAddress());
        phoneField.setText(user.getPhone());
        birthDatePicker.setValue(user.getBirthDate());
        roleComboBox.setValue(user.getRole());
    }

    @FXML
    private void handleUpdateProfile() {
        User userToUpdate;

        // Déterminer quel utilisateur mettre à jour
        if (isEditMode && userToEdit != null) {
            userToUpdate = userToEdit;
        } else {
            userToUpdate = Session.getCurrentUser();
            if (userToUpdate == null) {
                showAlert(Alert.AlertType.ERROR, "Aucun utilisateur connecté.");
                return;
            }
        }

        // Mettre à jour l'objet utilisateur avec les nouvelles valeurs
        userToUpdate.setFirstName(firstNameField.getText());
        userToUpdate.setLastName(lastNameField.getText());
        userToUpdate.setEmail(emailField.getText());
        userToUpdate.setAddress(addressField.getText());
        userToUpdate.setPhone(phoneField.getText());
        userToUpdate.setBirthDate(birthDatePicker.getValue());

        // Mettre à jour le rôle si en mode édition
        if (isEditMode) {
            userToUpdate.setRole(roleComboBox.getValue());
        }

        // Mettre à jour le mot de passe si nécessaire
        String newPassword = passwordField.getText();
        if (!newPassword.isEmpty()) {
            userToUpdate.setPassword(newPassword); // UserService will hash it
        }

        // Enregistrer les modifications
        userService.update(userToUpdate);

        // Afficher un message de confirmation
        showAlert(Alert.AlertType.INFORMATION, "Profil mis à jour avec succès.");

        // Fermer la fenêtre si en mode édition
        if (isEditMode) {
            closeWindow();
        }
    }

    @FXML
    private void handleDeleteAccount() {
        User userToDelete;

        // Déterminer quel utilisateur supprimer
        if (isEditMode && userToEdit != null) {
            userToDelete = userToEdit;
        } else {
            userToDelete = Session.getCurrentUser();
            if (userToDelete == null) {
                showAlert(Alert.AlertType.ERROR, "Aucun utilisateur connecté.");
                return;
            }
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);

        if (isEditMode) {
            confirm.setHeaderText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");
            confirm.setContentText("Vous allez supprimer le compte de " + userToDelete.getFirstName() + " " + userToDelete.getLastName() + ". Cette action est irréversible.");
        } else {
            confirm.setHeaderText("Êtes-vous sûr de vouloir supprimer votre compte ?");
            confirm.setContentText("Cette action est irréversible.");
        }

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.delete(userToDelete.getId());

                if (!isEditMode) {
                    // Si c'est l'utilisateur courant qui supprime son propre compte
                    Session.clear();
                }

                closeWindow();
            }
        });
    }
    @FXML
    private void handleBack() {
        // Si en mode édition, simplement fermer la fenêtre
        if (isEditMode) {
            closeWindow();
            return;
        }

        // Sinon, retourner au tableau de bord approprié
        try {
            User currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                closeWindow();
                return;
            }

            String fxml = currentUser.getRole().equalsIgnoreCase("Admin")
                    ? "/Admin/AdminDashboard.fxml"
                    : "/User/UserInterface.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Stage stage = (Stage) mainBox.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.sizeToScene();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du retour au tableau de bord: " + e.getMessage());
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) firstNameField.getScene().getWindow();
        stage.close(); // Or redirect to login if you're embedding this in a main app
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
