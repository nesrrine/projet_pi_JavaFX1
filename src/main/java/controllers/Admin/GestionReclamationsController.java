package controllers.Admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Reclamation;
import models.User;
import service.ReclamationService;
import service.UserService;
import utils.Session;
import utils.PermissionManager;

import java.util.List;

public class GestionReclamationsController {

    @FXML private VBox reclamationListContainer;

    private final ReclamationService reclamationService = new ReclamationService();
    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        // Load reclamations
        loadReclamations();
    }

    private void loadReclamations() {
        // Clear existing items
        reclamationListContainer.getChildren().clear();
        
        // Get all reclamations
        List<Reclamation> reclamations = reclamationService.display();
        
        if (reclamations.isEmpty()) {
            Label emptyLabel = new Label("Aucune réclamation trouvée");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
            emptyLabel.setPadding(new Insets(20));
            reclamationListContainer.getChildren().add(emptyLabel);
            return;
        }
        
        // Create a card for each reclamation
        for (Reclamation reclamation : reclamations) {
            reclamationListContainer.getChildren().add(createReclamationCard(reclamation));
        }
    }
    
    private VBox createReclamationCard(Reclamation reclamation) {
        // Main card container
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5px; -fx-padding: 15px;");
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        
        // Reclamation title
        Label titleLabel = new Label(reclamation.getTitre());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #3498db;");
        
        // Status badge
        Label statusLabel = new Label(reclamation.getStatut());
        statusLabel.setPadding(new Insets(3, 8, 3, 8));
        statusLabel.setStyle("-fx-background-radius: 3px; -fx-text-fill: white;");
        
        // Set status color based on status value
        switch (reclamation.getStatut().toLowerCase()) {
            case "en attente":
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #f39c12;");
                break;
            case "traitée":
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #2ecc71;");
                break;
            case "rejetée":
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #e74c3c;");
                break;
            default:
                statusLabel.setStyle(statusLabel.getStyle() + "-fx-background-color: #95a5a6;");
        }
        
        // Reclamation details
        HBox detailsBox = new HBox(20);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        
        // Author
        VBox authorBox = new VBox(5);
        Text authorTitle = new Text("Auteur");
        authorTitle.setStyle("-fx-font-weight: bold; -fx-fill: #555;");
        Text authorValue = new Text(userService.getById(reclamation.getAuteurId()).getFirstName());
        authorBox.getChildren().addAll(authorTitle, authorValue);
        
        // Target
        VBox targetBox = new VBox(5);
        Text targetTitle = new Text("Cible");
        targetTitle.setStyle("-fx-font-weight: bold; -fx-fill: #555;");
        Text targetValue = new Text(userService.getById(reclamation.getCibleId()).getFirstName());
        targetBox.getChildren().addAll(targetTitle, targetValue);
        
        // Add status to details
        VBox statusBox = new VBox(5);
        Text statusTitle = new Text("Statut");
        statusTitle.setStyle("-fx-font-weight: bold; -fx-fill: #555;");
        statusBox.getChildren().addAll(statusTitle, statusLabel);
        
        detailsBox.getChildren().addAll(authorBox, targetBox, statusBox);
        
        // Get current user
        User currentUser = Session.getCurrentUser();
        
        // Action buttons - only show if user has permission
        boolean canManageReclamations = PermissionManager.canManageReclamations();
        boolean isAuthor = currentUser != null && currentUser.getId() == reclamation.getAuteurId();
        boolean isAdmin = PermissionManager.isAdmin();
        
        // Only add action buttons if user has permission
        if (canManageReclamations || isAuthor || isAdmin) {
            HBox actionsBox = new HBox(10);
            actionsBox.setAlignment(Pos.CENTER_RIGHT);
            
            // Edit button - Admin can edit any reclamation, author can only edit their own
            if (isAdmin || (canManageReclamations && isAuthor)) {
                Button editButton = new Button("Modifier");
                editButton.getStyleClass().add("button-edit");
                editButton.setOnAction(e -> handleEdit(reclamation));
                actionsBox.getChildren().add(editButton);
            }
            
            // Delete button - Admin can delete any reclamation, author can only delete their own
            if (isAdmin || (canManageReclamations && isAuthor)) {
                Button deleteButton = new Button("Supprimer");
                deleteButton.getStyleClass().add("button-delete");
                deleteButton.setOnAction(e -> handleDelete(reclamation));
                actionsBox.getChildren().add(deleteButton);
            }
            
            // Only add the actions box if it has any buttons
            if (!actionsBox.getChildren().isEmpty()) {
                card.getChildren().addAll(titleLabel, detailsBox, actionsBox);
            } else {
                card.getChildren().addAll(titleLabel, detailsBox);
            }
        } else {
            // If user doesn't have permission, just show the details without action buttons
            card.getChildren().addAll(titleLabel, detailsBox);
        }
        
        return card;
    }

    private void handleEdit(Reclamation reclamation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/EditReclamation.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            EditReclamationController controller = loader.getController();
            controller.setReclamation(reclamation);
            stage.setTitle("Modifier Statut");
            stage.setResizable(false);
            stage.showAndWait();
            loadReclamations();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleDelete(Reclamation reclamation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la réclamation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                reclamationService.delete(reclamation.getId());
                loadReclamations();
            }
        });
    }
    

}
