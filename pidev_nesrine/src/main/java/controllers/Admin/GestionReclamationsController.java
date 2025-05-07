package controllers.Admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Reclamation;
import service.ReclamationService;
import service.UserService;

import java.util.List;

public class GestionReclamationsController {

    @FXML private TableView<Reclamation> reclamationTable;
    @FXML private TableColumn<Reclamation, String> titreColumn;
    @FXML private TableColumn<Reclamation, String> statutColumn;
    @FXML private TableColumn<Reclamation, String> auteurColumn;
    @FXML private TableColumn<Reclamation, String> cibleColumn;
    @FXML private TableColumn<Reclamation, Void> actionsColumn;

    private final ReclamationService reclamationService = new ReclamationService();
    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        titreColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getTitre()));
        statutColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getStatut()));
        auteurColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(userService.getById(data.getValue().getAuteurId()).getFirstName()));
        cibleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(userService.getById(data.getValue().getCibleId()).getFirstName()));

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");

            {
                editBtn.getStyleClass().add("button-edit");
                deleteBtn.getStyleClass().add("button-delete");

                editBtn.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
                deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(10, editBtn, deleteBtn);
                    setGraphic(box);
                }
            }
        });

        loadReclamations();
    }

    private void loadReclamations() {
        List<Reclamation> list = reclamationService.display();
        reclamationTable.getItems().setAll(list);
    }

    private void handleEdit(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/EditReclamation.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            EditReclamationController controller = loader.getController();
            controller.setReclamation(reclamation);
            stage.setTitle("Modifier Statut");
            stage.setResizable(false);
            stage.showAndWait();
            loadReclamations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void handleDelete(Reclamation reclamation) {
        reclamationService.delete(reclamation.getId());
        loadReclamations();
    }
}
