package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.User;
import service.UserService;
import utils.Session;
import service.TwilioSMSService;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private final UserService userService = new UserService();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
            return;
        }

        User user = userService.getByEmail(email);

        // Vérifier si l'utilisateur existe
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Email ou mot de passe incorrect.");
            return;
        }

        // Vérifier si l'utilisateur est actif
        if (!user.isActive()) {
            showAlert(Alert.AlertType.ERROR, "Ce compte a été désactivé. Veuillez contacter l'administrateur.");
            return;
        }

        // Tenter la connexion
        if (userService.login(email, password)) {
            System.out.println("Login successful for user ID: " + user.getId()); // Debug log

            // Continuer avec la connexion
            proceedWithLogin(user);
        } else {
            showAlert(Alert.AlertType.ERROR, "Email ou mot de passe incorrect.");
        }
    }

    /**
     * Finalise le processus de connexion et charge l'interface appropriée
     * @param user L'utilisateur connecté
     */
    private void proceedWithLogin(User user) {
        Session.setCurrentUser(user);
        System.out.println("Session user ID after set: " + Session.getCurrentUser().getId()); // Debug log

        // Envoyer un SMS
        try {
            // Formater le numéro
            String specificPhoneNumber = "+21658414579";

            // Message avec les informations de l'utilisateur qui s'est connecté
            String message = "Alerte: L'utilisateur " + user.getFirstName() + " " + user.getLastName() +
                             " (ID: " + user.getId() + ") s'est connecté le " +
                             java.time.LocalDateTime.now().format(
                                 java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm"));

            TwilioSMSService.sendSMS(specificPhoneNumber, message);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'envoi du SMS: " + e.getMessage());
            e.printStackTrace();
        }

        showAlert(Alert.AlertType.INFORMATION, "Connexion réussie !");

        loadRoleUI(user.getRole());
    }

    private void loadRoleUI(String role) {
        String fxml;

        if (role.equalsIgnoreCase("Admin")) {
            fxml = "/Admin/AdminDashboard.fxml";
        } else {
            // Pour tous les utilisateurs non-admin, charger d'abord le chatbot
            fxml = "/User/Chatbot.fxml";
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'interface.");
        }
    }

    @FXML
    private void goToSignup() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Signup.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement de la page d'inscription.");
        }
    }

    @FXML
    private void goToForgotPassword() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ForgotPassword.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement de la page de récupération de mot de passe.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
