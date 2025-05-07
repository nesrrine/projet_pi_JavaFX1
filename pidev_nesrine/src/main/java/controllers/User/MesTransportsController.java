package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Transport;
import models.User;
import service.TransportService;
import service.UserService;
import utils.Session;

import java.io.File;
import java.sql.SQLException;

public class MesTransportsController {

    @FXML private FlowPane transportContainer;

    private final TransportService transportService = new TransportService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        try {
            User currentUser = Session.getCurrentUser();
            if (currentUser == null) {
                showAlert(Alert.AlertType.ERROR, "Vous devez être connecté pour voir vos transports.");
                return;
            }

            transportContainer.getChildren().clear();

            for (Transport transport : transportService.getTransportsByUserId(currentUser.getId())) {
                VBox card = new VBox(10);
                card.getStyleClass().add("transport-card");
                card.setPrefWidth(300);
                card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

                // Title with type
                Label title = new Label(transport.getType());
                title.setStyle("-fx-font-size: 18; -fx-font-weight: bold;");

                // Description
                Label description = new Label(transport.getDescription());
                description.setStyle("-fx-font-size: 14;");
                description.setWrapText(true);

                // Price
                Label price = new Label(String.format("Prix: %.2f TND", transport.getPrix()));
                price.setStyle("-fx-font-size: 16; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                VBox content = new VBox(10, title, description, price);

                // Image if available
                if (transport.getImage() != null && !transport.getImage().isEmpty()) {
                    File imageFile = new File(transport.getImage());
                    if (imageFile.exists()) {
                        ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
                        imageView.setFitWidth(270);
                        imageView.setPreserveRatio(true);
                        content.getChildren().add(1, imageView); // Add after title
                    }
                }

                // Edit/Delete buttons
                Button editBtn = new Button("Modifier");
                editBtn.getStyleClass().add("btn-edit");
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                editBtn.setOnAction(e -> openEditTransport(transport));

                Button deleteBtn = new Button("Supprimer");
                deleteBtn.getStyleClass().add("btn-delete");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteBtn.setOnAction(e -> handleDelete(transport));

                HBox buttons = new HBox(10, editBtn, deleteBtn);
                buttons.setStyle("-fx-alignment: center;");
                content.getChildren().add(buttons);

                card.getChildren().add(content);
                transportContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des transports: " + e.getMessage());
        }
    }

    private void openEditTransport(Transport transport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/CreateTransport.fxml"));
            Parent root = loader.load();

            CreateTransportController controller = loader.getController();
            controller.setTransportToEdit(transport);

            Stage stage = new Stage();
            stage.setTitle("Modifier Transport");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            initialize(); // Refresh after editing
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire de modification: " + e.getMessage());
        }
    }

    private void handleDelete(Transport transport) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer ce transport ?");

        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                transportService.deleteTransport(transport.getId());
                initialize(); // Refresh the view
                showAlert(Alert.AlertType.INFORMATION, "Transport supprimé avec succès!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
