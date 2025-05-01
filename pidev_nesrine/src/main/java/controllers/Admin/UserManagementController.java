package controllers.Admin;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.User;
import service.UserService;
import utils.RoleUtils;

import java.io.IOException;
import java.util.List;

public class UserManagementController {

    @FXML
    private ListView<HBox> userListView;

    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        loadUsers();
    }

    @FXML
    private void handleOpenSearch() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/UserSearch.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Recherche d'utilisateurs");
            stage.setScene(new Scene(root));
            stage.setResizable(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la recherche: " + e.getMessage());
        }
    }

    private void loadUsers() {
        userListView.getItems().clear();
        List<User> users = userService.display();

        for (User user : users) {
            // Créer le texte d'information de l'utilisateur avec son statut
            boolean isActive = user.isActive();
            System.out.println("Affichage de l'utilisateur " + user.getId() + " (" + user.getFirstName() + " " + user.getLastName() + ") - Statut actif: " + isActive);
            String statusText = isActive ? "Actif" : "Inactif";
            String statusStyle = isActive ? "-fx-fill: #4CAF50;" : "-fx-fill: #f44336;"; // Vert pour actif, rouge pour inactif

            Text userInfo = new Text(
                    user.getFirstName() + " " + user.getLastName() + " | " +
                    user.getEmail() + " | " +
                    user.getRole()
            );
            userInfo.setStyle("-fx-fill: #333333; -fx-font-size: 14px;");

            Text statusInfo = new Text(" | " + statusText);
            statusInfo.setStyle(statusStyle + " -fx-font-weight: bold; -fx-font-size: 14px;");

            HBox infoBox = new HBox(5, userInfo, statusInfo);
            infoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // Créer les boutons d'action
            Button toggleStatusBtn;
            if (user.isActive()) {
                toggleStatusBtn = new Button("Désactiver");
                toggleStatusBtn.setStyle("-fx-background-color: #FF9800; -fx-text-fill: white; -fx-background-radius: 5;");
                toggleStatusBtn.setOnAction(e -> {
                    userService.deactivateUser(user.getId());
                    loadUsers();
                });
            } else {
                toggleStatusBtn = new Button("Activer");
                toggleStatusBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-background-radius: 5;");
                toggleStatusBtn.setOnAction(e -> {
                    userService.activateUser(user.getId());
                    loadUsers();
                });
            }

            Button editBtn = new Button("Modifier");
            editBtn.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-background-radius: 5;");
            editBtn.setOnAction(e -> {
                handleEditUser(user);
            });

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-background-radius: 5;");
            deleteBtn.setOnAction(e -> {
                userService.delete(user.getId());
                loadUsers();
            });

            // Créer la boîte de boutons
            HBox buttonsBox = new HBox(10, toggleStatusBtn, editBtn, deleteBtn);
            buttonsBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

            // Créer la boîte principale
            HBox hbox = new HBox(20);
            hbox.getChildren().addAll(infoBox, buttonsBox);
            hbox.setStyle("-fx-padding: 10; -fx-background-color: " + (user.isActive() ? "#f9f9f9" : "#f5f5f5") + "; -fx-background-radius: 10;");
            hbox.setSpacing(20);
            hbox.setPrefWidth(650);

            // Configurer la disposition
            HBox.setHgrow(infoBox, javafx.scene.layout.Priority.ALWAYS);
            HBox.setHgrow(buttonsBox, javafx.scene.layout.Priority.NEVER);

            userListView.getItems().add(hbox);
        }

        if (userListView.getItems().isEmpty()) {
            Label emptyLabel = new Label("Aucun utilisateur à afficher.");
            userListView.setPlaceholder(emptyLabel);
        }
    }

    @FXML
    private void handleShowRoleStats() {
        // Récupérer tous les utilisateurs
        List<User> allUsers = userService.display();

        // Calculer le nombre d'utilisateurs par rôle
        java.util.Map<String, Integer> roleStats = new java.util.HashMap<>();

        // Initialiser le compteur pour tous les rôles disponibles
        for (String role : RoleUtils.getAvailableRoles()) {
            roleStats.put(role, 0);
        }

        // Compter les utilisateurs par rôle
        for (User user : allUsers) {
            String role = user.getRole();
            roleStats.put(role, roleStats.getOrDefault(role, 0) + 1);
        }

        // Créer une boîte de dialogue pour afficher les statistiques
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Statistiques des rôles");
        dialog.setHeaderText("Nombre d'utilisateurs par rôle");

        // Créer le contenu de la boîte de dialogue
        VBox content = new VBox(15);
        content.setStyle("-fx-padding: 20;");

        // Ajouter un titre
        Label titleLabel = new Label("Répartition des utilisateurs par rôle");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        content.getChildren().add(titleLabel);

        // Créer un graphique en camembert pour visualiser les statistiques
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        // Ajouter les données au graphique
        for (java.util.Map.Entry<String, Integer> entry : roleStats.entrySet()) {
            if (entry.getValue() > 0) { // N'ajouter que les rôles qui ont des utilisateurs
                pieChartData.add(new PieChart.Data(entry.getKey() + " (" + entry.getValue() + ")", entry.getValue()));
            }
        }

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Statistiques des rôles");
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        pieChart.setPrefHeight(300);
        pieChart.setPrefWidth(400);

        // Ajouter des tooltips pour afficher le pourcentage au survol
        for (final PieChart.Data data : pieChart.getData()) {
            double percentage = (data.getPieValue() / allUsers.size()) * 100;
            Tooltip tooltip = new Tooltip(String.format("%s: %.1f%%", data.getName(), percentage));
            Tooltip.install(data.getNode(), tooltip);

            // Ajouter un événement pour mettre en évidence la section au survol
            data.getNode().setOnMouseEntered(e -> data.getNode().setStyle("-fx-opacity: 0.8;"));
            data.getNode().setOnMouseExited(e -> data.getNode().setStyle("-fx-opacity: 1;"));
        }

        content.getChildren().add(pieChart);

        // Ajouter un séparateur
        Separator separator = new Separator();
        separator.setPrefWidth(400);
        content.getChildren().add(separator);

        // Ajouter un titre pour le tableau
        Label tableTitle = new Label("Détails des statistiques");
        tableTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        content.getChildren().add(tableTitle);

        // Créer un tableau pour afficher les statistiques détaillées
        TableView<RoleStat> statsTable = new TableView<>();
        statsTable.setPrefHeight(200);

        // Configurer les colonnes du tableau
        TableColumn<RoleStat, String> roleColumn = new TableColumn<>("Rôle");
        roleColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getRole()));
        roleColumn.setPrefWidth(150);

        TableColumn<RoleStat, Integer> countColumn = new TableColumn<>("Nombre d'utilisateurs");
        countColumn.setCellValueFactory(data -> new javafx.beans.property.SimpleIntegerProperty(data.getValue().getCount()).asObject());
        countColumn.setPrefWidth(150);

        TableColumn<RoleStat, String> percentColumn = new TableColumn<>("Pourcentage");
        percentColumn.setCellValueFactory(data -> {
            double percent = allUsers.size() > 0 ? (double) data.getValue().getCount() / allUsers.size() * 100 : 0;
            return new javafx.beans.property.SimpleStringProperty(String.format("%.1f%%", percent));
        });
        percentColumn.setPrefWidth(100);

        statsTable.getColumns().addAll(roleColumn, countColumn, percentColumn);

        // Ajouter les données au tableau
        for (java.util.Map.Entry<String, Integer> entry : roleStats.entrySet()) {
            statsTable.getItems().add(new RoleStat(entry.getKey(), entry.getValue()));
        }

        content.getChildren().add(statsTable);

        // Ajouter le nombre total d'utilisateurs
        Label totalLabel = new Label("Nombre total d'utilisateurs : " + allUsers.size());
        totalLabel.setStyle("-fx-font-weight: bold;");
        content.getChildren().add(totalLabel);

        // Configurer la boîte de dialogue
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().setPrefWidth(500);
        dialog.getDialogPane().setPrefHeight(700);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        // Afficher la boîte de dialogue
        dialog.showAndWait();
    }

    // Classe interne pour représenter les statistiques des rôles
    private static class RoleStat {
        private final String role;
        private final int count;

        public RoleStat(String role, int count) {
            this.role = role;
            this.count = count;
        }

        public String getRole() {
            return role;
        }

        public int getCount() {
            return count;
        }
    }



    /**
     * Gère l'action de modification d'un utilisateur
     * @param user L'utilisateur à modifier
     */
    private void handleEditUser(User user) {
        try {
            // Charger la vue de profil pour édition
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Profile.fxml"));
            Parent root = loader.load();

            // Récupérer le contrôleur et initialiser avec l'utilisateur sélectionné
            controllers.ProfileController controller = loader.getController();
            controller.initData(user, true); // true indique que c'est en mode édition

            // Créer une nouvelle scène
            Stage stage = new Stage();
            stage.setTitle("Modifier l'utilisateur: " + user.getFirstName() + " " + user.getLastName());
            stage.setScene(new Scene(root));

            // Configurer la fenêtre
            stage.setResizable(true);

            // Ajouter un événement pour rafraîchir la liste des utilisateurs après la fermeture
            stage.setOnHidden(e -> loadUsers());

            // Afficher la fenêtre
            stage.show();

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.WARNING ? "Avertissement" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
