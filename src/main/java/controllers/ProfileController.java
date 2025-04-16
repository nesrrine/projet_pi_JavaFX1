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

    @FXML
    private void initialize() {
        User user = Session.getCurrentUser();

        if (user != null) {
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
            emailField.setText(user.getEmail());
            addressField.setText(user.getAddress());
            phoneField.setText(user.getPhone());
            birthDatePicker.setValue(user.getBirthDate());
            roleComboBox.getItems().add(user.getRole());
            roleComboBox.setValue(user.getRole());
        }
    }

    @FXML
    private void handleUpdateProfile() {
        User currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Aucun utilisateur connecté.");
            return;
        }

        // Update user object with new values
        currentUser.setFirstName(firstNameField.getText());
        currentUser.setLastName(lastNameField.getText());
        currentUser.setEmail(emailField.getText());
        currentUser.setAddress(addressField.getText());
        currentUser.setPhone(phoneField.getText());
        currentUser.setBirthDate(birthDatePicker.getValue());

        String newPassword = passwordField.getText();
        if (!newPassword.isEmpty()) {
            currentUser.setPassword(newPassword); // UserService will hash it
        }

        userService.update(currentUser);
        showAlert(Alert.AlertType.INFORMATION, "Profil mis à jour avec succès.");
    }

    @FXML
    private void handleDeleteAccount() {
        User currentUser = Session.getCurrentUser();

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Aucun utilisateur connecté.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setHeaderText("Êtes-vous sûr de vouloir supprimer votre compte ?");
        confirm.setContentText("Cette action est irréversible.");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userService.delete(currentUser.getId());
                Session.clear();
                closeWindow();
            }
        });
    }
    @FXML
    private void handleBack() {
        try {
            String fxml = Session.getCurrentUser().getRole().equalsIgnoreCase("Admin")
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
