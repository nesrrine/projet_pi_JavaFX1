package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/reservation.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Connexion - Gestion des voyages");
            primaryStage.setScene(scene);

            // Automatically fit window size to the FXML content
            primaryStage.sizeToScene();
            primaryStage.setResizable(false); // Optional: disable resizing if you want fixed layout

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de l'interface de connexion.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
