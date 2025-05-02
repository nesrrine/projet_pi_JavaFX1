package controllers.Admin;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import models.Vlog;
import service.UserService;
import service.VlogService;

import java.util.List;

public class GestionVlogsController {

    @FXML private VBox vlogListContainer;

    private final VlogService vlogService = new VlogService();
    private final UserService userService = new UserService();

    @FXML
    private void initialize() {
        // Load vlogs
        loadVlogs();
    }

    private void loadVlogs() {
        // Clear existing items
        vlogListContainer.getChildren().clear();
        
        // Get all vlogs
        List<Vlog> vlogs = vlogService.display();
        
        if (vlogs.isEmpty()) {
            Label emptyLabel = new Label("Aucun vlog trouvé");
            emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
            emptyLabel.setPadding(new Insets(20));
            vlogListContainer.getChildren().add(emptyLabel);
            return;
        }
        
        // Create a card for each vlog
        for (Vlog vlog : vlogs) {
            vlogListContainer.getChildren().add(createVlogCard(vlog));
        }
    }
    
    private VBox createVlogCard(Vlog vlog) {
        // Main card container
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-border-color: #e0e0e0; -fx-border-radius: 5px; -fx-padding: 15px;");
        card.setPrefWidth(Region.USE_COMPUTED_SIZE);
        
        // Author and date
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        // Author
        Label authorLabel = new Label(userService.getById(vlog.getAuthorId()).getFirstName());
        authorLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        authorLabel.setStyle("-fx-text-fill: #3498db;");
        
        // Date
        Label dateLabel = new Label(vlog.getCreatedAt().toLocalDate().toString());
        dateLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        headerBox.getChildren().addAll(authorLabel, new Label(" • "), dateLabel);
        
        // Content
        Text contentText = new Text(vlog.getContent());
        contentText.setWrappingWidth(600);
        
        // Action buttons
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button deleteButton = new Button("Supprimer");
        deleteButton.getStyleClass().add("button-delete");
        deleteButton.setOnAction(e -> handleDelete(vlog));
        
        actionsBox.getChildren().add(deleteButton);
        
        // Add all elements to card
        card.getChildren().addAll(headerBox, contentText, actionsBox);
        
        return card;
    }

    private void handleDelete(Vlog vlog) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le vlog");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce vlog ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                vlogService.delete(vlog.getId());
                loadVlogs();
            }
        });
    }
    

}
