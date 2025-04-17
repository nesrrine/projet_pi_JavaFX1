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
import java.io.IOException;
import java.sql.SQLException;

public class TousLesTransportsController {

    @FXML private FlowPane transportContainer;

    private final TransportService transportService = new TransportService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        loadTransports();
    }

    private void loadTransports() {
        try {
            User currentUser = Session.getCurrentUser();
            transportContainer.getChildren().clear();

            for (Transport transport : transportService.getAllTransports()) {
                VBox card = createTransportCard(transport, currentUser);
                transportContainer.getChildren().add(card);
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des transports: " + e.getMessage());
        }
    }

    private VBox createTransportCard(Transport transport, User currentUser) {
        VBox card = new VBox(10); // 10 is spacing between elements
        card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 4);");
        card.setPrefWidth(300);

        // Type label
        Label typeLabel = new Label(transport.getType());
        typeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Owner info
        User owner = userService.getById(transport.getUserId());
        Label ownerLabel = new Label("Propriétaire: " + owner.getFirstName());
        ownerLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");

        // Description label
        Label descriptionLabel = new Label(transport.getDescription());
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-font-size: 14px;");

        // Prix label
        Label prixLabel = new Label(String.format("%.2f TND", transport.getPrix()));
        prixLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #007bff;");

        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(280);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(true);

        String imagePath = transport.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            }
        }

        // Réserver button
        Button reserverButton = new Button("Réserver");
        reserverButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
        reserverButton.setOnAction(e -> openReservationWindow(transport));

        // Edit/Delete buttons for owner
        if (currentUser != null && currentUser.getId() == transport.getUserId()) {
            Button editBtn = new Button("Modifier");
            editBtn.getStyleClass().add("btn-edit");
            editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            editBtn.setOnAction(e -> openEditTransport(transport));

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.getStyleClass().add("btn-delete");
            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            deleteBtn.setOnAction(e -> handleDelete(transport));

            HBox buttonsBox = new HBox(10);
            buttonsBox.setStyle("-fx-alignment: center;");
            buttonsBox.getChildren().addAll(reserverButton, editBtn, deleteBtn);
            card.getChildren().addAll(imageView, typeLabel, ownerLabel, descriptionLabel, prixLabel, buttonsBox);
        } else {
            card.getChildren().addAll(imageView, typeLabel, ownerLabel, descriptionLabel, prixLabel, reserverButton);
        }

        return card;
    }

    private void openReservationWindow(Transport transport) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/ReservationTransport.fxml"));
            Scene scene = new Scene(loader.load());
            
            Stage stage = new Stage();
            stage.setTitle("Réservation de Transport");
            stage.setScene(scene);
            
            ReservationTransportController controller = loader.getController();
            controller.setTransport(transport);
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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

            loadTransports(); // Refresh after editing
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
                loadTransports(); // Refresh the view
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
