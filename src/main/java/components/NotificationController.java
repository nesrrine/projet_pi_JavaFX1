package components;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import models.Notification;
import service.NotificationService;
import utils.Session;

import java.util.List;

public class NotificationController {

    @FXML
    private ListView<String> notificationList;

    @FXML
    private Button markAllAsReadButton;

    private final NotificationService notificationService = new NotificationService();
    //    private VBox createReactionBox(Vlog vlog, Runnable onInteractionChanged) {
//        // Bouton principal "👍 J'aime"
//        Button likeBtn = new Button("👍 J'aime");
//        likeBtn.setPrefWidth(120);
//        likeBtn.setPrefHeight(40);
//        likeBtn.setStyle("""
//        -fx-background-color: #4267B2;
//        -fx-text-fill: white;
//        -fx-font-weight: bold;
//        -fx-font-size: 16px;
//        -fx-background-radius: 30;
//    """);
//
//        // Résumé des réactions
//        Label reactionSummary = new Label();
//        reactionSummary.setStyle("-fx-font-size: 16px; -fx-text-fill: #333;");
//
//        // Barre des emojis (affichée au hover)
//        HBox reactionButtons = new HBox(10);
//        reactionButtons.setPadding(new Insets(5));
//        reactionButtons.setAlignment(Pos.CENTER_LEFT);
//        reactionButtons.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15px; -fx-border-color: #ddd; -fx-border-radius: 15px;");
//        reactionButtons.setVisible(true);  // Maintenant visible immédiatement pour le test
//
//        // Style des emojis
//        String emojiFontStyle = "-fx-font-size: 30px; -fx-background-color: transparent; -fx-border-width: 0;";
//
//        for (ReactionType type : ReactionType.values()) {
//            final ReactionType capturedType = type;
//            Button emojiButton = new Button(capturedType.getEmoji());
//            emojiButton.setStyle(emojiFontStyle);
//
//            // Action clic : enregistrer la réaction
//            emojiButton.setOnAction(e -> {
//                reactionService.reactToVlog(new Reaction(
//                        vlog.getId(),
//                        Session.getCurrentUser().getId(),
//                        capturedType
//                ));
//
//                updateReactionSummary(vlog, reactionSummary); // Mettre à jour le résumé des réactions
//                onInteractionChanged.run(); // pour rafraîchir les données du vlog
//            });
//
//            reactionButtons.getChildren().add(emojiButton);
//        }
//
//        // Affiche les emojis quand on survole le bouton "👍"
//        likeBtn.setOnAction(e -> {
//            // Quand on clique sur le bouton "👍", on affiche les emojis
//            reactionButtons.setVisible(true);
//        });
//
//        // Initialiser le résumé des réactions
//        updateReactionSummary(vlog, reactionSummary);
//
//        // Conteneur final
//        VBox container = new VBox(5);
//        container.setAlignment(Pos.CENTER_LEFT);
//        container.getChildren().addAll(
//                new HBox(10, likeBtn, reactionSummary),
//                reactionButtons
//        );
//        container.setPadding(new Insets(10));
//
//        return container;
//    }

//    private void updateReactionSummary(Vlog vlog, Label reactionSummary) {
//        // Récupérer toutes les réactions du vlog
//        Map<ReactionType, Integer> reactionCounts = new HashMap<>();
//
//        // Vérifier si les réactions sont présentes pour ce vlog
//        if (vlog.getReactions() == null || vlog.getReactions().isEmpty()) {
//            reactionSummary.setText("Aucune réaction");
//            return;
//        }
//
//        // Compter chaque type de réaction
//        for (Reaction reaction : vlog.getReactions()) {
//            reactionCounts.put(reaction.getType(),
//                    reactionCounts.getOrDefault(reaction.getType(), 0) + 1);
//        }
//
//        // Construire le résumé des réactions
//        StringBuilder summary = new StringBuilder();
//        for (ReactionType type : ReactionType.values()) {
//            int count = reactionCounts.getOrDefault(type, 0);
//            if (count > 0) {
//                summary.append(type.getEmoji())
//                        .append(" ")
//                        .append(count)
//                        .append(" ");
//            }
//        }
//
//        // Mettre à jour le label avec le résumé
//        if (summary.length() > 0) {
//            reactionSummary.setText(summary.toString().trim());
//        } else {
//            reactionSummary.setText("Aucune réaction");
//        }
//    }

    @FXML
    public void initialize() {
        loadNotifications();

        markAllAsReadButton.setOnAction(event -> {
            notificationService.markAllAsRead(Session.getCurrentUser().getId());
            loadNotifications();
            showAlert("Succès", "Toutes les notifications ont été marquées comme lues !");
        });
    }

    private void loadNotifications() {
        notificationList.getItems().clear();
        List<Notification> notifications = notificationService.getUnreadNotifications(Session.getCurrentUser().getId());
        for (Notification notif : notifications) {
            notificationList.getItems().add(notif.getMessage() + " - " + notif.getCreatedAt());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
