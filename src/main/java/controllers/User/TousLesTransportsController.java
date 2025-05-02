package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Transport;
import models.User;
import service.TransportReservationService;
import service.TransportService;
import service.UserService;
import utils.Session;
import utils.PermissionManager;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class TousLesTransportsController {

    @FXML private FlowPane transportContainer;
    @FXML private ComboBox<String> typeFilterComboBox;
    @FXML private Button resetFilterButton;

    private final TransportService transportService = new TransportService();
    private final UserService userService = new UserService();
    private final TransportReservationService reservationService = new TransportReservationService();

    @FXML
    public void initialize() {
        // Create the reservation table if it doesn't exist
        try {
            reservationService.createTableIfNotExists();
        } catch (SQLException e) {
            System.err.println("Error creating reservation table: " + e.getMessage());
        }
        
        setupTypeFilter();
        loadTransports();
        
        // Set up reset filter button
        resetFilterButton.setOnAction(e -> {
            typeFilterComboBox.getSelectionModel().clearSelection();
            loadTransports();
        });
    }
    
    private void setupTypeFilter() {
        try {
            // Add "Tous les types" option
            typeFilterComboBox.getItems().add("Tous les types");
            
            // Add all available transport types from database
            typeFilterComboBox.getItems().addAll(transportService.getAllTransportTypes());
            
            // Select "Tous les types" by default
            typeFilterComboBox.getSelectionModel().selectFirst();
            
            // Add listener for selection changes
            typeFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    if (newVal.equals("Tous les types")) {
                        loadTransports();
                    } else {
                        loadTransportsByType(newVal);
                    }
                }
            });
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement des types de véhicules: " + e.getMessage());
        }
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
    
    private void loadTransportsByType(String type) {
        try {
            User currentUser = Session.getCurrentUser();
            transportContainer.getChildren().clear();
            
            List<Transport> filteredTransports = transportService.getTransportsByType(type);
            
            if (filteredTransports.isEmpty()) {
                Label noResultsLabel = new Label("Aucun véhicule de type '" + type + "' trouvé");
                noResultsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666;");
                transportContainer.getChildren().add(noResultsLabel);
            } else {
                for (Transport transport : filteredTransports) {
                    VBox card = createTransportCard(transport, currentUser);
                    transportContainer.getChildren().add(card);
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors du filtrage des transports: " + e.getMessage());
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

        // Check if transport is available today
        boolean isAvailableToday = true;
        try {
            isAvailableToday = !reservationService.isTransportReservedOnDate(transport.getId(), LocalDate.now());
        } catch (SQLException e) {
            System.err.println("Error checking transport availability: " + e.getMessage());
        }
        
        // Availability indicator
        HBox availabilityBox = new HBox(5);
        availabilityBox.setStyle("-fx-alignment: center-left;");
        
        Circle statusCircle = new Circle(6);
        statusCircle.setStyle(isAvailableToday ? 
            "-fx-fill: #2ecc71;" : // Green for available
            "-fx-fill: #e74c3c;"   // Red for unavailable
        );
        
        Label statusLabel = new Label(isAvailableToday ? 
            "Disponible aujourd'hui" : 
            "Réservé aujourd'hui"
        );
        statusLabel.setStyle(isAvailableToday ? 
            "-fx-text-fill: #2ecc71; -fx-font-weight: bold;" : 
            "-fx-text-fill: #e74c3c; -fx-font-weight: bold;"
        );
        
        availabilityBox.getChildren().addAll(statusCircle, statusLabel);

        // Create a container for buttons
        HBox buttonsBox = new HBox(10);
        buttonsBox.setStyle("-fx-alignment: center;");

        // Get user role
        String userRole = currentUser != null ? currentUser.getRole() : null;
        
        // Add Réserver button for all users except Transporteur
        if (!PermissionManager.ROLE_TRANSPORTEUR.equals(userRole)) {
            Button reserverButton = new Button("Réserver");
            reserverButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; " +
                                  "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5;");
            
            if (!isAvailableToday) {
                reserverButton.setDisable(true);
                reserverButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
                Tooltip tooltip = new Tooltip("Ce véhicule est réservé aujourd'hui");
                Tooltip.install(reserverButton, tooltip);
            }
            
            reserverButton.setOnAction(e -> openReservationWindow(transport));
            buttonsBox.getChildren().add(reserverButton);
        }

        // Add Edit/Delete buttons for transport owners and admins
        boolean canEdit = PermissionManager.canEditTransportItems() && 
                         (PermissionManager.isAdmin() || (currentUser != null && currentUser.getId() == transport.getUserId()));
        
        if (canEdit) {
            Button editBtn = new Button("Modifier");
            editBtn.getStyleClass().add("btn-edit");
            editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            editBtn.setOnAction(e -> openEditTransport(transport));

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.getStyleClass().add("btn-delete");
            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            deleteBtn.setOnAction(e -> handleDelete(transport));

            buttonsBox.getChildren().addAll(editBtn, deleteBtn);
        }

        // Add all elements to the card
        card.getChildren().addAll(imageView, typeLabel, ownerLabel, descriptionLabel, prixLabel, availabilityBox);
        
        // Only add buttons box if it has any buttons
        if (!buttonsBox.getChildren().isEmpty()) {
            card.getChildren().add(buttonsBox);
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
            stage.initModality(Modality.APPLICATION_MODAL);
            
            ReservationTransportController controller = loader.getController();
            controller.setTransport(transport);
            
            stage.showAndWait();
            
            // Refresh the transport list after reservation
            loadTransports();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire de réservation: " + e.getMessage());
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
