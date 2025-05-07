package controllers.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Logement;
import service.LogementService;

import java.io.IOException;

public class TousLesLogementsController {

    @FXML private TableView<Logement> tableLogement;
    @FXML private TableColumn<Logement, Integer> colId;
    @FXML private TableColumn<Logement, String> colTitre;
    @FXML private TableColumn<Logement, String> colDescription;
    @FXML private TableColumn<Logement, String> colLocalisation;
    @FXML private TableColumn<Logement, Float> colPrix;
    @FXML private TableColumn<Logement, Void> colReserver;

    private final LogementService logementService = new LogementService();
    private final ObservableList<Logement> logements = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));

        // Ajouter les boutons "Réserver"
        colReserver.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("Réserver");

            {
                btn.setOnAction(event -> openReservationWindow());
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });

        logements.setAll(logementService.display());
        tableLogement.setItems(logements);
    }

    private void openReservationWindow() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/user/reservation.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Réservation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
