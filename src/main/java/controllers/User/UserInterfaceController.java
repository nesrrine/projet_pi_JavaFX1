package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import utils.Session;

import java.io.IOException;

public class UserInterfaceController {
    @FXML private MenuItem createLogementMenuItem;
    @FXML private MenuItem   LogementMenuItem;

    @FXML private MenuItem myVlogsMenuItem;
    @FXML private MenuItem createVlogMenuItem;
    @FXML private MenuItem myTransportsMenuItem;
    @FXML private MenuItem  createTransportsMenuItem;
    @FXML private MenuItem myRestaurantMenuItem;
    @FXML private MenuItem  createRestaurantMenuItem;
    @FXML private VBox mainContent;
    @FXML private Button logoutButton;
    @FXML private ImageView profileIcon;
    @FXML private MenuBar menuBar;

    @FXML
    public void initialize() {
        loadPartial("/user/HomeUserWelcome.fxml");
        configureMenuBasedOnRole();
    }

    private void configureMenuBasedOnRole() {

        User currentUser = Session.getCurrentUser();

        if (currentUser != null) {
            String role = currentUser.getRole();

            if (!"Voyageur".equals(role)) {
                myVlogsMenuItem.setVisible(false);
                createVlogMenuItem.setVisible(false);
            }

            if (!"Transporteur".equals(role)) {
                myTransportsMenuItem.setVisible(false);
                createTransportsMenuItem.setVisible(false);
            }

            if (!"Restaurant".equals(role)) {
                myRestaurantMenuItem.setVisible(false);
                createRestaurantMenuItem.setVisible(false);
            }  if (!"Hôte".equals(role)) {
                LogementMenuItem.setVisible(false);
                createLogementMenuItem.setVisible(false);
            }
        } else {
            // Si l'utilisateur n'est pas connecté, cacher tout ce qui est spécifique
            myVlogsMenuItem.setVisible(false);
            createVlogMenuItem.setVisible(false);
            myTransportsMenuItem.setVisible(false);
            createTransportsMenuItem.setVisible(false);
        }}
    @FXML
    private void handleMyReclamationsrecus() {
        loadPartial("/user/ReclamationsRecues.fxml");
    }
    private boolean isTRestaurant() {
        User currentUser = Session.getCurrentUser();
        return currentUser != null && "Restaurant".equals(currentUser.getRole());
    }
    private boolean isTransporteur() {
        User currentUser = Session.getCurrentUser();
        return currentUser != null && "Transporteur".equals(currentUser.getRole());
    }
    private boolean isVoyageur() {
        User currentUser = Session.getCurrentUser();
        return currentUser != null && "Voyageur".equals(currentUser.getRole());
    }
    private boolean isHote() {
        User currentUser = Session.getCurrentUser();
        return currentUser != null && "Hote".equals(currentUser.getRole());
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
        if (!isVoyageur()) {
            showAlert("Accès refusé", "Seuls les voyageurs peuvent accéder à leurs vlogs.");
            return;
        }
        loadPartial("/user/MesVlogs.fxml");
    }

    @FXML
    private void handleCreateVlog() {
        if (!isVoyageur()) {
            showAlert("Accès refusé", "Seuls les voyageurs peuvent créer un vlog.");
            return;
        }
        loadPartial("/user/CreateVlog.fxml");
    }


    @FXML
    private void handleMyReclamations() {
        loadPartial("/user/MesReclamations.fxml");
    }




    @FXML
    private void handleMylogements() {
        if (!isHote()) {
            showAlert("Accès refusé", "Seuls les hote peuvent crrer à leurs logemenets.");
            return;}
        loadPartial("/user/ajoutLogement.fxml");
    }

    @FXML
    private void handlelistLogment() {
        if (!isHote()) {
            showAlert("Accès refusé", "Seuls les hote peuvent accéder à leurs logemenets.");
            return;}        loadPartial("/user/gestion_logement.fxml");
    }


    @FXML
    private void handleAllLogement() {
        loadPartial("/user/TouslesLougement.fxml");
    }

    @FXML
    private void handleCreateReclamation() {loadPartial("/user/CreateReclamation.fxml");
    }

    @FXML
    public void handleAllTransports() {
        loadPartial("/user/TousLesTransports.fxml");
    }

    @FXML
    private void handleMyTransports() {
        if (!isTransporteur()) {
            showAlert("Accès refusé", "Seuls les voir mes   peuvent accéder à leurs tansport.");
            return;

        }        loadPartial("/user/MesTransports.fxml");
    }

    @FXML

    private void handleCreateTransport() {
        if (!isTransporteur()) {
            showAlert("Accès refusé", "Seuls les creer   peuvent accéder à leurs tansport.");
            return;

        }
        loadPartial("/user/CreateTransport.fxml");
    }

    @FXML
    public void handleAllRestaurants() {
        loadPartial("/user/TousLesRestaurants.fxml");
    }

    @FXML
    private void handleMyRestaurants() {
        if (!isTRestaurant()) {
            showAlert("Accès refusé", "Seuls les restaurant   peuvent accéder à leurs restaurant.");
            return;

        }          loadPartial("/user/MesRestaurants.fxml");
    }

    @FXML
    private void handleCreateRestaurant() {
        if (!isTRestaurant()) {
            showAlert("Accès refusé", "Seuls les creer   peuvent accéder à leurs restaurant.");
            return;

        }        loadPartial("/user/CreateRestaurant.fxml");
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadPartial(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            mainContent.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
