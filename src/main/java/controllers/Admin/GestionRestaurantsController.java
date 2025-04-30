package controllers.Admin;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import models.Restaurant;
import service.RestaurantService;

import java.sql.SQLException;
import java.util.List;

public class GestionRestaurantsController {

    @FXML private TableView<Restaurant> restaurantTable;
    @FXML private TableColumn<Restaurant, String> nomColumn;
    @FXML private TableColumn<Restaurant, String> localisationColumn;
    @FXML private TableColumn<Restaurant, String> descriptionColumn;
    @FXML private TableColumn<Restaurant, String> prixColumn;
    @FXML private TableColumn<Restaurant, Void> actionsColumn;

    private final RestaurantService restaurantService = new RestaurantService();

    @FXML
    private void initialize() {
        nomColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNom()));
        localisationColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getLocalisation()));
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

        loadRestaurants();
    }

    private void loadRestaurants() {
        try {
            List<Restaurant> restaurants = restaurantService.getAllRestaurants();
            restaurantTable.getItems().setAll(restaurants);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des restaurants", e.getMessage());
        }
    }

    private void handleDelete(Restaurant restaurant) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le restaurant");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce restaurant ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    restaurantService.deleteRestaurant(restaurant.getId());
                    loadRestaurants();
                } catch (SQLException e) {
                    showError("Erreur lors de la suppression", e.getMessage());
                }
            }
        });
    }

    private void handleEdit(Restaurant restaurant) {
        // TODO: Implement edit functionality
        System.out.println("Edit restaurant: " + restaurant.getId());
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
