package controllers.User;

import javafx.animation.FadeTransition;
import javafx.stage.Popup;
import javafx.util.Duration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Notification;
import models.User;
import models.Vlog;
import service.NotificationService;
import service.VlogService;
import utils.Session;

import java.io.File;
import java.util.List;

public class MesVlogsController {

    @FXML private FlowPane vlogContainer;
    @FXML private Button notificationButton;
    @FXML private ListView<String> notificationList;
    @FXML private Button markAllAsReadButton;
    @FXML private Label notificationBadge;

    private final NotificationService notificationService = new NotificationService();
    private final VlogService vlogService = new VlogService();

    @FXML
    public void initialize() {
        notificationButton.setOnAction(event -> showNotificationsPopup());
        updateNotificationBadge(); // Important: affiche le badge si besoin

        User currentUser = Session.getCurrentUser();
        vlogContainer.getChildren().clear();

        List<Vlog> userVlogs = vlogService.getByAuthor(currentUser.getId());

        if (userVlogs.isEmpty()) {
            Label empty = new Label("Vous n'avez pas encore publié de vlogs.");
            empty.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
            vlogContainer.getChildren().add(empty);
        } else {
            for (Vlog vlog : userVlogs) {
                VBox card = createVlogCard(vlog);
                vlogContainer.getChildren().add(card);
            }
        }
    }

    private VBox createVlogCard(Vlog vlog) {
        VBox card = new VBox(10);
        card.getStyleClass().add("vlog-card");
        card.setPrefWidth(300);
        card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

        Label title = new Label("Vlog #" + vlog.getId());
        title.getStyleClass().add("vlog-title");

        Label content = new Label(vlog.getContent());
        content.getStyleClass().add("vlog-content");
        content.setWrapText(true);

        VBox inner = new VBox(8, title, content);
        inner.setStyle("-fx-alignment: top-center;");

        // Image (from Cloudinary URL)
        if (vlog.getImage() != null && !vlog.getImage().isEmpty()) {
            ImageView imageView = new ImageView(new Image(vlog.getImage()));
            imageView.setFitWidth(300);
            imageView.setPreserveRatio(true);
            imageView.setSmooth(true);
            imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
            inner.getChildren().add(imageView);
        }

        // Video Button (if video exists)
        if (vlog.getVideo() != null && !vlog.getVideo().isEmpty()) {
            Button videoBtn = new Button("Avoir Vidéo");
            videoBtn.getStyleClass().add("btn-video");
            videoBtn.setOnAction(e -> openVideoDialog(new File(vlog.getVideo())));
            inner.getChildren().add(videoBtn);
        }

        // Edit/Delete buttons
        Button editBtn = new Button("Modifier");
        editBtn.getStyleClass().add("btn-edit");
        editBtn.setOnAction(e -> openEditVlog(vlog));

        Button deleteBtn = new Button("Supprimer");
        deleteBtn.getStyleClass().add("btn-delete");
        deleteBtn.setOnAction(e -> {
            vlogService.delete(vlog.getId());
            initialize(); // Refresh view
        });

        HBox buttons = new HBox(10, editBtn, deleteBtn);
        buttons.setStyle("-fx-alignment: center-right; -fx-padding: 10 0 0 0;");
        buttons.getStyleClass().add("vlog-buttons");

        inner.getChildren().add(buttons);
        card.getChildren().add(inner);

        return card;
    }

    private void openEditVlog(Vlog vlog) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/CreateVlog.fxml"));
            Parent root = loader.load();

            CreateVlogController controller = loader.getController();
            controller.setVlogToEdit(vlog);

            Stage stage = new Stage();
            stage.setTitle("Modifier Vlog");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            initialize(); // Refresh after closing
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification.");
        }
    }

    private void openVideoDialog(File videoFile) {
        try {

            Media media = new Media(videoFile.toURI().toString());
            System.out.println(videoFile.toURI().toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(640);
            mediaView.setFitHeight(360);
            mediaView.setPreserveRatio(true);

            VBox root = new VBox(mediaView);
            root.setStyle("-fx-padding: 20; -fx-background-color: white;");
            Scene scene = new Scene(root, 700, 400);

            Stage dialog = new Stage();
            dialog.setTitle("Vidéo du Vlog");
            dialog.setScene(scene);
            dialog.initModality(Modality.APPLICATION_MODAL);

            dialog.setOnCloseRequest(e -> mediaPlayer.stop());
            dialog.show();
            mediaPlayer.play();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la vidéo.");
        }
    }

    private void showNotificationsPopup() {
        List<Notification> notifications = notificationService.getUnreadNotifications(Session.getCurrentUser().getId());

        VBox popupContent = new VBox(10);
        popupContent.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;");
        popupContent.setPrefWidth(300);

        if (notifications.isEmpty()) {
            Label empty = new Label("Aucune nouvelle notification.");
            empty.setStyle("-fx-text-fill: gray; -fx-font-style: italic;");
            popupContent.getChildren().add(empty);
        } else {
            for (Notification notif : notifications) {
                Label label = new Label(notif.getMessage());
                label.setWrapText(true);
                popupContent.getChildren().add(label);
            }
        }

        Button markAllAsRead = new Button("Tout marquer comme lu");
        markAllAsRead.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        markAllAsRead.setOnAction(e -> {
            notificationService.markAllAsRead(Session.getCurrentUser().getId());
            showAlert("Succès", "Toutes les notifications ont été marquées comme lues !");
            updateNotificationBadge();
        });

        popupContent.getChildren().add(markAllAsRead);

        Popup popup = new Popup();
        popup.getContent().add(popupContent);
        popup.setAutoHide(true);

        // --- Animation d'apparition en fondu ---
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), popupContent);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();

        popup.show(notificationButton,
                notificationButton.getScene().getWindow().getX() + notificationButton.localToScene(0, 0).getX() + notificationButton.getScene().getX(),
                notificationButton.getScene().getWindow().getY() + notificationButton.localToScene(0, 0).getY() + notificationButton.getScene().getY() + 40
        );
    }

    private void updateNotificationBadge() {
        List<Notification> notifications = notificationService.getUnreadNotifications(Session.getCurrentUser().getId());
        int unreadCount = notifications.size();

        if (unreadCount > 0) {
            notificationBadge.setText(String.valueOf(unreadCount));
            notificationBadge.setVisible(true);
        } else {
            notificationBadge.setVisible(false);
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
