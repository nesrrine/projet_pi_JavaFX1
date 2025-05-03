package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.User;
import service.UserService;
import service.EmailService;
import utils.RoleUtils;

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
        // Utiliser la méthode statique pour obtenir les rôles disponibles
        roleComboBox.getItems().addAll(RoleUtils.getAvailableRoles());
    }

    @FXML
    private void handleSignup() {
        if (!validateFields()) return;

        // Récupérer le numéro de téléphone
        String phoneNumber = phoneField.getText().trim();

        // Formater le numéro de téléphone (ajouter le préfixe +216 si nécessaire)
        if (phoneNumber.startsWith("0")) {
            phoneNumber = "+216" + phoneNumber.substring(1);
        } else if (!phoneNumber.startsWith("+")) {
            phoneNumber = "+216" + phoneNumber;
        }

        // Créer l'utilisateur
        User user = new User(
            firstNameField.getText().trim(),
            lastNameField.getText().trim(),
            emailField.getText().trim(),
            passwordField.getText().trim(),
            addressField.getText().trim(),
            phoneNumber,
            birthDatePicker.getValue(),
            roleComboBox.getValue()
        );

        // Inscrire l'utilisateur
        userService.signup(user);

        // Envoyer un email de bienvenue
        EmailService emailService = new EmailService();
        boolean emailSent = emailService.sendWelcomeEmail(
            user.getEmail(),
            user.getFirstName(),
            user.getLastName()
        );

        if (emailSent) {
            showAlert(Alert.AlertType.INFORMATION, "Inscription réussie ! Un email de bienvenue a été envoyé à votre adresse email.");
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Inscription réussie ! Mais l'envoi de l'email de bienvenue a échoué.");
        }

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
        if (!isValidPhoneNumber(phone)) {
            setError(phoneField, phoneError, "Format invalide. Ex: 12345678");
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
        // Supprimer les espaces, tirets et parenthèses
        phone = phone.replaceAll("[\\s\\-()]+", "");

        // Accepter les formats: 12345678, +21612345678, 00216123456789
        return phone.matches("\\d{8}") || // Format tunisien standard
               phone.matches("\\+216\\d{8}") || // Format international avec +
               phone.matches("00216\\d{8}"); // Format international avec 00
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
