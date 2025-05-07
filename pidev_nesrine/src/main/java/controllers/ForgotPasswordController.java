package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import service.UserService;

import java.io.IOException;

public class ForgotPasswordController {

    @FXML private TextField emailField;
    @FXML private Button resetButton;
    @FXML private Label messageLabel;

    private final UserService userService = new UserService();

    @FXML
    private void handleResetRequest() {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez entrer votre adresse e-mail.");
            return;
        }
        
        // Vérifier si l'email est valide (format basique)
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.WARNING, "Veuillez entrer une adresse e-mail valide.");
            return;
        }
        
        // Générer un token de réinitialisation
        String token = userService.generatePasswordResetToken(email);
        
        if (token != null) {
            // Afficher le token (dans une application réelle, on enverrait un email)
            messageLabel.setText("Un lien de réinitialisation a été envoyé à votre adresse e-mail.\n\nPour les besoins de démonstration, voici votre token: " + token);
            
            // Désactiver le bouton pour éviter les demandes multiples
            resetButton.setDisable(true);
            
            // Rediriger vers la page de réinitialisation après un délai (simulation d'envoi d'email)
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    javafx.application.Platform.runLater(() -> {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
                            Scene scene = new Scene(loader.load());
                            
                            ResetPasswordController controller = loader.getController();
                            controller.setToken(token);
                            
                            Stage stage = (Stage) emailField.getScene().getWindow();
                            stage.setScene(scene);
                            stage.sizeToScene();
                        } catch (IOException e) {
                            e.printStackTrace();
                            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de la page de réinitialisation.");
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        } else {
            showAlert(Alert.AlertType.ERROR, "Aucun compte n'est associé à cette adresse e-mail.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de la page de connexion.");
        }
    }
    
    private boolean isValidEmail(String email) {
        // Validation basique de l'email
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
