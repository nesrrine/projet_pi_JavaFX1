package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Vlog;
import service.VlogService;
import utils.CloudinaryUploader;
import utils.Session;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CreateVlogController {

    @FXML private TextArea contentField;
    @FXML private Label imageLabel;
    @FXML private Label videoLabel;
    @FXML private Button submitButton;

    private File selectedImage;
    private File selectedVideo;
    private final VlogService vlogService = new VlogService();
    private final CloudinaryUploader CloudinaryUploader = new CloudinaryUploader();


    private Vlog vlogToEdit = null;

    public void setVlogToEdit(Vlog vlog) {
        this.vlogToEdit = vlog;
        contentField.setText(vlog.getContent());
        imageLabel.setText(vlog.getImage());
        videoLabel.setText(vlog.getVideo());
    }

    @FXML
    private void handleChooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une image");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        selectedImage = chooser.showOpenDialog(getStage());
        if (selectedImage != null) {
            imageLabel.setText(selectedImage.getName());
        }
    }

    @FXML
    private void handleChooseVideo() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une vidéo");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Vidéos", "*.mp4", "*.avi", "*.mov"));
        selectedVideo = chooser.showOpenDialog(getStage());
        if (selectedVideo != null) {
            videoLabel.setText(selectedVideo.getName());
        }
    }

    @FXML
    private void handleSubmit() {
        String content = contentField.getText().trim();
        String imagePath = selectedImage != null ? selectedImage.getAbsolutePath()
                : (vlogToEdit != null ? vlogToEdit.getImage() : "");
        String videoPath = selectedVideo != null ? selectedVideo.getAbsolutePath()
                : (vlogToEdit != null ? vlogToEdit.getVideo() : "");

        // --- Validation ---
        if (content.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Le champ contenu est obligatoire.");
            return;
        }

        if ((vlogToEdit == null && selectedImage == null) || (imagePath == null || imagePath.trim().isEmpty())) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner une image.");
            return;
        }

        if ((vlogToEdit == null && selectedVideo == null) || (videoPath == null || videoPath.trim().isEmpty())) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner une vidéo.");
            return;
        }

        // --- Upload to Cloudinary ---
        String imageUrl = null;
        if (selectedImage != null) {
            try {
                imageUrl = CloudinaryUploader.uploadImage(selectedImage.getAbsolutePath());
                if (imageUrl == null) {
                    showAlert(Alert.AlertType.ERROR, "Échec de l'upload de l'image.");
                    return;
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'upload de l'image : " + e.getMessage());
                return;
            }
        }

        String videoUrl = null;
        if (selectedVideo != null) {
            try {
                videoUrl = CloudinaryUploader.uploadVideo(selectedVideo.getAbsolutePath());
                if (videoUrl == null) {
                    showAlert(Alert.AlertType.ERROR, "Échec de l'upload de la vidéo.");
                    return;
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'upload de la vidéo : " + e.getMessage());
                return;
            }
        }

        // --- Insert or Update ---
        if (vlogToEdit != null) {
            vlogToEdit.setContent(content);
            vlogToEdit.setImage(imageUrl != null ? imageUrl : vlogToEdit.getImage());
            vlogToEdit.setVideo(videoUrl != null ? videoUrl : vlogToEdit.getVideo());
            vlogService.update(vlogToEdit);
        } else {
            Vlog newVlog = new Vlog(
                    content,
                    imageUrl != null ? imageUrl : "",
                    videoUrl != null ? videoUrl : "",
                    LocalDateTime.now(),
                    Session.getCurrentUser().getId()
            );
            vlogService.add(newVlog);
        }

        showAlert(Alert.AlertType.INFORMATION, "Vlog enregistré avec succès !");
        loadPartial("/user/MesVlogs.fxml"); // ✅ Go back to vlog list
    }


    private void loadPartial(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            // Remplacer le contenu à l'intérieur de mainContent VBox dans UserInterfaceController
            VBox mainContent = (VBox) contentField.getScene().lookup("#mainContent");
            if (mainContent != null) {
                mainContent.getChildren().setAll(content);
            } else {
                showAlert(Alert.AlertType.ERROR, "Impossible de trouver 'mainContent' dans la scène.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de chargement du fichier FXML : " + e.getMessage());
        }
    }


    private Stage getStage() {
        return (Stage) contentField.getScene().getWindow();
    }

    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
