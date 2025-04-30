package controllers.User;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.Transport;
import service.TransportService;

import java.sql.SQLException;
import java.util.List;

public class TransportFrontController {
    @FXML
    private GridPane transportsGrid;

    private TransportService transportService = new TransportService();

    @FXML
    public void initialize() {
        try {
            loadTransports();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadTransports() throws SQLException {
        List<Transport> transports = transportService.getAllTransports();

        int column = 0;
        int row = 0;
        final int MAX_COLUMNS = 3; // 3 cartes par ligne

        for (Transport transport : transports) {
            VBox card = createTransportCard(transport);
            transportsGrid.add(card, column, row);

            column++;
            if (column >= MAX_COLUMNS) {
                column = 0;
                row++;
            }
        }
    }

    private VBox createTransportCard(Transport transport) {
        // Création de la carte
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 5, 0, 0);");

        // Image
        ImageView imageView = new ImageView();
        if (transport.getImage() != null && !transport.getImage().isEmpty()) {
            try {
                Image image = new Image(new java.io.File(transport.getImage()).toURI().toString());
                imageView.setImage(image);
                imageView.setFitWidth(200);
                imageView.setFitHeight(150);
                imageView.setPreserveRatio(true);
            } catch (Exception e) {
                // Image par défaut si problème de chargement
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-transport.png")));
            }
        }

        // Type
        Text typeText = new Text(transport.getType());
        typeText.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

        // Prix
        Text priceText = new Text(String.format("%.2f DT", transport.getPrix()));
        priceText.setStyle("-fx-font-size: 14px;");

        // Bouton Réserver
        Button reserveButton = new Button("Réserver");
        reserveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        reserveButton.setOnAction(e -> handleReservation(transport));

        // Ajout des éléments à la carte
        card.getChildren().addAll(imageView, typeText, priceText, reserveButton);

        return card;
    }

    private void handleReservation(Transport transport) {
        System.out.println("Réservation pour: " + transport.getType());
        // Ici vous pouvez implémenter la logique de réservation
        // Par exemple ouvrir une nouvelle fenêtre ou enregistrer en base
    }
}