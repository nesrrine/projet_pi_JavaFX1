package controllers.User;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.awt.Desktop;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ReclamationDetailController {

    @FXML private ImageView imageView;
    @FXML private Button downloadButton;

    private String filePath;

    public void initializeDetail(String imagePath, String filePath) {
        this.filePath = filePath;
        try {
            FileInputStream fis = new FileInputStream(imagePath);
            imageView.setImage(new Image(fis));
        } catch (IOException e) {
            imageView.setImage(null);
        }
    }

    @FXML
    private void handleDownload() {
        if (filePath != null) {
            File file = new File(filePath);
            if (file.exists()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    new Alert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du fichier.").showAndWait();
                }
            } else {
                new Alert(Alert.AlertType.WARNING, "Fichier introuvable.").showAndWait();
            }
        }
    }
}
