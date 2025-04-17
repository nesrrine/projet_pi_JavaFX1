package controllers.Admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import models.Vlog;
import service.UserService;
import service.VlogService;

import java.util.List;

public class GestionVlogsController {

    @FXML private TableView<Vlog> vlogTable;
    @FXML private TableColumn<Vlog, String> contentColumn;
    @FXML private TableColumn<Vlog, String> authorColumn;
    @FXML private TableColumn<Vlog, String> dateColumn;
    @FXML private TableColumn<Vlog, Void> actionsColumn;

    private final VlogService vlogService = new VlogService();
    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        contentColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getContent()));
        authorColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(userService.getById(data.getValue().getAuthorId()).getFirstName()));
        dateColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getCreatedAt().toLocalDate().toString()));

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
                    HBox actions = new HBox(10,  deleteBtn);
                    setGraphic(actions);
                }
            }
        });

        loadVlogs();
    }

    private void loadVlogs() {
        vlogTable.getItems().setAll(vlogService.display());
    }


    private void handleDelete(Vlog vlog) {
        vlogService.delete(vlog.getId());
        loadVlogs();
    }
}
