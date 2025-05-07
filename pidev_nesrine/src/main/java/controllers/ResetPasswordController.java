package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.UserService;

import java.io.IOException;

public class ResetPasswordController {

    @FXML private TextField tokenField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();
    private String prefilledToken;

    @FXML
    private void initialize() {
        if (prefilledToken != null) {
            tokenField.setText(prefilledToken);
        }
    }

    public void setToken(String token) {
        this.prefilledToken = token;
        if (tokenField != null) {
            tokenField.setText(token);
        }
    }

    @FXML
    private void handleResetPassword() {
        String token = tokenField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Vérifier que tous les champs sont remplis
        if (token.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
            return;
        }
        
        // Vérifier que les mots de passe correspondent
        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.WARNING, "Les mots de passe ne correspondent pas.");
            return;
        }
        
        // Vérifier que le mot de passe est suffisamment fort
        if (password.length() < 6) {
            showAlert(Alert.AlertType.WARNING, "Le mot de passe doit contenir au moins 6 caractères.");
            return;
        }
        
        // Réinitialiser le mot de passe
        boolean success = userService.resetPassword(token, password);
        
        if (success) {
            messageLabel.setStyle("-fx-text-fill: #4CAF50;");
            messageLabel.setText("Votre mot de passe a été réinitialisé avec succès.");
            
            // Rediriger vers la page de connexion après un délai
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(this::handleBackToLogin);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            messageLabel.setStyle("-fx-text-fill: #f44336;");
            messageLabel.setText("Le code de réinitialisation est invalide ou a expiré.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) tokenField.getScene().getWindow();
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de la page de connexion.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
