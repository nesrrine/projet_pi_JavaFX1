package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
<<<<<<< HEAD
=======
<<<<<<< HEAD
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import utils.Session;
=======
>>>>>>> Ons
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
<<<<<<< HEAD
=======
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
>>>>>>> Ons

import java.io.IOException;

public class UserInterfaceController {

    @FXML private VBox mainContent;
    @FXML private Button logoutButton;
    @FXML private ImageView profileIcon;
<<<<<<< HEAD
=======
<<<<<<< HEAD
    @FXML private MenuBar menuBar;
=======
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
>>>>>>> Ons

    @FXML
    public void initialize() {
        loadPartial("/user/HomeUserWelcome.fxml");
<<<<<<< HEAD
    }


=======
<<<<<<< HEAD
        configureMenuBasedOnRole();
    }

    private void configureMenuBasedOnRole() {
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            // Parcourir tous les menus
            for (Menu menu : menuBar.getMenus()) {
                // Pour chaque menu, parcourir ses items
                menu.getItems().removeIf(item -> {
                    String text = item.getText();
                    // Supprimer les options de création et de gestion personnelle
                    return text != null && (text.startsWith("Créer") || 
                           text.startsWith("Mes") ||
                           text.contains("modifier") ||
                           text.contains("supprimer"));
                });
            }
        }
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
=======
    }


>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
>>>>>>> Ons

    @FXML
    private void handleProfileClick() {
        loadPartial("/Profile.fxml");
    }

    @FXML
    private void handleAllVlogs() {
        loadPartial("/user/TousLesVlogs.fxml");
    }

    @FXML
    private void handleMyVlogs() {
<<<<<<< HEAD
=======
<<<<<<< HEAD
        if (checkVoyageurAccess()) return;
=======
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
>>>>>>> Ons
        loadPartial("/user/MesVlogs.fxml");
    }

    @FXML
    private void handleCreateVlog() {
<<<<<<< HEAD
=======
<<<<<<< HEAD
        if (checkVoyageurAccess()) return;
=======
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
>>>>>>> Ons
        loadPartial("/user/CreateVlog.fxml");
    }

    @FXML
    private void handleMyReclamations() {
<<<<<<< HEAD
=======
<<<<<<< HEAD
        if (checkVoyageurAccess()) return;
=======
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
>>>>>>> Ons
        loadPartial("/user/MesReclamations.fxml");
    }

    @FXML
<<<<<<< HEAD
=======
<<<<<<< HEAD
    private void handleMylogements() {
        if (checkVoyageurAccess()) return;
        loadPartial("/user/ajoutLogement.fxml");
    }

    @FXML
    private void handlelistLogment() {
        if (checkVoyageurAccess()) return;
        loadPartial("/user/gestion_logement.fxml");
    }


    @FXML
    private void handleAllLogement() {
        loadPartial("/user/TouslesLougement.fxml");
    }

    @FXML
    private void handleCreateReclamation() {
        if (checkVoyageurAccess()) return;
        loadPartial("/user/CreateReclamation.fxml");
    }

    @FXML
    public void handleAllTransports() {
        loadPartial("/user/TousLesTransports.fxml");
    }

    @FXML
    private void handleMyTransports() {
        if (checkVoyageurAccess()) return;
        loadPartial("/user/MesTransports.fxml");
    }

    @FXML
    private void handleCreateTransport() {
        if (checkVoyageurAccess()) return;
        loadPartial("/user/CreateTransport.fxml");
    }

    @FXML
    public void handleAllRestaurants() {
        loadPartial("/user/TousLesRestaurants.fxml");
    }

    @FXML
    private void handleMyRestaurants() {
        if (checkVoyageurAccess()) return;
        loadPartial("/user/MesRestaurants.fxml");
    }

    @FXML
    private void handleCreateRestaurant() {
        if (checkVoyageurAccess()) return;
        loadPartial("/user/CreateRestaurant.fxml");
    }

    private boolean checkVoyageurAccess() {
        User currentUser = Session.getCurrentUser();
        if (currentUser != null && "Voyageur".equals(currentUser.getRole())) {
            showAlert("Accès refusé", "En tant que voyageur, vous ne pouvez pas accéder à cette fonctionnalité.");
            return true;
        }
        return false;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

=======
>>>>>>> Ons
    private void handleCreateReclamation() {
        loadPartial("/user/CreateReclamation.fxml");
    }

<<<<<<< HEAD
=======
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
>>>>>>> Ons
    private void loadPartial(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
<<<<<<< HEAD
            mainContent.getChildren().setAll(content); // replaces VBox content
=======
<<<<<<< HEAD
            mainContent.getChildren().setAll(content);
=======
            mainContent.getChildren().setAll(content); // replaces VBox content
>>>>>>> 5d64a614c574df4fe6dc3b6cb57a914a15bab66a
>>>>>>> Ons
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
