package controllers.User;
import models.User;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Reclamation;
import service.ReclamationService;
import service.UserService;
import utils.Session;

import javafx.util.Callback; // ✅ correction ici
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MesReclamationsController {
    // Ajout des filtres
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statutFilter;
    @FXML private TableView<Reclamation> reclamationTable;
    @FXML private TableColumn<Reclamation, String> titreCol;
    @FXML private TableColumn<Reclamation, String> descriptionCol;
    @FXML private TableColumn<Reclamation, String> categorieCol;
    @FXML private TableColumn<Reclamation, String> statutCol;
    @FXML private TableColumn<Reclamation, Void> actionsCol;
    @FXML private TableColumn<Reclamation, String> envoyeeACol;
//--
@FXML private ComboBox<String> utilisateurFilter;  // Déclaration du ComboBox pour les utilisateurs

    @FXML private TableView<Reclamation> tableView;
    @FXML private TableColumn<Reclamation, LocalDate> dateCol;
    @FXML private TableColumn<Reclamation, String> utilisateurCol;
    //--
    private final ReclamationService reclamationService = new ReclamationService();

    @FXML
    public void initialize() {
        // Initialiser les colonnes de la table
        titreCol.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        categorieCol.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        statutCol.setCellValueFactory(new PropertyValueFactory<>("statut"));

        // Personnalisation de la colonne 'statut'
        statutCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Reclamation, String> call(TableColumn<Reclamation, String> col) {
                return new TableCell<>() {
                    @Override
                    protected void updateItem(String statut, boolean empty) {
                        super.updateItem(statut, empty);
                        if (empty || statut == null) {
                            setText(null);
                            setGraphic(null);
                        } else {
                            Label chip = new Label(statut);
                            String bgColor = switch (statut.toLowerCase()) {
                                case "en cours" -> "#FFA000";
                                case "traitée" -> "#4CAF50";
                                default -> "#E53935";
                            };
                            chip.setStyle("-fx-background-color: " + bgColor +
                                    "; -fx-text-fill: white; -fx-padding: 4 8; -fx-background-radius: 10;");
                            setGraphic(chip);
                            setText(null);
                        }
                    }
                };
            }
        });


        // Remplir la colonne 'envoyée à' avec l'email du destinataire
        envoyeeACol.setCellValueFactory(cellData -> {
            int cibleId = cellData.getValue().getCibleId();
            String cibleEmail = "";
            try {
                cibleEmail = new UserService().getById(cibleId).getEmail();
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération de l'utilisateur : " + e.getMessage());
            }
            return new SimpleStringProperty(cibleEmail);
        });

        // Ajouter des actions (modification du statut et voir le détail)
        actionsCol.setCellFactory(new Callback<>() {
            @Override
            public TableCell<Reclamation, Void> call(TableColumn<Reclamation, Void> param) {
                return new TableCell<>() {
                    private final Button detailBtn = new Button("Voir Détail");
                    private final HBox hbox = new HBox(10, detailBtn);

                    {
                        hbox.setAlignment(Pos.CENTER);

                        // Voir le détail d'une réclamation
                        detailBtn.setOnAction(e -> {
                            Reclamation r = getTableView().getItems().get(getIndex());
                            showDetailPopup(r);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(hbox);
                        }
                    }
                };
            }
        });

        // Initialisation des filtres
        statutFilter.setItems(FXCollections.observableArrayList("en cours", "traitée", "rejetée"));
        statutFilter.setValue(""); // Valeur initiale vide

        // Remplir le filtre utilisateur avec les utilisateurs existants
        List<String> usersList = getUsersList();
        utilisateurFilter.setItems(FXCollections.observableArrayList(usersList));
        utilisateurFilter.setValue("");  // Valeur initiale vide

        // Écouter les changements sur les champs de recherche, statut et utilisateur
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        statutFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());
        utilisateurFilter.valueProperty().addListener((observable, oldValue, newValue) -> applyFilters());

        // Charger les données de la table
        loadTableData();
    }

    // Méthode pour récupérer les utilisateurs sous forme de liste d'emails
    private List<String> getUsersList() {
        List<String> users = new ArrayList<>();
        try {
            UserService userService = new UserService();
            List<User> userList = userService.getAllUsers();
            for (User user : userList) {
                users.add(user.getEmail());  // Ajoute l'email de chaque utilisateur à la liste
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;}


    private void loadTableData() {
        ObservableList<Reclamation> list = FXCollections.observableArrayList(
                reclamationService.getByAuteur(Session.getCurrentUser().getId())
        );
        reclamationTable.setItems(list);
    }

    // Appliquer les filtres
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String statut = statutFilter.getValue();
        String utilisateur = utilisateurFilter.getValue();

        // Appliquer le filtrage en fonction du texte de recherche, du statut et de l'utilisateur
        ObservableList<Reclamation> filteredList = FXCollections.observableArrayList();

        for (Reclamation r : reclamationService.getByAuteur(Session.getCurrentUser().getId())) {
            boolean matchesSearch = r.getTitre().toLowerCase().contains(searchText) || r.getDescription().toLowerCase().contains(searchText);
            boolean matchesStatut = statut.isEmpty() || r.getStatut().equalsIgnoreCase(statut);
            boolean matchesUtilisateur = utilisateur.isEmpty() || new UserService().getById(r.getCibleId()).getEmail().equalsIgnoreCase(utilisateur);

            if (matchesSearch && matchesStatut && matchesUtilisateur) {
                filteredList.add(r);
            }
        }

        reclamationTable.setItems(filteredList);
    }
    private void handleEdit(Reclamation r) {
        Dialog<Reclamation> dialog = new Dialog<>();
        dialog.setTitle("Modifier la réclamation");
        dialog.setHeaderText("Modifiez les informations ci-dessous (le statut n’est pas modifiable)");

        ButtonType updateBtn = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateBtn, ButtonType.CANCEL);

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

        GridPane grid = new GridPane();
        grid.setVgap(10);
        grid.setHgap(10);
        grid.addRow(0, new Label("Titre :"), titreField);
        grid.addRow(1, new Label("Description :"), descriptionField);
        grid.addRow(2, new Label("Catégorie :"), categorieBox);
        grid.addRow(3, cibleLabel);
        grid.addRow(4, new Label("Photo :"), photoField, photoBtn);
        grid.addRow(5, new Label("Document :"), documentField, docBtn);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateBtn) {
                r.setTitre(titreField.getText());
                r.setDescription(descriptionField.getText());
                r.setCategorie(categorieBox.getValue());
                r.setPhoto(photoField.getText());
                r.setDocument(documentField.getText());
                reclamationService.update(r);
                loadTableData();
                return r;
            }
            return null;
        });

        dialog.showAndWait();
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
    private void showDetailPopup(Reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/ReclamationDetailView.fxml"));
            Parent root = loader.load();

            ReclamationDetailController controller = loader.getController();
            controller.initializeDetail(r.getPhoto(), r.getDocument());

            Stage stage = new Stage();
            stage.setTitle("Détail de la Réclamation");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
