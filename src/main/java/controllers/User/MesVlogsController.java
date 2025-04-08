package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import models.User;
import models.Vlog;
import service.VlogService;
import utils.Session;

import java.io.File;
import java.util.List;

public class MesVlogsController {

    @FXML private FlowPane vlogContainer;


    private final VlogService vlogService = new VlogService();

    @FXML
    public void initialize() {
        User currentUser = Session.getCurrentUser();
        vlogContainer.getChildren().clear();

        List<Vlog> userVlogs = vlogService.getByAuthor(currentUser.getId());

        for (Vlog vlog : userVlogs) {
            VBox card = new VBox(10);
            card.getStyleClass().add("vlog-card");
            card.setPrefWidth(300); // or 250 depending on available width
            card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");
            card.setSpacing(12);
            Label title = new Label("Vlog #" + vlog.getId());
            title.getStyleClass().add("vlog-title");

            Label content = new Label(vlog.getContent());
            content.getStyleClass().add("vlog-content");
            content.setWrapText(true);

            VBox inner = new VBox(8, title, content);
            inner.setStyle("-fx-spacing: 8;");
            inner.setStyle("-fx-alignment: top-center;");

            // Image (if available)
            if (vlog.getImage() != null && !vlog.getImage().isEmpty()) {
                File imageFile = new File(vlog.getImage());
                if (imageFile.exists()) {
                    ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
                    imageView.setFitWidth(300);
                    imageView.setPreserveRatio(true);
                    imageView.setSmooth(true);
                    imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                    inner.getChildren().add(imageView);
                }
            }

            // Video (if available)
            if (vlog.getVideo() != null && !vlog.getVideo().isEmpty()) {
                File videoFile = new File(vlog.getVideo());
                if (videoFile.exists()) {
                    Button videoBtn = new Button("Voir Vidéo");
                    videoBtn.getStyleClass().add("btn-video");
                    videoBtn.setOnAction(e -> openVideoDialog(videoFile));
                    inner.getChildren().add(videoBtn);
                }
            }

            // Edit/Delete Buttons
            Button editBtn = new Button("Modifier");
            editBtn.getStyleClass().add("btn-edit");
            editBtn.setOnAction(e -> openEditVlog(vlog));

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.getStyleClass().add("btn-delete");
            deleteBtn.setOnAction(e -> {
                vlogService.delete(vlog.getId());
                initialize(); // Refresh
            });

            HBox buttons = new HBox(10, editBtn, deleteBtn);
            buttons.setStyle("-fx-alignment: center;");
            buttons.setStyle("-fx-alignment: CENTER_RIGHT; -fx-padding: 10 0 0 0;");
            buttons.getStyleClass().add("vlog-buttons");

            inner.getChildren().add(buttons);
            card.getChildren().add(inner);
            vlogContainer.getChildren().add(card);
        }

        if (userVlogs.isEmpty()) {
            Label empty = new Label("Vous n'avez pas encore publié de vlogs.");
            empty.setStyle("-fx-text-fill: #888; -fx-font-style: italic;");
            vlogContainer.getChildren().add(empty);
        }
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
            stage.sizeToScene();
            stage.showAndWait();

            initialize(); // Refresh
        } catch (Exception e) {
            e.printStackTrace();
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

        }
    }
}
