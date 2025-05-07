package controllers.Admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Comment;
import models.Vlog;
import service.CommentService;
import service.UserService;
import service.VlogService;

import java.util.List;

public class etst {

    @FXML private TableView<Vlog> vlogTable;
    @FXML private TableColumn<Vlog, String> contentColumn;
    @FXML private TableColumn<Vlog, String> authorColumn;
    @FXML private TableColumn<Vlog, String> dateColumn;
    @FXML private TableColumn<Vlog, Void> actionsColumn;
    @FXML private TableColumn<Vlog, Void> commentsColumn; // colonne combinée : commentaires + suppression
    @FXML private TableColumn<Vlog, String> isReportedColumn;  // Nouvelle colonne signalement

    private final VlogService vlogService = new VlogService();
    private final UserService userService = new UserService();
    private final CommentService commentService = new CommentService();

    @FXML
    private void initialize() {
        // Initialisation des colonnes des vlogs
        contentColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getContent()));
        authorColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(userService.getById(data.getValue().getAuthorId()).getFirstName()));
        dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCreatedAt().toLocalDate().toString()));

        // Colonne des actions (supprimer vlog)
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");

            {
                deleteBtn.getStyleClass().add("button-delete");
                deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(10, deleteBtn);
                    setGraphic(actions);
                }
            }
        });

        // Colonne combinée : afficher les commentaires + bouton de suppression
        commentsColumn.setCellFactory(col -> new TableCell<>() {
            private final VBox vbox = new VBox(5);

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                vbox.getChildren().clear();

                if (empty) {
                    setGraphic(null);
                } else {
                    Vlog vlog = getTableView().getItems().get(getIndex());
                    List<Comment> comments = commentService.getCommentsByVlog(vlog.getId());

                    for (Comment comment : comments) {
                        Label commentLabel = new Label(comment.getContent());
                        Button deleteBtn = new Button("🗑");

                        deleteBtn.getStyleClass().add("button-delete-comment");
                        deleteBtn.setOnAction(e -> {
                            commentService.deleteComment(comment.getId());
                            loadVlogs(); // Recharger après suppression
                        });

                        HBox commentBox = new HBox(10, commentLabel, deleteBtn);
                        vbox.getChildren().add(commentBox);
                    }

                    setGraphic(vbox);
                }
            }
        });

        // Colonne du signalement
        isReportedColumn.setCellValueFactory(data -> {
            return new ReadOnlyStringWrapper(data.getValue().isReported() ? "Oui" : "Non");
        });

        // Colonne des actions de signalement : approuver ou ignorer un signalement
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button approveBtn = new Button("Approuver");
            private final Button rejectBtn = new Button("Ignorer");

            {
                approveBtn.getStyleClass().add("button-approve");
                rejectBtn.getStyleClass().add("button-reject");

                approveBtn.setOnAction(e -> handleApproveSignalement(getTableView().getItems().get(getIndex())));
                rejectBtn.setOnAction(e -> handleRejectSignalement(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(10, approveBtn, rejectBtn);
                    setGraphic(actions);
                }
            }
        });

        // Charger les vlogs
        loadVlogs();
    }

    private void loadVlogs() {
        vlogTable.getItems().setAll(vlogService.display());
    }

    private void handleDelete(Vlog vlog) {
        vlogService.delete(vlog.getId());
        loadVlogs();
    }

    private void handleApproveSignalement(Vlog vlog) {
        vlogService.delete(vlog.getId());  // Supprimer le vlog si signalé
        loadVlogs();  // Recharger la liste des vlogs après suppression
    }

    private void handleRejectSignalement(Vlog vlog) {
        vlog.setReported(false);
        vlogService.update(vlog);  // Met à jour le vlog pour le marquer comme non signalé
        loadVlogs();  // Recharger la liste des vlogs après mise à jour
    }
}
