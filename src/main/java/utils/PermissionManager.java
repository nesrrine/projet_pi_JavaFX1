package utils;

import models.User;

/**
 * Utility class to manage role-based permissions across the application
 */
public class PermissionManager {
    
    // Role constants
    public static final String ROLE_ADMIN = "Admin";
    public static final String ROLE_CLIENT = "Client";
    public static final String ROLE_TRANSPORTEUR = "Transporteur";
    public static final String ROLE_RESTAURANT = "Restaurant";
    public static final String ROLE_VOYAGEUR = "Voyageur";
    
    /**
     * Check if the current user can create restaurants
     * @return true if the user has permission
     */
    public static boolean canCreateRestaurant() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        String role = currentUser.getRole();
        boolean hasPermission = ROLE_ADMIN.equals(role) || ROLE_RESTAURANT.equals(role);
        System.out.println("canCreateRestaurant for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can edit restaurants
     * @return true if the user has permission
     */
    public static boolean canEditRestaurant() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        String role = currentUser.getRole();
        boolean hasPermission = ROLE_ADMIN.equals(role) || ROLE_RESTAURANT.equals(role);
        System.out.println("canEditRestaurant for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can create transports
     * @return true if the user has permission
     */
    public static boolean canCreateTransport() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        String role = currentUser.getRole();
        boolean hasPermission = ROLE_ADMIN.equals(role) || ROLE_TRANSPORTEUR.equals(role);
        System.out.println("canCreateTransport for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can edit transports
     * @return true if the user has permission
     */
    public static boolean canEditTransport() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        String role = currentUser.getRole();
        boolean hasPermission = ROLE_ADMIN.equals(role) || ROLE_TRANSPORTEUR.equals(role);
        System.out.println("canEditTransport for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can create vlogs
     * @return true if the user has permission
     */
    public static boolean canCreateVlog() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        String role = currentUser.getRole();
        // All users except Voyageur can create vlogs
        boolean hasPermission = !ROLE_VOYAGEUR.equals(role);
        System.out.println("canCreateVlog for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can create reclamations
     * @return true if the user has permission
     */
    public static boolean canCreateReclamation() {
        // All authenticated users can create reclamations
        User currentUser = Session.getCurrentUser();
        boolean hasPermission = currentUser != null;
        if (currentUser != null) {
            System.out.println("canCreateReclamation for role " + currentUser.getRole() + ": " + hasPermission);
        }
        return hasPermission;
    }
    
    /**
     * Check if the current user has admin privileges
     * @return true if the user is an admin
     */
    public static boolean isAdmin() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        boolean isAdmin = ROLE_ADMIN.equals(currentUser.getRole());
        System.out.println("isAdmin for role " + currentUser.getRole() + ": " + isAdmin);
        return isAdmin;
    }
    
    /**
     * Check if the current user can view their own restaurants
     * @return true if the user has permission
     */
    public static boolean canViewOwnRestaurants() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        String role = currentUser.getRole();
        boolean hasPermission = ROLE_ADMIN.equals(role) || ROLE_RESTAURANT.equals(role);
        System.out.println("canViewOwnRestaurants for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can view their own transports
     * @return true if the user has permission
     */
    public static boolean canViewOwnTransports() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        String role = currentUser.getRole();
        boolean hasPermission = ROLE_ADMIN.equals(role) || ROLE_TRANSPORTEUR.equals(role);
        System.out.println("canViewOwnTransports for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can edit restaurant items in a list
     * @return true if the user has permission
     */
    public static boolean canEditRestaurantItems() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        String role = currentUser.getRole();
        boolean hasPermission = ROLE_ADMIN.equals(role) || ROLE_RESTAURANT.equals(role);
        System.out.println("canEditRestaurantItems for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can edit transport items in a list
     * @return true if the user has permission
     */
    public static boolean canEditTransportItems() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        String role = currentUser.getRole();
        boolean hasPermission = ROLE_ADMIN.equals(role) || ROLE_TRANSPORTEUR.equals(role);
        System.out.println("canEditTransportItems for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can edit vlog items in a list
     * @return true if the user has permission
     */
    public static boolean canEditVlogItems() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        // All users except Voyageur can edit their own vlogs
        String role = currentUser.getRole();
        boolean hasPermission = !ROLE_VOYAGEUR.equals(role);
        System.out.println("canEditVlogItems for role " + role + ": " + hasPermission);
        return hasPermission;
    }
    
    /**
     * Check if the current user can manage reclamations
     * @return true if the user has permission
     */
    public static boolean canManageReclamations() {
        User currentUser = Session.getCurrentUser();
        if (currentUser == null) return false;
        
        // All users can manage their own reclamations
        // Admin can manage all reclamations
        System.out.println("canManageReclamations for role " + currentUser.getRole() + ": true");
        return true;
    }
    
    /**
     * Check if the current user can view all reclamations
     * @return true if the user has permission
     */
    public static boolean canViewAllReclamations() {
        boolean hasPermission = isAdmin();
        User currentUser = Session.getCurrentUser();
        if (currentUser != null) {
            System.out.println("canViewAllReclamations for role " + currentUser.getRole() + ": " + hasPermission);
        }
        return hasPermission;
    }
}
