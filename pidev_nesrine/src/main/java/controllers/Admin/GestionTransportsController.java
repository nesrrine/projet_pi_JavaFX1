package controllers.Admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import models.Transport;
import service.TransportService;

import java.sql.SQLException;
import java.util.List;

public class GestionTransportsController {

    @FXML private TableView<Transport> transportTable;
    @FXML private TableColumn<Transport, String> typeColumn;
    @FXML private TableColumn<Transport, String> destinationColumn;
    @FXML private TableColumn<Transport, String> prixColumn;
    @FXML private TableColumn<Transport, String> descriptionColumn;
    @FXML private TableColumn<Transport, Void> actionsColumn;

    private final TransportService transportService = new TransportService();

    @FXML
    private void initialize() {
        typeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getType()));
        descriptionColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getDescription()));
        prixColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(String.format("%.2f DT", data.getValue().getPrix())));

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final Button editBtn = new Button("Modifier");

            {
                deleteBtn.getStyleClass().add("button-delete");
                editBtn.getStyleClass().add("button-edit");

                deleteBtn.setOnAction(e -> handleDelete(getTableView().getItems().get(getIndex())));
                editBtn.setOnAction(e -> handleEdit(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox actions = new HBox(10, editBtn, deleteBtn);
                    setGraphic(actions);
                }
            }
        });

        loadTransports();
    }

    private void loadTransports() {
        try {
            List<Transport> transports = transportService.getAllTransports();
            transportTable.getItems().setAll(transports);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des transports", e.getMessage());
        }
    }

    private void handleDelete(Transport transport) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le transport");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce transport ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    transportService.deleteTransport(transport.getId());
                    loadTransports();
                } catch (SQLException e) {
                    showError("Erreur lors de la suppression", e.getMessage());
                }
            }
        });
    }

    private void handleEdit(Transport transport) {
        // TODO: Implement edit functionality
        System.out.println("Edit transport: " + transport.getId());
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
