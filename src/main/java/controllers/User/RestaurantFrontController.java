package controllers.User;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Restaurant;
import service.RestaurantService;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

public class RestaurantFrontController {
    @FXML
    private GridPane restaurantsGrid;

    private RestaurantService restaurantService = new RestaurantService();

    @FXML
    public void initialize() {
        try {
            loadRestaurants();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadRestaurants() throws SQLException {
        List<Restaurant> restaurants = restaurantService.getAllRestaurants();

        int column = 0;
        int row = 0;
        final int MAX_COLUMNS = 3; // 3 cartes par ligne

        for (Restaurant restaurant : restaurants) {
            VBox card = createRestaurantCard(restaurant);
            restaurantsGrid.add(card, column, row);

            column++;
            if (column >= MAX_COLUMNS) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createRestaurantCard(Restaurant restaurant) {
        // Création de la carte
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 5, 0, 0);");
        card.setPrefWidth(250);

        // Image principale
        ImageView imageView = new ImageView();
        if (restaurant.getImage() != null && !restaurant.getImage().isEmpty()) {
            try {
                Image image = new Image(new File(restaurant.getImage()).toURI().toString());
                imageView.setImage(image);
                imageView.setFitWidth(220);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
            } catch (Exception e) {
                // Image par défaut si problème de chargement
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-restaurant.png")));
            }
        }

        // Nom
        Text nameText = new Text(restaurant.getNom());
        nameText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Localisation
        Text locationText = new Text(restaurant.getLocalisation());
        locationText.setStyle("-fx-font-size: 14px;");

        // Prix moyen
        Text priceText = new Text("Prix moyen: " + String.format("%.2f DT", restaurant.getPrix()));
        priceText.setStyle("-fx-font-size: 14px;");

        // Bouton Voir Détails
        Button detailsButton = new Button("reserver");
        detailsButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        detailsButton.setOnAction(e -> showRestaurantDetails(restaurant));

        // Ajout des éléments à la carte
        card.getChildren().addAll(imageView, nameText, locationText, priceText, detailsButton);

        return card;
    }

    private void showRestaurantDetails(Restaurant restaurant) {
        System.out.println("Détails pour: " + restaurant.getNom());
        // Ici vous pouvez implémenter l'affichage des détails complets
    }
}