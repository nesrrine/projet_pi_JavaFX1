package service;
import models.User;

import models.Reaction;
import models.ReactionType;
import utils.MyDatabase; // Ton utilitaire de connexion à la base

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ReactionService {

    private final Connection con;

    // Constructeur corrigé : il doit être ReactionService et non NotificationService
    public ReactionService() {
        this.con = MyDatabase.getInstance().getCon(); // On récupère la connexion à la base
    }

    public void reactToVlog(Reaction reaction) {
        try {
            if (hasReacted(reaction.getVlogId(), reaction.getUserId())) {
                // Mise à jour si déjà existant
                String update = "UPDATE reactions SET type = ? WHERE vlog_id = ? AND user_id = ?";
                PreparedStatement ps = con.prepareStatement(update); // Utilisation de con au lieu de cnx
                ps.setString(1, reaction.getType().name());
                ps.setInt(2, reaction.getVlogId());
                ps.setInt(3, reaction.getUserId());
                ps.executeUpdate();
            } else {
                // Insertion sinon
                String insert = "INSERT INTO reactions (vlog_id, user_id, type) VALUES (?, ?, ?)";
                PreparedStatement ps = con.prepareStatement(insert); // Utilisation de con au lieu de cnx
                ps.setInt(1, reaction.getVlogId());
                ps.setInt(2, reaction.getUserId());
                ps.setString(3, reaction.getType().name());
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la réaction : " + e.getMessage());
        }
    }

    public boolean hasReacted(int vlogId, int userId) {
        try {
            String query = "SELECT * FROM reactions WHERE vlog_id = ? AND user_id = ?";
            PreparedStatement ps = con.prepareStatement(query); // Utilisation de con au lieu de cnx
            ps.setInt(1, vlogId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de réaction : " + e.getMessage());
            return false;
        }
    }

    public ReactionType getUserReactionType(int vlogId, int userId) {
        try {
            String query = "SELECT type FROM reactions WHERE vlog_id = ? AND user_id = ?";
            PreparedStatement ps = con.prepareStatement(query); // Utilisation de con au lieu de cnx
            ps.setInt(1, vlogId);
            ps.setInt(2, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return ReactionType.valueOf(rs.getString("type"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la réaction : " + e.getMessage());
        }
        return null;
    }

    public void removeReaction(int vlogId, int userId) {
        try {
            String delete = "DELETE FROM reactions WHERE vlog_id = ? AND user_id = ?";
            PreparedStatement ps = con.prepareStatement(delete); // Utilisation de con au lieu de cnx
            ps.setInt(1, vlogId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de réaction : " + e.getMessage());
        }
    }

    public Map<ReactionType, Integer> countReactionsByType(int vlogId) {
        Map<ReactionType, Integer> counts = new HashMap<>();
        try {
            String query = "SELECT type, COUNT(*) as count FROM reactions WHERE vlog_id = ? GROUP BY type";
            PreparedStatement ps = con.prepareStatement(query); // Utilisation de con au lieu de cnx
            ps.setInt(1, vlogId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                ReactionType type = ReactionType.valueOf(rs.getString("type"));
                int count = rs.getInt("count");
                counts.put(type, count);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des réactions : " + e.getMessage());
        }
        return counts;
    }

    // Cette méthode nécessite ta classe User => je la commente pour l’instant

    public List<User> getUsersByReaction(int vlogId, ReactionType type) {
        List<User> users = new ArrayList<>();
        try {
            // Requête SQL pour récupérer les utilisateurs ayant réagi à un vlog donné
            String query = "SELECT u.id, u.first_name, u.last_name, u.email, u.address, u.phone, u.birth_date, u.role " +
                    "FROM users u " +
                    "JOIN reactions r ON u.id = r.user_id " +
                    "WHERE r.vlog_id = ? AND r.type = ?";
            PreparedStatement ps = con.prepareStatement(query); // Utilisation de 'con' pour la connexion à la base
            ps.setInt(1, vlogId); // Définition du vlogId dans la requête
            ps.setString(2, type.name()); // Définition du type de réaction dans la requête

            ResultSet rs = ps.executeQuery(); // Exécution de la requête
            while (rs.next()) {
                // Récupérer les informations de l'utilisateur à partir du ResultSet
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("first_name"), // Correspond à 'first_name' dans la base de données
                        rs.getString("last_name"),  // Correspond à 'last_name' dans la base de données
                        rs.getString("email"),
                        null, // Le mot de passe n'est pas nécessaire ici, donc on passe 'null'
                        rs.getString("address"), // Récupérer l'adresse
                        rs.getString("phone"),   // Récupérer le téléphone
                        rs.getDate("birth_date") != null ? rs.getDate("birth_date").toLocalDate() : null, // Convertir la date si elle est non nulle
                        rs.getString("role")     // Récupérer le rôle
                );
                users.add(user); // Ajout de l'utilisateur à la liste
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des utilisateurs par réaction : " + e.getMessage());
        }
        return users;
    }

}
