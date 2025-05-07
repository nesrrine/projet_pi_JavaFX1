package service;

import models.Reclamation;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService implements IService<Reclamation> {

    private final Connection con;

    public ReclamationService() {
        this.con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Reclamation r) {
        String sql = "INSERT INTO reclamation (auteur_id, cible_id, titre, description, statut, photo, document, categorie) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getAuteurId());
            ps.setInt(2, r.getCibleId());
            ps.setString(3, r.getTitre());
            ps.setString(4, r.getDescription());
            ps.setString(5, r.getStatut());
            ps.setString(6, r.getPhoto());
            ps.setString(7, r.getDocument());
            ps.setString(8, r.getCategorie());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding reclamation: " + e.getMessage());
        }
    }
    public List<Reclamation> getByAuteur(int auteurId) {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM reclamation WHERE auteur_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, auteurId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getInt("auteur_id"),
                        rs.getInt("cible_id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_soumission").toLocalDateTime(),
                        rs.getString("photo"),
                        rs.getString("document"),
                        rs.getString("categorie")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public void update(Reclamation r) {
        String sql = "UPDATE reclamation SET titre=?, description=?, statut=?, photo=?, document=?, categorie=? WHERE id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getTitre());
            ps.setString(2, r.getDescription());
            ps.setString(3, r.getStatut());
            ps.setString(4, r.getPhoto());
            ps.setString(5, r.getDocument());
            ps.setString(6, r.getCategorie());
            ps.setInt(7, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating reclamation: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reclamation WHERE id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting reclamation: " + e.getMessage());
        }
    }

    @Override
    public List<Reclamation> display() {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM reclamation";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getInt("auteur_id"),
                        rs.getInt("cible_id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_soumission").toLocalDateTime(),
                        rs.getString("photo"),
                        rs.getString("document"),
                        rs.getString("categorie")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reclamations: " + e.getMessage());
        }

        return list;
    }
    public List<Reclamation> getByCible(int cibleId) {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM reclamation WHERE cible_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cibleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getInt("auteur_id"),
                        rs.getInt("cible_id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_soumission").toLocalDateTime(),
                        rs.getString("photo"),
                        rs.getString("document"),
                        rs.getString("categorie")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching reclamations by cible: " + e.getMessage());
        }

        return list;
    }
    public void updateStatut(int id, String nouveauStatut) {
        String sql = "UPDATE reclamation SET statut = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nouveauStatut);
            ps.setInt(2, id);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Statut mis à jour avec succès pour la réclamation ID : " + id);
            } else {
                System.out.println("Aucune réclamation trouvée avec l'ID : " + id);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du statut : " + e.getMessage());
        }
    }
    public int countReclamationsRecues(int userId) {
        String sql = "SELECT COUNT(*) FROM reclamation WHERE cible_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    public List<Reclamation> getFilteredReclamations(String searchText, String statut, LocalDate dateSoumission, String utilisateur) {
        List<Reclamation> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM reclamation WHERE 1=1");

        // Ajouter des conditions en fonction des filtres
        if (searchText != null && !searchText.isEmpty()) {
            sql.append(" AND (titre LIKE ? OR description LIKE ?)");
        }
        if (statut != null && !statut.isEmpty()) {
            sql.append(" AND statut = ?");
        }
        if (dateSoumission != null) {
            sql.append(" AND DATE(date_soumission) = ?");
        }
        if (utilisateur != null && !utilisateur.isEmpty()) {
            sql.append(" AND auteur_id = (SELECT id FROM user WHERE email = ?)");
        }

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int index = 1;

            if (searchText != null && !searchText.isEmpty()) {
                ps.setString(index++, "%" + searchText + "%");
                ps.setString(index++, "%" + searchText + "%");
            }
            if (statut != null && !statut.isEmpty()) {
                ps.setString(index++, statut);
            }
            if (dateSoumission != null) {
                ps.setDate(index++, Date.valueOf(dateSoumission));
            }
            if (utilisateur != null && !utilisateur.isEmpty()) {
                ps.setString(index++, utilisateur);
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getInt("auteur_id"),
                        rs.getInt("cible_id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("statut"),
                        rs.getTimestamp("date_soumission").toLocalDateTime(),
                        rs.getString("photo"),
                        rs.getString("document"),
                        rs.getString("categorie")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching filtered reclamations: " + e.getMessage());
        }
        return list;
    }

    public ReclamationService(Connection con) {
        this.con = con;
    }
}
