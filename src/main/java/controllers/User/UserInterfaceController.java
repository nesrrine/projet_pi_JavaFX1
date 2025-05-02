package controllers.User;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import utils.Session;
import utils.PermissionManager;

import java.io.IOException;

public class UserInterfaceController {

    @FXML private VBox mainContent;
    @FXML private Button logoutButton;
    @FXML private ImageView profileIcon;
    @FXML private MenuBar menuBar;

    @FXML
    public void initialize() {
        loadPartial("/user/HomeUserWelcome.fxml");
        configureMenuBasedOnRole();
    }

    private void configureMenuBasedOnRole() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return;
        
        String role = currentUser.getRole();
        System.out.println("Current user role: " + role); // Debug log
        
        // Configure each menu based on user role
        for (Menu menu : menuBar.getMenus()) {
            String menuText = menu.getText();
            
            // Handle Restaurants menu
            if ("Restaurants".equals(menuText)) {
                configureRestaurantsMenu(menu, role);
            }
            
            // Handle Transports menu
            else if ("Transports".equals(menuText)) {
                configureTransportsMenu(menu, role);
            }
            
            // Handle Vlogs menu
            else if ("Vlogs".equals(menuText)) {
                configureVlogsMenu(menu, role);
            }
            
            // Handle Réclamations menu
            else if ("Réclamations".equals(menuText)) {
                configureReclamationsMenu(menu, role);
            }
        }
    }
    
    private void configureRestaurantsMenu(Menu menu, String role) {
        // All users can view all restaurants
        // Remove "Mes Restaurants" for users who can't manage restaurants
        if (!PermissionManager.canViewOwnRestaurants()) {
            menu.getItems().removeIf(item -> "Mes restaurants".equals(item.getText()));
        }
        
        // Remove "Créer Restaurant" for users who can't create restaurants
        if (!PermissionManager.canCreateRestaurant()) {
            menu.getItems().removeIf(item -> "Créer un restaurant".equals(item.getText()));
        }
        
        // Debug log for menu items
        System.out.println("Restaurant menu items after configuration:");
        for (MenuItem item : menu.getItems()) {
            System.out.println(" - " + item.getText());
        }
    }
    
    private void configureTransportsMenu(Menu menu, String role) {
        // All users can view all transports
        // Remove "Mes Transports" for users who can't manage transports
        if (!PermissionManager.canViewOwnTransports()) {
            menu.getItems().removeIf(item -> "Mes transports".equals(item.getText()));
        }
        
        // Remove "Créer Transport" for users who can't create transports
        if (!PermissionManager.canCreateTransport()) {
            menu.getItems().removeIf(item -> "Créer un transport".equals(item.getText()));
        }
        
        // Debug log for menu items
        System.out.println("Transport menu items after configuration:");
        for (MenuItem item : menu.getItems()) {
            System.out.println(" - " + item.getText());
        }
    }
    
    private void configureVlogsMenu(Menu menu, String role) {
        // All users can view all vlogs
        // Remove "Mes Vlogs" for Voyageur users
        if (PermissionManager.ROLE_VOYAGEUR.equals(role)) {
            menu.getItems().removeIf(item -> "Mes vlogs".equals(item.getText()));
        }
        
        // Remove "Créer Vlog" for users who can't create vlogs
        if (!PermissionManager.canCreateVlog()) {
            menu.getItems().removeIf(item -> "Créer un vlog".equals(item.getText()));
        }
        
        // Debug log for menu items
        System.out.println("Vlog menu items after configuration:");
        for (MenuItem item : menu.getItems()) {
            System.out.println(" - " + item.getText());
        }
    }
    
    private void configureReclamationsMenu(Menu menu, String role) {
        // All users can access their own reclamations
        
        // Admin users can manage all reclamations
        if (!PermissionManager.canViewAllReclamations()) {
            menu.getItems().removeIf(item -> "Toutes les réclamations".equals(item.getText()));
        }
        
        // Only users who can create reclamations should see the create option
        if (!PermissionManager.canCreateReclamation()) {
            menu.getItems().removeIf(item -> "Créer une réclamation".equals(item.getText()));
        }
        
        // Debug log for menu items
        System.out.println("Reclamation menu items after configuration:");
        for (MenuItem item : menu.getItems()) {
            System.out.println(" - " + item.getText());
        }
    }

    @FXML
    private void handleLogout() {
        Session.clear();
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Login.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleProfileClick() {
        loadPartial("/Profile.fxml");
    }

    @FXML
    private void handleAllVlogs() {
        loadPartial("/user/TousLesVlogs.fxml");
    }

    @FXML
    private void handleMyVlogs() {
        if (!PermissionManager.canCreateVlog()) {
            showAlert("Accès refusé", "Vous n'avez pas l'autorisation d'accéder à vos vlogs.");
            return;
        }
        loadPartial("/user/MesVlogs.fxml");
    }

    @FXML
    private void handleCreateVlog() {
        if (!PermissionManager.canCreateVlog()) {
            showAlert("Accès refusé", "Vous n'avez pas l'autorisation de créer des vlogs.");
            return;
        }
        loadPartial("/user/CreateVlog.fxml");
    }

    @FXML
    private void handleMyReclamations() {
        // Allow Voyageurs to access their reclamations
        loadPartial("/user/MesReclamations.fxml");
    }

    @FXML
    private void handleCreateReclamation() {
        // Allow Voyageurs to create reclamations
        loadPartial("/user/CreateReclamation.fxml");
    }

    @FXML
    public void handleAllTransports() {
        loadPartial("/user/TousLesTransports.fxml");
    }

    @FXML
    private void handleMyTransports() {
        if (!PermissionManager.canViewOwnTransports()) {
            showAlert("Accès refusé", "Vous n'avez pas l'autorisation d'accéder à vos transports.");
            return;
        }
        loadPartial("/user/MesTransports.fxml");
    }

    @FXML
    private void handleCreateTransport() {
        if (!PermissionManager.canCreateTransport()) {
            showAlert("Accès refusé", "Vous n'avez pas l'autorisation de créer des transports.");
            return;
        }
        loadPartial("/user/CreateTransport.fxml");
    }

    @FXML
    public void handleAllRestaurants() {
        loadPartial("/user/TousLesRestaurants.fxml");
    }

    @FXML
    private void handleMyRestaurants() {
        if (!PermissionManager.canViewOwnRestaurants()) {
            showAlert("Accès refusé", "Vous n'avez pas l'autorisation d'accéder à vos restaurants.");
            return;
        }
        loadPartial("/user/MesRestaurants.fxml");
    }

    @FXML
    private void handleCreateRestaurant() {
        if (!PermissionManager.canCreateRestaurant()) {
            showAlert("Accès refusé", "Vous n'avez pas l'autorisation de créer des restaurants.");
            return;
        }
        loadPartial("/user/CreateRestaurant.fxml");
    }

    // This method is replaced by specific permission checks using PermissionManager

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void loadPartial(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            mainContent.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
