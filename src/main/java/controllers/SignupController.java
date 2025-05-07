package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.User;
import service.UserService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.regex.Pattern;

public class SignupController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField phoneField;
    @FXML
    private DatePicker birthDatePicker;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label firstNameError, lastNameError, emailError, passwordError,
            addressError, phoneError, birthDateError, roleError;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll("Client", "Admin","Hôte", "Transporteur", "Restaurant", "Voyageur");
    }

    @FXML
    private void handleSignup() {
        if (!validateFields()) return;

        User user = new User(
                firstNameField.getText().trim(),
                lastNameField.getText().trim(),
                emailField.getText().trim(),
                passwordField.getText().trim(),
                addressField.getText().trim(),
                phoneField.getText().trim(),
                birthDatePicker.getValue(),
                roleComboBox.getValue()
        );

        userService.signup(user);
        showAlert(Alert.AlertType.INFORMATION, "Inscription réussie !");
        goToLogin();
    }

    private boolean validateFields() {
        boolean isValid = true;

        // Reset styles and messages
        resetValidation();

        if (firstNameField.getText().trim().isEmpty()) {
            setError(firstNameField, firstNameError, "Le prénom est requis.");
            isValid = false;
        }

        if (lastNameField.getText().trim().isEmpty()) {
            setError(lastNameField, lastNameError, "Le nom est requis.");
            isValid = false;
        }

        String email = emailField.getText().trim();
        if (email.isEmpty() || !isValidEmail(email)) {
            setError(emailField, emailError, "Email invalide.");
            isValid = false;
        }

        String password = passwordField.getText().trim();
        if (password.isEmpty() || password.length() < 6) {
            setError(passwordField, passwordError, "Au moins 6 caractères.");
            isValid = false;
        }

        if (addressField.getText().trim().isEmpty()) {
            setError(addressField, addressError, "Adresse requise.");
            isValid = false;
        }

        String phone = phoneField.getText().trim();
        if (!phone.matches("\\d{8}")) {
            setError(phoneField, phoneError, "8 chiffres requis.");
            isValid = false;
        }

        if (birthDatePicker.getValue() == null) {
            birthDateError.setText("Date requise.");
            isValid = false;
        }

        if (roleComboBox.getValue() == null) {
            roleError.setText("Rôle requis.");
            isValid = false;
        }

        return isValid;
    }

    private void resetValidation() {
        resetField(firstNameField, firstNameError);
        resetField(lastNameField, lastNameError);
        resetField(emailField, emailError);
        resetField(passwordField, passwordError);
        resetField(addressField, addressError);
        resetField(phoneField, phoneError);
        birthDateError.setText("");
        roleError.setText("");
    }

    private void setError(Control field, Label errorLabel, String message) {
        field.setStyle("-fx-border-color: red; -fx-border-width: 1;");
        errorLabel.setText(message);
    }

    private void resetField(Control field, Label errorLabel) {
        field.setStyle(null);
        errorLabel.setText("");
    }


    private boolean isValidEmail(String email) {
        // Basic email pattern
        return Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", email);
    }

    private boolean isValidPhoneNumber(String phone) {
        return phone.matches("\\d{8}");
    }

    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement de la page de connexion.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
