package controllers.User;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import models.Reclamation;
import service.ReclamationService;
import service.UserService;
import utils.Session;

import java.io.File;

public class MesReclamationsController {

    @FXML private TableView<Reclamation> reclamationTable;
    @FXML private TableColumn<Reclamation, String> titreCol;
    @FXML private TableColumn<Reclamation, String> descriptionCol;
    @FXML private TableColumn<Reclamation, String> categorieCol;
    @FXML private TableColumn<Reclamation, String> statutCol;
    @FXML private TableColumn<Reclamation, Void> actionsCol;

    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));

        statutCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String statut, boolean empty) {
                super.updateItem(statut, empty);
                if (empty || statut == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label chip = new Label(statut);
                    chip.setStyle("-fx-background-color: "
                            + (statut.equalsIgnoreCase("En cours") ? "#FFA000;" :
                            statut.equalsIgnoreCase("Traitée") ? "#4CAF50;" :
                                    "#E53935;")
                            + " -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 10;");
                    setGraphic(chip);
                }
            }
        });
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox container = new HBox(10, editBtn, deleteBtn);

            {
                editBtn.getStyleClass().add("btn-edit");
                deleteBtn.getStyleClass().add("btn-delete");

                editBtn.setOnAction(e -> {
                    Reclamation selected = getTableView().getItems().get(getIndex());
                    handleEdit(selected);
                });

                deleteBtn.setOnAction(e -> {
                    Reclamation selected = getTableView().getItems().get(getIndex());
                    handleDelete(selected);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(container);
                }
            }
        });

        loadTableData();
    }

    private void loadTableData() {
        ObservableList<Reclamation> list = FXCollections.observableArrayList(
                reclamationService.getByAuteur(Session.getCurrentUser().getId())
        );
        reclamationTable.setItems(list);
    }

    private void handleEdit(Reclamation r) {
        Dialog<Reclamation> dialog = new Dialog<>();
        dialog.setTitle("Modifier la réclamation");
        dialog.setHeaderText("Modifiez les informations ci-dessous (le statut n’est pas modifiable)");

        ButtonType updateBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateBtn, ButtonType.CANCEL);

        // Fields
        TextField titreField = new TextField(r.getTitre());
        applyFieldStyle(titreField);
        TextArea descriptionField = new TextArea(r.getDescription());
        descriptionField.setPrefRowCount(4);
        applyFieldStyle(descriptionField);
        ComboBox<String> categorieBox = new ComboBox<>();
        categorieBox.getItems().addAll("Hôte", "Transporteur", "Restaurant", "Voyageur");
        categorieBox.setValue(r.getCategorie());
        applyFieldStyle(categorieBox);

        Label cibleLabel = new Label("Utilisateur concerné : " + new UserService().getById(r.getCibleId()).getEmail());

        TextField photoField = new TextField(r.getPhoto());
        applyFieldStyle(photoField);
        Button photoBtn = new Button("Choisir");
        photoBtn.setOnAction(ev -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Choisir une photo");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.jpg", "*.png", "*.jpeg"));
            File selected = chooser.showOpenDialog(reclamationTable.getScene().getWindow());
            if (selected != null) photoField.setText(selected.getAbsolutePath());
        });

        TextField documentField = new TextField(r.getDocument());
        applyFieldStyle(documentField);
        Button docBtn = new Button("Choisir");
        docBtn.setOnAction(ev -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Joindre un document");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Documents", "*.pdf", "*.docx"));
            File selected = chooser.showOpenDialog(reclamationTable.getScene().getWindow());
            if (selected != null) documentField.setText(selected.getAbsolutePath());
        });
        photoBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-background-radius: 8; -fx-padding: 5 10;");
        docBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-background-radius: 8; -fx-padding: 5 10;");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setStyle("-fx-padding: 20; -fx-background-color: #f9f9f9; -fx-border-color: #e0e0e0; -fx-border-radius: 10;");

        grid.add(new Label("Titre:"), 0, 0);
        grid.add(titreField, 1, 0);
        grid.add(new Label("Description:"), 0, 1);
        grid.add(descriptionField, 1, 1);
        grid.add(new Label("Catégorie:"), 0, 2);
        grid.add(categorieBox, 1, 2);
        grid.add(new Label("Utilisateur concerné:"), 0, 3);
        grid.add(cibleLabel, 1, 3);
        grid.add(new Label("Photo:"), 0, 4);
        grid.add(photoField, 1, 4);
        grid.add(photoBtn, 2, 4);
        grid.add(new Label("Document:"), 0, 5);
        grid.add(documentField, 1, 5);
        grid.add(docBtn, 2, 5);

        dialog.getDialogPane().setContent(grid);

        // Validation before applying changes
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateBtn) {
                if (titreField.getText().trim().isEmpty() ||
                        descriptionField.getText().trim().isEmpty() ||
                        categorieBox.getValue() == null) {
                    showAlert(Alert.AlertType.WARNING, "Tous les champs sont requis !");
                    return null;
                }

                r.setTitre(titreField.getText().trim());
                r.setDescription(descriptionField.getText().trim());
                r.setCategorie(categorieBox.getValue());
                r.setPhoto(photoField.getText().trim());
                r.setDocument(documentField.getText().trim());
                return r;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(updatedReclamation -> {
            reclamationService.update(updatedReclamation);
            loadTableData();
        });
    }
    private void showAlert(Alert.AlertType type, String msg) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }

    private void applyFieldStyle(Control control) {
        control.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-padding: 8;" +
                "-fx-border-color: #ccc; -fx-background-color: white;");
    }

    private void handleDelete(Reclamation r) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Supprimer");
        confirm.setHeaderText("Confirmer la suppression");
        confirm.setContentText("Voulez-vous supprimer cette réclamation ?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                reclamationService.delete(r.getId());
                loadTableData();
            }
        });
    }
}
