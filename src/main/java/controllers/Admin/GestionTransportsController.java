package controllers.Admin;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import models.Transport;
import service.TransportService;

import java.sql.SQLException;
import java.util.List;

public class GestionTransportsController {

    @FXML private VBox transportListContainer;

    private final TransportService transportService = new TransportService();

    @FXML
    private void initialize() {
        // Load transports
        loadTransports();
    }

    private void loadTransports() {
        try {
            // Clear existing items
            transportListContainer.getChildren().clear();
            
            // Get all transports
            List<Transport> transports = transportService.getAllTransports();
            
            if (transports.isEmpty()) {
                Label emptyLabel = new Label("Aucun transport trouvé");
                emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
                emptyLabel.setPadding(new Insets(20));
                transportListContainer.getChildren().add(emptyLabel);
                return;
            }
            
            // Create a card for each transport
            for (Transport transport : transports) {
                transportListContainer.getChildren().add(createTransportCard(transport));
            }
        } catch (SQLException e) {
            showError("Erreur lors du chargement des transports", e.getMessage());
        }
    }
    
    private VBox createTransportCard(Transport transport) {
        // Main card container
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5px; -fx-padding: 15px;");
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        
        // Transport type
        Label typeLabel = new Label(transport.getType());
        typeLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        typeLabel.setStyle("-fx-text-fill: #3498db;");
        
        // Transport details
        HBox detailsBox = new HBox(20);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        
        // Price
        VBox priceBox = new VBox(5);
        Text priceTitle = new Text("Prix");
        priceTitle.setStyle("-fx-font-weight: bold; -fx-fill: #555;");
        Text priceValue = new Text(String.format("%.2f DT", transport.getPrix()));
        priceBox.getChildren().addAll(priceTitle, priceValue);
        
        detailsBox.getChildren().add(priceBox);
        
        // Description
        VBox descriptionBox = new VBox(5);
        Text descriptionTitle = new Text("Description");
        descriptionTitle.setStyle("-fx-font-weight: bold; -fx-fill: #555;");
        Text descriptionValue = new Text(transport.getDescription());
        descriptionValue.setWrappingWidth(600);
        descriptionBox.getChildren().addAll(descriptionTitle, descriptionValue);
        
        // Action buttons
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button editButton = new Button("Modifier");
        editButton.getStyleClass().add("button-edit");
        editButton.setOnAction(e -> handleEdit(transport));
        
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("button-delete");
        deleteButton.setOnAction(e -> handleDelete(transport));
        
        actionsBox.getChildren().addAll(editButton, deleteButton);
        
        // Add all elements to card
        card.getChildren().addAll(typeLabel, detailsBox, descriptionBox, actionsBox);
        
        return card;
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
