package controllers.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.User;
import service.UserService;
import utils.RoleUtils;

import java.util.List;

public class UserSearchController {

    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField emailField;
    @FXML private TextField lastNameField;
    @FXML private TextField firstNameField;
    @FXML private Button searchButton;
    @FXML private Button resetButton;

    @FXML private TableView<User> userTableView;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> firstNameColumn;
    @FXML private TableColumn<User, String> lastNameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Boolean> statusColumn;

    private final UserService userService = new UserService();
    private ObservableList<User> usersList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Initialiser les colonnes du tableau
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Personnaliser l'affichage de la colonne de statut
        statusColumn.setCellFactory(column -> new TableCell<User, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item ? "Actif" : "Inactif");
                    setStyle("-fx-text-fill: " + (item ? "green" : "red") + "; -fx-font-weight: bold;");
                }
            }
        });

        // Initialiser le ComboBox des rôles en utilisant la méthode statique
        roleComboBox.setItems(FXCollections.observableArrayList(RoleUtils.getAvailableRoles()));


        // Charger tous les utilisateurs au démarrage
        loadAllUsers();
    }



    @FXML
    private void handleSearch() {
        String role = roleComboBox.getValue();
        String email = emailField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();

        // Vérifier si au moins un critère de recherche est rempli
        if ((role == null || role.isEmpty()) &&
            email.isEmpty() &&
            lastName.isEmpty() &&
            firstName.isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Veuillez saisir au moins un critère de recherche.");
            return;
        }

        // Effectuer la recherche
        List<User> searchResults = userService.searchUsers(role, email, lastName, firstName);
        usersList.setAll(searchResults);
        userTableView.setItems(usersList);

        // Afficher un message si aucun résultat n'est trouvé
        if (searchResults.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Aucun utilisateur ne correspond à ces critères.");
        }
    }

    @FXML
    private void handleReset() {
        // Réinitialiser les champs de recherche
        roleComboBox.setValue(null);
        emailField.clear();
        lastNameField.clear();
        firstNameField.clear();

        // Recharger tous les utilisateurs
        loadAllUsers();
    }

    private void loadAllUsers() {
        List<User> allUsers = userService.display();
        usersList.setAll(allUsers);
        userTableView.setItems(usersList);
    }



    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.WARNING ? "Avertissement" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
