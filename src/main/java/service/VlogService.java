package service;

import models.Vlog;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class VlogService implements IService<Vlog> {

    private final Connection con;

    public VlogService() {
        this.con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Vlog vlog) {
        String sql = "INSERT INTO vlog (content, image, video, author_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vlog.getContent());
            ps.setString(2, vlog.getImage());
            ps.setString(3, vlog.getVideo());
            ps.setInt(4, vlog.getAuthorId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error adding vlog: " + e.getMessage());
        }
    }

    @Override
    public void update(Vlog vlog) {
        String sql = "UPDATE vlog SET content=?, image=?, video=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, vlog.getContent());
            ps.setString(2, vlog.getImage());
            ps.setString(3, vlog.getVideo());
            ps.setInt(4, vlog.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating vlog: " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        deleteCommentsByVlogId(id);
        String sql = "DELETE FROM vlog WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting vlog: " + e.getMessage());
        }
    }

    private void deleteCommentsByVlogId(int vlogId) {
        String sql = "DELETE FROM comments WHERE vlog_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vlogId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting comments for vlog: " + e.getMessage());
        }
    }

    public Vlog getVlogById(int id) {
        String sql = "SELECT * FROM vlog WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Vlog(
                        rs.getInt("id"),
                        rs.getString("content"),
                        rs.getString("image"),
                        rs.getString("video"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("author_id")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching vlog by ID: " + e.getMessage());
        }
        return null;
    }
    public int getNombreSignalements(int vlogId) {
        String sql = "SELECT COUNT(*) FROM vlog_reports WHERE vlog_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vlogId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des signalements : " + e.getMessage());
        }
        return 0;
    }


    @Override
    public List<Vlog> display() {
        List<Vlog> list = new ArrayList<>();
        String sql = "SELECT * FROM vlog";
        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Vlog vlog = new Vlog(
                        rs.getInt("id"),
                        rs.getString("content"),
                        rs.getString("image"),
                        rs.getString("video"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("author_id")
                );
                list.add(vlog);
            }
        } catch (SQLException e) {
            System.err.println("Error fetching vlogs: " + e.getMessage());
        }
        return list;
    }

    public List<Vlog> getByAuthor(int authorId) {
        List<Vlog> vlogs = new ArrayList<>();
        String sql = "SELECT * FROM vlog WHERE author_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vlogs.add(new Vlog(
                        rs.getInt("id"),
                        rs.getString("content"),
                        rs.getString("image"),
                        rs.getString("video"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getInt("author_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vlogs;
    }

    public boolean hasAlreadyReported(int userId, int vlogId) {
        String sql = "SELECT COUNT(*) FROM vlog_reports WHERE user_id = ? AND vlog_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, vlogId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }



    public void reportVlog(int userId, int vlogId) {
        String sql = "INSERT INTO vlog_reports (user_id, vlog_id) VALUES (?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, vlogId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteVlog(int vlogId) {
        String sql = "DELETE FROM vlog WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vlogId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public List<Vlog> getReportedVlogs() {
        List<Vlog> all = display(); // Ou requête spécifique SQL
        return all.stream()
                .filter(Vlog::isReported)
                .collect(Collectors.toList());
    }
    public void approveVlogReport(int vlogId) {
        delete(vlogId); // Supprimer le vlog signalé
    }

    public void rejectVlogReport(int vlogId) {
        Vlog vlog = getVlogById(vlogId);
        vlog.setReported(false);
        update(vlog); // Remettre "non signalé"
    }


}
