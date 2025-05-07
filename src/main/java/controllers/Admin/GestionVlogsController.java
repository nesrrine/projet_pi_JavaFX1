package controllers.Admin;

import controllers.AnalyseSentimentController;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Comment;
import models.Vlog;
import service.CommentService;
import service.UserService;
import service.VlogService;

import java.io.IOException;
import java.util.List;

public class GestionVlogsController {

    @FXML
    private TableView<Vlog> vlogTable;
    @FXML
    private TableColumn<Vlog, String> contentColumn;
    @FXML
    private TableColumn<Vlog, String> authorColumn;
    @FXML
    private TableColumn<Vlog, String> dateColumn;
    @FXML
    private TableColumn<Vlog, Void> actionsColumn;
    @FXML
    private TableColumn<Vlog, String> isReportedColumn;
    @FXML
    private TableColumn<Vlog, String> commentColumn;  // Nouvelle colonne pour afficher "Signaler"

    private final VlogService vlogService = new VlogService();
    private final UserService userService = new UserService();
    private final CommentService commentService = new CommentService();

    @FXML
    private void initialize() {
        contentColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getContent()));
        authorColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                userService.getById(data.getValue().getAuthorId()).getFirstName()));
        dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(
                data.getValue().getCreatedAt().toLocalDate().toString()));

        // Logique des actions : Suppression et Analyse
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final Button analyseBtn = new Button("Analyser");

            {
                deleteBtn.getStyleClass().add("button-delete");
                analyseBtn.getStyleClass().add("button-analyse");

                deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
                analyseBtn.setOnAction(e -> handleAnalyseSentimentForVlog(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, analyseBtn, deleteBtn);
                    setGraphic(buttons);
                }
            }
        });

        // Colonne de signalement avec bouton
        isReportedColumn.setCellFactory(col -> new TableCell<>() {
            private final Button reportBtn = new Button("Signaler");

            {
                reportBtn.getStyleClass().add("button-report");
                reportBtn.setOnAction(e -> handleReport(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Vlog vlog = getTableView().getItems().get(getIndex());
                    if (vlog.isReported()) {
                        reportBtn.setText("Annuler le signalement");
                    } else {
                        reportBtn.setText("Signaler");
                    }
                    setGraphic(reportBtn);  // Afficher le bouton dans la cellule
                }
            }
        });

        loadVlogs();  // Charger les vlogs au démarrage
    }

    // Méthode pour charger les vlogs dans la TableView
    private void loadVlogs() {
        vlogTable.getItems().setAll(vlogService.display());
    }

    private void handleDelete(Vlog vlog) {
        vlogService.delete(vlog.getId());
        loadVlogs();  // Recharger les vlogs après la suppression
    }

    @FXML
    private void handleAnalyseSentiment() {
        List<Comment> commentaires = commentService.getAllComments();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/AnalyseSentimentView.fxml"));
            Parent root = loader.load();

            AnalyseSentimentController controller = loader.getController();
            controller.analyserEtAfficherParVlog(commentaires);

            Stage stage = new Stage();
            stage.setTitle("Analyse des Sentiments par Vlog");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleAnalyseSentimentForVlog(Vlog vlog) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Admin/AnalyseSentimentView.fxml"));
            Parent root = loader.load();

            // Récupérer les commentaires du vlog
            List<Comment> allComments = commentService.getAllComments();
            List<Comment> commentsDuVlog = allComments.stream()
                    .filter(c -> c.getVlogId() == vlog.getId())
                    .toList();

            // Passer les commentaires à la vue
            AnalyseSentimentController controller = loader.getController();
            controller.analyserEtAfficherParVlog(commentsDuVlog);

            // Afficher la fenêtre
            Stage stage = new Stage();
            stage.setTitle("Analyse des Sentiments - Vlog ID: " + vlog.getId());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode de signalement du vlog
    private void handleReport(Vlog vlog) {
        if (vlog.isReported()) {
            // Annuler le signalement
            vlog.setReported(false);
            showAlert("Signalement annulé", "Le signalement de ce vlog a été annulé.");
        } else {
            // Signaler le vlog
            vlog.setReported(true);
            showAlert("Vlog signalé", "Ce vlog a été signalé pour examen.");
        }

        // Mettre à jour le vlog dans la base de données
        vlogService.update(vlog);
        loadVlogs(); // Recharger la liste des vlogs après mise à jour
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
