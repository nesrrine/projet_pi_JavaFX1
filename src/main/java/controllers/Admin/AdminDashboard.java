package controllers.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboard {

<<<<<<< HEAD
    @FXML private Button gestionUserButton;
    @FXML private Button gestionReclamationButton;
    @FXML private Button gestionVlogButton;
    @FXML private Button gestionTransportButton;
    @FXML private Button gestionRestaurantButton;
    @FXML private Button logoutButton;
    @FXML private ImageView profileIcon;
    @FXML private StackPane contentArea;
=======
    @FXML
    private Button gestionUserButton;
    @FXML
    private Button gestionReclamationButton;

    @FXML
    private Button gestionVlogButton;

    @FXML
    private Button logoutButton;

    @FXML
    private ImageView profileIcon;

    @FXML
    private StackPane contentArea;
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a

    private Button currentActiveButton;

    @FXML
    public void initialize() {
        currentActiveButton = gestionUserButton;
        updateButtonStyles();

        try {
            loadUserManagement();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateButtonStyles() {
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #333333; -fx-font-weight: bold;" +
                " -fx-background-radius: 5; -fx-border-color: #e0e0e0; -fx-border-radius: 5;";
        String activeStyle = "-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;" +
                " -fx-background-radius: 5;";

        gestionUserButton.setStyle(defaultStyle);
<<<<<<< HEAD
        gestionReclamationButton.setStyle(defaultStyle);
        gestionVlogButton.setStyle(defaultStyle);
        gestionTransportButton.setStyle(defaultStyle);
        gestionRestaurantButton.setStyle(defaultStyle);
=======
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a

        if (currentActiveButton != null) {
            currentActiveButton.setStyle(activeStyle);
        }
    }

    @FXML
    private void handleGestionUser() throws IOException {
        currentActiveButton = gestionUserButton;
        updateButtonStyles();
        loadUserManagement();
    }

    @FXML
    private void handleProfileClick() {
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profile.fxml"));
            Parent profileView = loader.load();
            contentArea.getChildren().add(profileView);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la page de profil.");
        }
    }

    @FXML
    private void handleLogout() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.show();
    }

    private void loadUserManagement() throws IOException {
<<<<<<< HEAD
=======
        currentActiveButton = gestionUserButton;
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
        contentArea.getChildren().clear();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/UserManagement.fxml"));
        Parent userView = loader.load();
        contentArea.getChildren().add(userView);
    }
<<<<<<< HEAD

    @FXML
    private void handleGestionReclamation() {
        currentActiveButton = gestionReclamationButton;
        updateButtonStyles();
=======
    @FXML
    private void handleGestionReclamation() {
        currentActiveButton = gestionReclamationButton;
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/GestionReclamations.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la gestion des réclamations.");
        }
    }

    @FXML
    private void handleGestionVlog() {
        currentActiveButton = gestionVlogButton;
<<<<<<< HEAD
        updateButtonStyles();
=======
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/GestionVlogs.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la gestion des vlogs.");
        }
    }

<<<<<<< HEAD
    @FXML
    private void handleGestionTransport() {
        currentActiveButton = gestionTransportButton;
        updateButtonStyles();
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/GestionTransports.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la gestion des transports.");
        }
    }

    @FXML
    private void handleGestionRestaurant() {
        currentActiveButton = gestionRestaurantButton;
        updateButtonStyles();
        try {
            contentArea.getChildren().clear();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/GestionRestaurants.fxml"));
            Parent view = loader.load();
            contentArea.getChildren().add(view);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de la gestion des restaurants.");
        }
    }
=======
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
}
