package controllers.User;

import components.VlogInteractionBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.User;
import models.Vlog;
import service.UserService;
import service.VlogService;
import utils.Session;
import utils.VimeoAPI;

import java.io.File;

public class TousLesVlogsController {

    @FXML
    private FlowPane vlogContainer;

    private final VlogService vlogService = new VlogService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        vlogContainer.getChildren().clear();
        vlogContainer.setHgap(20);
        vlogContainer.setVgap(20);
        vlogContainer.setPadding(new Insets(20));
        vlogContainer.setStyle("-fx-background-color: #f0f2f5;");

        User currentUser = Session.getCurrentUser();

        for (Vlog vlog : vlogService.display()) {
            VBox card = createVlogCard(vlog, currentUser);
            vlogContainer.getChildren().add(card);
        }
    }

    private VBox createVlogCard(Vlog vlog, User currentUser) {
        VBox card = new VBox(15);
        card.setPrefWidth(350);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        Label title = new Label("Vlog #" + vlog.getId());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label author = new Label("Par: " + userService.getById(vlog.getAuthorId()).getFirstName());
        author.setStyle("-fx-text-fill: #555;");

        Label content = new Label(vlog.getContent());
        content.setWrapText(true);
        content.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

        VBox inner = new VBox(10, title, author, content);
        inner.setAlignment(Pos.TOP_CENTER);
        // Image (if available)

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


        // Bundle Interactions (likes, dislikes, commentaires, etc.)
        VlogInteractionBundle bundle = new VlogInteractionBundle();
        VBox interactionBundle = bundle.create(vlog, this::initialize);
        inner.getChildren().add(interactionBundle);

        // Si l'utilisateur est l'auteur : afficher boutons Modifier/Supprimer
        if (currentUser.getId() == vlog.getAuthorId()) {
            Button editBtn = new Button("✏️ Modifier");
            Button deleteBtn = new Button("🗑️ Supprimer");

            editBtn.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white; -fx-background-radius: 8;");
            deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 8;");

            editBtn.setOnAction(e -> openEditVlog(vlog));
            deleteBtn.setOnAction(e -> {
                vlogService.delete(vlog.getId());
                initialize(); // rafraîchir la vue
            });

            HBox buttons = new HBox(10, editBtn, deleteBtn);
            buttons.setAlignment(Pos.CENTER);
            inner.getChildren().add(buttons);
        }

        card.getChildren().add(inner);
        return card;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void openEditVlog(Vlog vlog) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/CreateVlog.fxml"));
            Parent root = loader.load();
            CreateVlogController controller = loader.getController();
            controller.setVlogToEdit(vlog);

            Stage stage = new Stage();
            stage.setTitle("Modifier Vlog");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.sizeToScene();
            stage.showAndWait();
            initialize();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'ouverture du formulaire d'édition.");
        }
    }
    private void openVideoDialog(File videoFile) {
        try {
            Media media = new Media(videoFile.toURI().toString());
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


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}