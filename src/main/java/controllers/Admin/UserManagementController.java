package controllers.Admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import models.User;
import service.UserService;

import java.util.List;

public class UserManagementController {

    @FXML
    private ListView<HBox> userListView;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        loadUsers();
    }

    private void loadUsers() {
        userListView.getItems().clear();
        List<User> users = userService.display();

        for (User user : users) {


            Text userInfo = new Text(
                    user.getFirstName() + " " + user.getLastName() + " | " +
                            user.getEmail() + " | " +
                            user.getRole()
            );
            userInfo.setStyle("-fx-fill: #333333; -fx-font-size: 14px;");

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5;");
            deleteBtn.setOnAction(e -> {
                userService.delete(user.getId());
                loadUsers();
            });

            HBox hbox = new HBox(20, userInfo, deleteBtn);
            hbox.setStyle("-fx-padding: 10; -fx-background-color: #f9f9f9; -fx-background-radius: 10;");
            hbox.setSpacing(20);
            hbox.setPrefWidth(650);

            userListView.getItems().add(hbox);
        }

        if (userListView.getItems().isEmpty()) {
            Label emptyLabel = new Label("Aucun utilisateur à afficher.");
            userListView.setPlaceholder(emptyLabel);
        }
    }
}
