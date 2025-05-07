// ReclamationsRecuesController.java
package controllers.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import models.Reclamation;
import service.ReclamationService;
import service.UserService;
import utils.Session;

public class ReclamationsRecuesController {

    @FXML private TableView<Reclamation> reclamationTable;
    @FXML private TableColumn<Reclamation, String> titreCol;
    @FXML private TableColumn<Reclamation, String> descriptionCol;
    @FXML private TableColumn<Reclamation, String> categorieCol;
    @FXML private TableColumn<Reclamation, String> statutCol;
    @FXML private TableColumn<Reclamation, String> envoyeeParCol;
    @FXML private TableColumn<Reclamation, Void> actionCol;

    private final ReclamationService reclamationService = new ReclamationService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        statutCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setGraphic(null);
                } else {
                    Label chip = new Label(statut);
                    chip.setStyle("-fx-background-color: " +
                            (statut.equalsIgnoreCase("En cours") ? "#FFA000;" :
                                    statut.equalsIgnoreCase("Traitée") ? "#4CAF50;" :
                                            "#E53935;") +
                            " -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 10;");
                    setGraphic(chip);
                }
            }
        });

        envoyeeParCol.setCellValueFactory(cellData -> {
            int auteurId = cellData.getValue().getAuteurId();
            String emailAuteur = userService.getById(auteurId).getEmail();
            return new javafx.beans.property.SimpleStringProperty(emailAuteur);
        });

        actionCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Reclamation, Void> call(final TableColumn<Reclamation, Void> param) {
                return new TableCell<>() {
                    private final ComboBox<String> statusCombo = new ComboBox<>(FXCollections.observableArrayList("En cours", "Traitée"));

                    {
                        statusCombo.setOnAction(e -> {
                            Reclamation r = getTableView().getItems().get(getIndex());
                            String newStatut = statusCombo.getValue();
                            r.setStatut(newStatut);
                            reclamationService.updateStatut(r.getId(), newStatut);
                            loadTableData();
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            Reclamation r = getTableView().getItems().get(getIndex());
                            statusCombo.setValue(r.getStatut());
                            setGraphic(new VBox(statusCombo));
                        }
                    }
                };
            }
        });

        loadTableData();
    }

    private void loadTableData() {
        ObservableList<Reclamation> list = FXCollections.observableArrayList(
                reclamationService.getByCible(Session.getCurrentUser().getId())
        );
        reclamationTable.setItems(list);
    }
}
