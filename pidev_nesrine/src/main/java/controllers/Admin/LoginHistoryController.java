package controllers.Admin;

// Imports pour l'exportation HTML (alternative à PDF)
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.LoginHistory;
import models.User;
import service.LoginHistoryService;
import service.UserService;

import java.awt.Desktop;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class LoginHistoryController {

    @FXML private ComboBox<String> filterComboBox;
    @FXML private ComboBox<User> userComboBox;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    @FXML private TableView<LoginHistory> historyTableView;
    @FXML private TableColumn<LoginHistory, Integer> idColumn;
    @FXML private TableColumn<LoginHistory, String> userColumn;
    @FXML private TableColumn<LoginHistory, String> dateTimeColumn;
    @FXML private TableColumn<LoginHistory, String> statusColumn;
    @FXML private TableColumn<LoginHistory, String> ipAddressColumn;
    @FXML private TableColumn<LoginHistory, String> userAgentColumn;

    @FXML private Label totalEntriesLabel;

    private final LoginHistoryService loginHistoryService = new LoginHistoryService();
    private final UserService userService = new UserService();
    private ObservableList<LoginHistory> historyList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        // Initialiser les colonnes du tableau
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        userColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUserName()));

        dateTimeColumn.setCellValueFactory(data -> {
            LocalDateTime dateTime = data.getValue().getLoginTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            return new SimpleStringProperty(dateTime.format(formatter));
        });

        statusColumn.setCellValueFactory(data -> {
            boolean success = data.getValue().isSuccess();
            return new SimpleStringProperty(success ? "Réussie" : "Échouée");
        });

        // Personnaliser l'affichage du statut
        statusColumn.setCellFactory(column -> new TableCell<LoginHistory, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Réussie")) {
                        setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    }
                }
            }
        });

        ipAddressColumn.setCellValueFactory(new PropertyValueFactory<>("ipAddress"));
        userAgentColumn.setCellValueFactory(new PropertyValueFactory<>("userAgent"));

        // Initialiser le ComboBox des utilisateurs
        List<User> users = userService.display();
        userComboBox.setItems(FXCollections.observableArrayList(users));
        userComboBox.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user == null ? "Tous les utilisateurs" : user.getFirstName() + " " + user.getLastName();
            }

            @Override
            public User fromString(String string) {
                return null; // Non utilisé
            }
        });

        // Initialiser le ComboBox de filtre
        filterComboBox.setItems(FXCollections.observableArrayList("Tous", "Réussies", "Échouées"));
        filterComboBox.getSelectionModel().selectFirst();

        // Forcer la création de la table et l'ajout de données de test si nécessaire
        LoginHistoryService service = new LoginHistoryService();

        // Charger l'historique initial
        loadLoginHistory();

        // Si aucune donnée n'est affichée, afficher un message
        if (historyList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION,
                    "Aucune donnée d'historique disponible. Connectez-vous plusieurs fois pour générer des données, " +
                    "ou redémarrez l'application pour générer des données de test.");
        }
    }

    @FXML
    private void handleFilter() {
        loadLoginHistory();
    }

    @FXML
    private void handleReset() {
        filterComboBox.getSelectionModel().selectFirst();
        userComboBox.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);

        loadLoginHistory();
    }

    @FXML
    private void handleExport() {
        if (historyList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Aucune donnée à exporter.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter l'historique des connexions");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Fichiers HTML", "*.html"),
            new FileChooser.ExtensionFilter("Fichiers CSV", "*.csv")
        );

        // Définir un nom de fichier par défaut avec la date actuelle
        String defaultFileName = "historique_connexions_" +
                                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".html";
        fileChooser.setInitialFileName(defaultFileName);

        File file = fileChooser.showSaveDialog(historyTableView.getScene().getWindow());
        if (file != null) {
            if (file.getName().endsWith(".html")) {
                exportToHTML(file);
            } else if (file.getName().endsWith(".csv")) {
                exportToCSV(file);
            }
        }
    }

    @FXML
    private void handleClear() {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation");
        confirmDialog.setHeaderText("Effacer l'historique des connexions");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir effacer tout l'historique des connexions ? Cette action est irréversible.");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                User selectedUser = userComboBox.getValue();
                boolean success;

                if (selectedUser != null) {
                    // Effacer l'historique d'un utilisateur spécifique
                    success = loginHistoryService.clearUserLoginHistory(selectedUser.getId());
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "L'historique des connexions de " +
                                 selectedUser.getFirstName() + " " + selectedUser.getLastName() +
                                 " a été effacé avec succès.");
                    }
                } else {
                    // Effacer tout l'historique
                    success = loginHistoryService.clearAllLoginHistory();
                    if (success) {
                        showAlert(Alert.AlertType.INFORMATION, "L'historique des connexions a été effacé avec succès.");
                    }
                }

                if (success) {
                    loadLoginHistory();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Une erreur est survenue lors de l'effacement de l'historique.");
                }
            }
        });
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/AdminDashboard.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) historyTableView.getScene().getWindow();
            stage.setScene(scene);
            stage.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement du tableau de bord administrateur.");
        }
    }

    private void loadLoginHistory() {
        // Récupérer tous les historiques de connexion
        List<LoginHistory> allHistory = loginHistoryService.getAllLoginHistory(0);

        System.out.println("Nombre d'entrées d'historique récupérées: " + allHistory.size());

        // Afficher les données brutes pour débogage
        for (LoginHistory entry : allHistory) {
            System.out.println("Entrée: " + entry);
        }

        // Appliquer les filtres
        List<LoginHistory> filteredHistory = allHistory.stream()
            .filter(entry -> filterByStatus(entry))
            .filter(entry -> filterByUser(entry))
            .filter(entry -> filterByDateRange(entry))
            .collect(Collectors.toList());

        System.out.println("Après filtrage: " + filteredHistory.size() + " entrées");

        // Mettre à jour la liste observable
        historyList.setAll(filteredHistory);
        historyTableView.setItems(historyList);

        // Mettre à jour le label du total
        totalEntriesLabel.setText("Total: " + historyList.size() + " entrées");
    }

    private boolean filterByStatus(LoginHistory entry) {
        String filter = filterComboBox.getValue();
        if (filter == null || filter.equals("Tous")) {
            return true;
        }

        return (filter.equals("Réussies") && entry.isSuccess()) ||
               (filter.equals("Échouées") && !entry.isSuccess());
    }

    private boolean filterByUser(LoginHistory entry) {
        User selectedUser = userComboBox.getValue();
        if (selectedUser == null) {
            return true;
        }

        return entry.getUserId() == selectedUser.getId();
    }

    private boolean filterByDateRange(LoginHistory entry) {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null && endDate == null) {
            return true;
        }

        LocalDate entryDate = entry.getLoginTime().toLocalDate();

        if (startDate != null && endDate != null) {
            return !entryDate.isBefore(startDate) && !entryDate.isAfter(endDate);
        } else if (startDate != null) {
            return !entryDate.isBefore(startDate);
        } else {
            return !entryDate.isAfter(endDate);
        }
    }

    private void exportToHTML(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Créer un document HTML avec un style CSS intégré
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>\n")
                .append("<html>\n")
                .append("<head>\n")
                .append("    <meta charset=\"UTF-8\">\n")
                .append("    <title>Historique des connexions</title>\n")
                .append("    <style>\n")
                .append("        body { font-family: Arial, sans-serif; margin: 20px; }\n")
                .append("        h1 { color: #333; text-align: center; }\n")
                .append("        .date { text-align: right; color: #777; font-style: italic; margin-bottom: 20px; }\n")
                .append("        table { width: 100%; border-collapse: collapse; margin-bottom: 20px; }\n")
                .append("        th { background-color: #2c3e50; color: white; padding: 10px; text-align: left; }\n")
                .append("        td { padding: 8px; border-bottom: 1px solid #ddd; }\n")
                .append("        tr:nth-child(even) { background-color: #f2f2f2; }\n")
                .append("        .success { color: #2e7d32; font-weight: bold; }\n")
                .append("        .failure { color: #c62828; font-weight: bold; }\n")
                .append("        .summary { margin-top: 20px; font-weight: bold; }\n")
                .append("        .stats { margin-top: 10px; }\n")
                .append("    </style>\n")
                .append("</head>\n")
                .append("<body>\n")
                .append("    <h1>Historique des connexions</h1>\n")
                .append("    <div class=\"date\">Généré le: ")
                .append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .append("</div>\n")
                .append("    <table>\n")
                .append("        <thead>\n")
                .append("            <tr>\n")
                .append("                <th>ID</th>\n")
                .append("                <th>Utilisateur</th>\n")
                .append("                <th>Date et heure</th>\n")
                .append("                <th>Statut</th>\n")
                .append("                <th>Adresse IP</th>\n")
                .append("                <th>Agent utilisateur</th>\n")
                .append("            </tr>\n")
                .append("        </thead>\n")
                .append("        <tbody>\n");

            // Ajouter les données
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            for (LoginHistory entry : historyList) {
                html.append("            <tr>\n")
                    .append("                <td>").append(entry.getId()).append("</td>\n")
                    .append("                <td>").append(entry.getUserName()).append("</td>\n")
                    .append("                <td>").append(entry.getLoginTime().format(formatter)).append("</td>\n")
                    .append("                <td class=\"")
                    .append(entry.isSuccess() ? "success\">Réussie" : "failure\">Échouée")
                    .append("</td>\n")
                    .append("                <td>").append(entry.getIpAddress()).append("</td>\n")
                    .append("                <td>").append(entry.getUserAgent()).append("</td>\n")
                    .append("            </tr>\n");
            }

            html.append("        </tbody>\n")
                .append("    </table>\n");

            // Ajouter un résumé et des statistiques
            long successCount = historyList.stream().filter(LoginHistory::isSuccess).count();
            long failureCount = historyList.size() - successCount;
            double successPercent = (double) successCount / historyList.size() * 100;
            double failurePercent = (double) failureCount / historyList.size() * 100;

            html.append("    <div class=\"summary\">Nombre total de connexions: ").append(historyList.size()).append("</div>\n")
                .append("    <div class=\"stats\">")
                .append("Connexions réussies: ").append(successCount).append(" (")
                .append(String.format("<span class=\"success\">%.1f%%</span>", successPercent))
                .append(") | Connexions échouées: ").append(failureCount).append(" (")
                .append(String.format("<span class=\"failure\">%.1f%%</span>", failurePercent))
                .append(")")
                .append("</div>\n")
                .append("</body>\n")
                .append("</html>");

            // Écrire le contenu HTML dans le fichier
            writer.write(html.toString());

            showAlert(Alert.AlertType.INFORMATION, "L'historique des connexions a été exporté avec succès en HTML.");

            // Ouvrir le fichier HTML dans le navigateur par défaut
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(file.toURI());
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'exportation de l'historique en HTML: " + e.getMessage());
        }
    }

    private void exportToCSV(File file) {
        try (FileWriter writer = new FileWriter(file)) {
            // Écrire l'en-tête
            writer.write("ID,Utilisateur,Date et heure,Statut,Adresse IP,Agent utilisateur\n");

            // Écrire les données
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            for (LoginHistory entry : historyList) {
                writer.write(String.format("%d,\"%s\",\"%s\",\"%s\",\"%s\",\"%s\"\n",
                    entry.getId(),
                    entry.getUserName(),
                    entry.getLoginTime().format(formatter),
                    entry.isSuccess() ? "Réussie" : "Échouée",
                    entry.getIpAddress(),
                    entry.getUserAgent()
                ));
            }

            showAlert(Alert.AlertType.INFORMATION, "L'historique des connexions a été exporté avec succès en CSV.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'exportation de l'historique en CSV: " + e.getMessage());
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
