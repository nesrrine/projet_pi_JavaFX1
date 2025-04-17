package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

import java.io.File;

public class TousLesVlogsController {


    @FXML private FlowPane vlogContainer;

    private final VlogService vlogService = new VlogService();
    private final UserService userService = new UserService();

    @FXML
    public void initialize() {
        User currentUser = Session.getCurrentUser();
        vlogContainer.getChildren().clear();

        for (Vlog vlog : vlogService.display()) {
            VBox card = new VBox(10);
            card.getStyleClass().add("vlog-card");
            card.setPrefWidth(300); // or 250 depending on available width
            card.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 1);");

            Label title = new Label("Vlog #" + vlog.getId());
            title.getStyleClass().add("vlog-title");

            Label author = new Label("Par: " + userService.getById(vlog.getAuthorId()).getFirstName());
            author.getStyleClass().add("vlog-author");

            Label content = new Label(vlog.getContent());
            content.getStyleClass().add("vlog-content");
            content.setWrapText(true);

            VBox inner = new VBox(title, author, content);
            inner.setSpacing(10);
            inner.setStyle("-fx-alignment: top-center;");

            // 📷 Display image if available
            if (vlog.getImage() != null && !vlog.getImage().isEmpty()) {
                File imageFile = new File(vlog.getImage());
                if (imageFile.exists()) {
                    ImageView imageView = new ImageView(new Image(imageFile.toURI().toString()));
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    inner.getChildren().add(imageView);
                }
            }

            // ▶ Add video view button if exists
            if (vlog.getVideo() != null && !vlog.getVideo().isEmpty()) {
                File videoFile = new File(vlog.getVideo());
                if (videoFile.exists()) {
                    Button showVideoBtn = new Button("Voir Vidéo");
                    showVideoBtn.getStyleClass().add("btn-edit");
                    showVideoBtn.setOnAction(e -> openVideoDialog(videoFile));
                    inner.getChildren().add(showVideoBtn);
                }
            }

            // ✏️ Only the author can edit/delete
            if (currentUser.getId() == vlog.getAuthorId()) {
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
                buttons.getStyleClass().add("vlog-buttons");
                buttons.setSpacing(10);
                inner.getChildren().add(buttons);
            }

            card.getChildren().add(inner);
            vlogContainer.getChildren().add(card);
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
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.sizeToScene();
            stage.showAndWait();

            initialize(); // Refresh after editing
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
            showAlert(Alert.AlertType.ERROR, "Impossible de lire la vidéo.");

        }
    }


    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
