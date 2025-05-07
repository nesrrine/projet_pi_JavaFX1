package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.Region;
import javafx.application.Platform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import javafx.stage.Stage;
import org.json.JSONObject;

public class ChatbotController {

    @FXML private ScrollPane scrollPane;
    @FXML private VBox chatBox;
    @FXML private TextField messageField;
    @FXML private Button sendButton;
    @FXML private Button continueButton;

    private final String COHERE_API_KEY = "Rkca66hIPmjXMyRWMFZdMejdMyr1g5q6XZG5PGsZ";
    private final String SYSTEM_PROMPT = "Tu es un assistant virtuel pour une application appelée \"Home_Swap\". "
            + "Tu ne réponds qu'aux questions liées à cette application.\n\n"
            + "Voici ce que fait Home_Swap :\n"
            + "- Permet aux utilisateurs de publier leur logement pour un échange temporaire.\n"
            + "- Permet de découvrir des logements disponibles dans le monde entier.\n"
            + "- Facilite la communication entre les membres pour organiser les échanges.\n"
            + "- Met en place des mesures de sécurité pour garantir des échanges fiables.\n\n"
            + "Informations détaillées sur les fonctionnalités :\n\n"
            + "1. Publication de logement :\n"
            + "   - Les utilisateurs avec un compte 'hôte' peuvent publier leur logement en cliquant sur 'Créer un logement' dans le menu Logements\n"
            + "   - Ils doivent fournir un titre, une description détaillée, la localisation et le prix\n"
            + "   - Les photos peuvent être ajoutées pour montrer l'intérieur et l'extérieur du logement\n"
            + "   - Les logements publiés apparaissent dans la liste 'Mes Logements' où ils peuvent être modifiés ou supprimés\n\n"
            + "2. Recherche et réservation :\n"
            + "   - Tous les utilisateurs peuvent consulter les logements disponibles dans 'Tous les Logements'\n"
            + "   - Ils peuvent filtrer par localisation, prix ou disponibilité\n"
            + "   - Pour réserver, il suffit de cliquer sur 'Réserver' et de choisir les dates souhaitées\n\n"
            + "3. Communication :\n"
            + "   - Un système de messagerie permet aux utilisateurs de discuter des détails de l'échange\n"
            + "   - Les notifications informent des nouvelles demandes ou messages\n\n"
            + "4. Sécurité :\n"
            + "   - Vérification des profils utilisateurs\n"
            + "   - Système d'évaluation après chaque échange\n"
            + "   - Possibilité de signaler des problèmes via le système de réclamation\n\n"
            + "Si l'utilisateur pose une question qui n'est pas liée à Home_Swap, tu dois répondre gentiment :\n"
            + "\"Désolé, je ne suis formé que pour répondre aux questions concernant Home_Swap.\"\n\n"
            + "Réponds de façon claire, utile et concise.";

    @FXML
    public void initialize() {
        // Configurer le ScrollPane
        scrollPane.setFitToWidth(true);
        chatBox.setSpacing(10);
        
        // Ajouter un message de bienvenue avec Text.setWrappingWidth pour assurer le bon affichage
        Platform.runLater(() -> {
            addBotMessage("Bonjour ! Je suis l'assistant virtuel de Home_Swap. Comment puis-je vous aider aujourd'hui ?");
        });
        
        // Configurer le bouton d'envoi
        sendButton.setOnAction(event -> handleSendMessage());
        
        // Permettre l'envoi du message avec la touche Entrée
        messageField.setOnAction(event -> handleSendMessage());
    }
    
    @FXML
    private void handleSendMessage() {
        String message = messageField.getText().trim();
        if (message.isEmpty()) return;
        
        // Ajouter le message de l'utilisateur à la conversation
        addUserMessage(message);
        
        // Vider le champ de texte
        messageField.clear();
        
        // Envoyer la requête à l'API Cohere
        CompletableFuture.runAsync(() -> {
            String response = getCohereResponse(message);
            Platform.runLater(() -> addBotMessage(response));
        });
    }
    
    private void addUserMessage(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_RIGHT);
        messageContainer.setPrefWidth(scrollPane.getWidth() - 20);
        
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-background-color: #0084FF; -fx-background-radius: 15; -fx-padding: 10;");
        textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        textFlow.setMaxWidth(scrollPane.getWidth() * 0.7);
        
        Text text = new Text(message);
        text.setFill(Color.WHITE);
        text.setFont(Font.font("System", FontWeight.NORMAL, 14));
        // Assurer que le texte s'enroule correctement
        text.setWrappingWidth(scrollPane.getWidth() * 0.65);
        textFlow.getChildren().add(text);
        
        messageContainer.getChildren().add(textFlow);
        chatBox.getChildren().add(messageContainer);
        
        // Faire défiler vers le bas
        scrollToBottom();
    }
    
    private void addBotMessage(String message) {
        HBox messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER_LEFT);
        messageContainer.setPrefWidth(scrollPane.getWidth() - 20);
        
        TextFlow textFlow = new TextFlow();
        textFlow.setStyle("-fx-background-color: #E9E9EB; -fx-background-radius: 15; -fx-padding: 10;");
        textFlow.setPrefWidth(Region.USE_COMPUTED_SIZE);
        textFlow.setMaxWidth(scrollPane.getWidth() * 0.7);
        
        Text text = new Text(message);
        text.setFill(Color.BLACK);
        text.setFont(Font.font("System", FontWeight.NORMAL, 14));
        // Assurer que le texte s'enroule correctement
        text.setWrappingWidth(scrollPane.getWidth() * 0.65);
        textFlow.getChildren().add(text);
        
        messageContainer.getChildren().add(textFlow);
        chatBox.getChildren().add(messageContainer);
        
        // Faire défiler vers le bas
        scrollToBottom();
    }
    
    private void scrollToBottom() {
        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }
    
    private String getCohereResponse(String userMessage) {
        try {
            URL url = new URL("https://api.cohere.ai/v1/chat");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + COHERE_API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            
            // Créer le corps de la requête JSON
            JSONObject requestBody = new JSONObject();
            requestBody.put("message", userMessage);
            requestBody.put("model", "command");
            requestBody.put("preamble", SYSTEM_PROMPT);
            
            // Envoyer la requête
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            // Lire la réponse
            StringBuilder response = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }
            
            // Extraire le texte de la réponse JSON
            JSONObject jsonResponse = new JSONObject(response.toString());
            return jsonResponse.getString("text");
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Désolé, je n'ai pas pu traiter votre demande. Veuillez réessayer plus tard.";
        }
    }

    @FXML
    private void handleContinue() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/UserInterface.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) continueButton.getScene().getWindow();
            stage.setScene(new Scene(root));


        } catch (IOException e) {
            e.printStackTrace();
            //showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'interface.");
        }
    }
}