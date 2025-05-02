package service;

import models.Logement;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogementService implements IService<Logement> {

    private final Connection con;

    public LogementService() {
        this.con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Logement l) {
        String sql = "INSERT INTO logement (titre, description, localisation, prix) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, l.getTitre());
            ps.setString(2, l.getDescription());
            ps.setString(3, l.getLocalisation());
            ps.setFloat(4, l.getPrix());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du logement : " + e.getMessage());
        }
    }

    @Override
    public void update(Logement l) {
        String sql = "UPDATE logement SET titre=?, description=?, localisation=?, prix=? WHERE id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, l.getTitre());
            ps.setString(2, l.getDescription());
            ps.setString(3, l.getLocalisation());
            ps.setFloat(4, l.getPrix());
            ps.setInt(5, l.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du logement : " + e.getMessage());
        }
    }

    public Map<String, Integer> getCountByLocalisation() {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT localisation, COUNT(*) as count FROM logement GROUP BY localisation";

        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                map.put(rs.getString("localisation"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des statistiques : " + e.getMessage());
        }

        return map;
    }






    @Override
    public void delete(int id) {
        String sql = "DELETE FROM logement WHERE id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du logement : " + e.getMessage());
        }
    }

    @Override
    public List<Logement> display() {
        List<Logement> list = new ArrayList<>();
        String sql = "SELECT * FROM logement";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Logement l = new Logement(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getString("description"),
                        rs.getString("localisation"),
                        rs.getFloat("prix")
                );
                list.add(l);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des logements : " + e.getMessage());
        }

        return list;
    }
}