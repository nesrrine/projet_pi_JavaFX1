package controllers.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
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

    @FXML private TextField searchFieldText;
    @FXML private ComboBox<Float> comboPrix;

    private final LogementService logementService = new LogementService();
    private final ObservableList<Logement> logements = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Initialiser les colonnes
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colLocalisation.setCellValueFactory(new PropertyValueFactory<>("localisation"));
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));

        // Ajouter bouton Réserver
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

        // Extraire les prix distincts pour la ComboBox
        ObservableList<Float> prixList = FXCollections.observableArrayList();
        logements.stream()
                .map(Logement::getPrix)
                .distinct()
                .sorted()
                .forEach(prixList::add);
        comboPrix.setItems(prixList);

        // Mise en place du filtrage
        FilteredList<Logement> filteredData = new FilteredList<>(logements, p -> true);

        searchFieldText.textProperty().addListener((obs, oldVal, newVal) -> filterList(filteredData));
        comboPrix.valueProperty().addListener((obs, oldVal, newVal) -> filterList(filteredData));

        tableLogement.setItems(filteredData);
    }

    private void filterList(FilteredList<Logement> filteredData) {
        String textFilter = searchFieldText.getText().toLowerCase();
        Float selectedPrix = comboPrix.getValue();

        filteredData.setPredicate(logement -> {
            boolean matchTexte = textFilter.isEmpty()
                    || logement.getTitre().toLowerCase().contains(textFilter)
                    || logement.getLocalisation().toLowerCase().contains(textFilter);

            boolean matchPrix = (selectedPrix == null || logement.getPrix() == selectedPrix);

            return matchTexte && matchPrix;
        });
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

    @FXML
    private void handleVoirGains() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/User/agenda.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Agenda");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
