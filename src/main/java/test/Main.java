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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);
            primaryStage.setTitle("Connexion - Gestion des voyages");
            primaryStage.setScene(scene);

            primaryStage.setResizable(true); // permettre l'agrandissement manuel
            primaryStage.setMaximized(true); // ouvrir la fenêtre directement en grand

            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors du chargement de l'interface de connexion.");
        }
    }
}