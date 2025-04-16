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
import models.Restaurant;
import service.RestaurantService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;

public class RestaurantController {
    @FXML private TextField nom;
    @FXML private TextField localisation;
    @FXML private TextField description;
    @FXML private TextField prix;
    @FXML private TextField lat;

    @FXML private TextField log;
    @FXML private ImageView image_id;
    @FXML private ImageView image1_id;
    @FXML private ImageView image2_id;
    @FXML private ListView<Restaurant> listRestaurant;
    @FXML private Button aj;
    @FXML private Button supptr;
    @FXML private Button moditr;
    @FXML private Button Import_btn;
    @FXML private Button Import_btn1;
    @FXML private Button Import_btn2;

    private RestaurantService restaurantService = new RestaurantService();
    private String imagePath;
    private String image1Path;
    private String image2Path;
    private ObservableList<Restaurant> restaurants = FXCollections.observableArrayList();

    @FXML
    private void handleListSelection() {
        Restaurant selected = listRestaurant.getSelectionModel().getSelectedItem();
        if (selected != null) {
            nom.setText(selected.getNom());
            localisation.setText(selected.getLocalisation());
            description.setText(selected.getDescription());
            prix.setText(String.valueOf(selected.getPrix()));
            lat.setText(String.valueOf(selected.getLat()));
            log.setText(String.valueOf(selected.getLng()));

            loadImageIfExists(selected.getImage(), image_id);
            loadImageIfExists(selected.getImage1(), image1_id);
            loadImageIfExists(selected.getImage2(), image2_id);

            imagePath = selected.getImage();
            image1Path = selected.getImage1();
            image2Path = selected.getImage2();
        }
    }

    private void loadImageIfExists(String imagePath, ImageView imageView) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                imageView.setImage(new Image(file.toURI().toString()));
            }
        }
    }

    @FXML
    public void initialize() {
        try {
            refreshList();
            listRestaurant.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> handleListSelection());
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void ajt() {
        try {
            Restaurant restaurant = new Restaurant();
            restaurant.setNom(nom.getText());
            restaurant.setLocalisation(localisation.getText());
            restaurant.setDescription(description.getText());

            try {
                restaurant.setPrix(Double.parseDouble(prix.getText()));
                restaurant.setLat(Double.parseDouble(lat.getText()));
                restaurant.setLng(Double.parseDouble(log.getText()));
            } catch (NumberFormatException e) {
                showAlert("Erreur", "Valeur invalide", "Veuillez entrer des nombres valides", Alert.AlertType.ERROR);
                return;
            }

            restaurant.setImage(imagePath);
            restaurant.setImage1(image1Path);
            restaurant.setImage2(image2Path);

            restaurantService.addRestaurant(restaurant);
            refreshList();
            clearFields();
            showAlert("Succès", "Restaurant ajouté", "Le restaurant a été ajouté avec succès", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de l'ajout", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void supptr() {
        Restaurant selected = listRestaurant.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                restaurantService.deleteRestaurant(selected.getId());
                refreshList();
                showAlert("Succès", "Restaurant supprimé", "Le restaurant a été supprimé avec succès", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression", e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Avertissement", "Aucune sélection", "Veuillez sélectionner un restaurant à supprimer", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void moditr() {
        Restaurant selected = listRestaurant.getSelectionModel().getSelectedItem();
        if (selected != null) {
            try {
                selected.setNom(nom.getText());
                selected.setLocalisation(localisation.getText());
                selected.setDescription(description.getText());

                try {
                    selected.setPrix(Double.parseDouble(prix.getText()));
                    selected.setLat(Double.parseDouble(lat.getText()));
                    selected.setLng(Double.parseDouble(log.getText()));
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Valeur invalide", "Veuillez entrer des nombres valides", Alert.AlertType.ERROR);
                    return;
                }

                if (imagePath != null) selected.setImage(imagePath);
                if (image1Path != null) selected.setImage1(image1Path);
                if (image2Path != null) selected.setImage2(image2Path);

                restaurantService.updateRestaurant(selected);
                refreshList();
                showAlert("Succès", "Restaurant modifié", "Le restaurant a été modifié avec succès", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la modification", e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            showAlert("Avertissement", "Aucune sélection", "Veuillez sélectionner un restaurant à modifier", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void Import() {
        imagePath = importImage(image_id);
    }

    @FXML
    private void Import1() {
        image1Path = importImage(image1_id);
    }

    @FXML
    private void Import2() {
        image2Path = importImage(image2_id);
    }

    private String importImage(ImageView imageView) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                File uploadsDir = new File("uploads");
                if (!uploadsDir.exists()) uploadsDir.mkdir();

                File destFile = new File(uploadsDir, selectedFile.getName());
                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                imageView.setImage(new Image(destFile.toURI().toString()));
                return destFile.getAbsolutePath();
            } catch (IOException e) {
                showAlert("Erreur", "Erreur lors de l'import", e.getMessage(), Alert.AlertType.ERROR);
            }
        }
        return null;
    }

    private void refreshList() throws SQLException {
        restaurants.setAll(restaurantService.getAllRestaurants());
        listRestaurant.setItems(restaurants);
        listRestaurant.setCellFactory(param -> new ListCell<Restaurant>() {
            @Override
            protected void updateItem(Restaurant item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);

                    ImageView imageView = new ImageView();
                    if (item.getImage() != null) {
                        File file = new File(item.getImage());
                        if (file.exists()) {
                            imageView.setImage(new Image(file.toURI().toString()));
                            imageView.setFitHeight(40);
                            imageView.setFitWidth(40);
                            imageView.setPreserveRatio(true);
                        }
                    }

                    VBox vbox = new VBox(5);
                    vbox.getChildren().addAll(
                            new Label("Nom: " + item.getNom()),
                            new Label("Localisation: " + item.getLocalisation()),
                            new Label("Prix: " + item.getPrix() + " DT")
                    );

                    hbox.getChildren().addAll(imageView, vbox);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void clearFields() {
        nom.clear();
        localisation.clear();
        description.clear();
        prix.clear();
        lat.clear();
        log.clear();
        image_id.setImage(null);
        image1_id.setImage(null);
        image2_id.setImage(null);
        imagePath = null;
        image1Path = null;
        image2Path = null;
    }

    private void showAlert(String title, String header, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}