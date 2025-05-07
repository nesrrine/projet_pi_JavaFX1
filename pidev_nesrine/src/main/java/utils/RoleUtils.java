package utils;

import java.util.Arrays;
import java.util.List;

/**
 * Classe utilitaire pour la gestion des rôles utilisateurs
 */
public class RoleUtils {
    
    /**
     * Retourne la liste des rôles disponibles dans le système
     * @return Une liste immuable contenant tous les types de rôles disponibles
     */
    public static List<String> getAvailableRoles() {
        return Arrays.asList(
            "Client", 
            "Admin", 
            "Hôte", 
            "Transporteur", 
            "Restaurant", 
            "Voyageur"
        );
    }
    
    /**
     * Vérifie si un rôle est valide
     * @param role Le rôle à vérifier
     * @return true si le rôle est valide, false sinon
     */
    public static boolean isValidRole(String role) {
        return role != null && getAvailableRoles().contains(role);
    }
}
