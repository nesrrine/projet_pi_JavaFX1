package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class UserInterfaceController {

    @FXML private VBox mainContent;
    @FXML private Button logoutButton;
    @FXML private ImageView profileIcon;

    @FXML
    public void initialize() {
        loadPartial("/user/HomeUserWelcome.fxml");
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
        loadPartial("/user/MesVlogs.fxml");
    }

    @FXML
    private void handleCreateVlog() {
        loadPartial("/user/CreateVlog.fxml");
    }

    @FXML
    private void handleMyReclamations() {
        loadPartial("/user/MesReclamations.fxml");
    }

    @FXML
    private void handleCreateReclamation() {
        loadPartial("/user/CreateReclamation.fxml");
    }

    private void loadPartial(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            mainContent.getChildren().setAll(content); // replaces VBox content
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
