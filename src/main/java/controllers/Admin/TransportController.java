package controllers.Admin;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import models.Transport;
import service.TransportService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class TransportController {
    @FXML private TextField nom;
    @FXML private TextField des;

    @FXML
    private TextField pri;
    @FXML private TextField web;
    @FXML private ImageView image_id;
    @FXML private ListView<Transport> listTerrain;
    @FXML private Button aj;
    @FXML private Button supptr;
    @FXML private Button moditr;
    @FXML private Button Import_btn;

    private TransportService transportService = new TransportService();
    private String imagePath;
    private ObservableList<Transport> transports = FXCollections.observableArrayList();

    @FXML
    private void handleListSelection() {
        Transport selected = listTerrain.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nom.setText(selected.getType());
            des.setText(selected.getDescription());
            pri.setText(String.valueOf(selected.getPrix()));
            if (selected.getImage() != null && !selected.getImage().isEmpty()) {
                File file = new File(selected.getImage());
                if (file.exists()) {
                    image_id.setImage(new Image(file.toURI().toString()));
                    imagePath = selected.getImage();
                }
            }
        }
    }

    @FXML
    public void initialize() {
        try {
            refreshList();
            // Ajouter le listener pour la sélection
            listTerrain.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> handleListSelection());
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des transports", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void ajt() {
        try {
            Transport transport = new Transport();
            transport.setType(nom.getText());
            transport.setDescription(des.getText());

            // Gestion du prix
            try {
                transport.setPrix(Double.parseDouble(pri.getText()));
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Prix invalide", "Veuillez entrer un nombre valide pour le prix", Alert.AlertType.ERROR);
                return;
            }

            transport.setImage(imagePath);
            transportService.addTransport(transport);
            refreshList();
            clearFields();
            showAlert("Succès", "Transport ajouté", "Le transport a été ajouté avec succès", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout", e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void supptr() {
        Transport selected = listTerrain.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                transportService.deleteTransport(selected.getId());
                refreshList();
                showAlert("Succès", "Transport supprimé", "Le transport a été supprimé avec succès", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression", e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Avertissement", "Aucune sélection", "Veuillez sélectionner un transport à supprimer", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void moditr() {
        Transport selected = listTerrain.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setType(nom.getText());
                selected.setDescription(des.getText());

                // Gestion du prix
                try {
                    selected.setPrix(Double.parseDouble(pri.getText()));
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Prix invalide", "Veuillez entrer un nombre valide pour le prix", Alert.AlertType.ERROR);
                    return;
                }

                if (imagePath != null) {
                    selected.setImage(imagePath);
                }

                transportService.updateTransport(selected);
                refreshList();
                showAlert("Succès", "Transport modifié", "Le transport a été modifié avec succès", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la modification", e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Avertissement", "Aucune sélection", "Veuillez sélectionner un transport à modifier", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void Import() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                // Créer un dossier "uploads" s'il n'existe pas
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) {
                    uploadsDir.mkdir();
                }

                // Copier le fichier dans le dossier uploads
                File destFile = new File(uploadsDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                imagePath = destFile.getAbsolutePath();
                image_id.setImage(new Image(destFile.toURI().toString()));

            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de l'import", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void refreshList() throws SQLException {
        transports.setAll(transportService.getAllTransports());
        listTerrain.setItems(transports);
        listTerrain.setCellFactory(param -> new ListCell<Transport>() {
            @Override
            protected void updateItem(Transport item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Créer un HBox pour afficher plusieurs informations
                    HBox hbox = new HBox(10);

                    // Afficher l'image si disponible
                    ImageView imageView = new ImageView();
                    if (item.getImage() != null && !item.getImage().isEmpty()) {
                        File file = new File(item.getImage());
                        if (file.exists()) {
                            Image image = new Image(file.toURI().toString());
                            imageView.setImage(image);
                            imageView.setFitHeight(40);
                            imageView.setFitWidth(40);
                            imageView.setPreserveRatio(true);
                        }
                    }

                    // Afficher les informations textuelles
                    VBox vbox = new VBox(5);
                    Label typeLabel = new Label("Type: " + item.getType());
                    Label descLabel = new Label("Description: " + item.getDescription());
                    Label prixLabel = new Label("Prix: " + item.getPrix() + " DT");

                    vbox.getChildren().addAll(typeLabel, descLabel, prixLabel);
                    hbox.getChildren().addAll(imageView, vbox);

                    setGraphic(hbox);
                }
            }
        });
    }

    private void clearFields() {
        nom.clear();
        des.clear();
        web.clear();
        image_id.setImage(null);
        imagePath = null;
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}