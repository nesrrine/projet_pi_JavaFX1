package service;

import models.Vlog;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
        String sql = "DELETE FROM vlog WHERE id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting vlog: " + e.getMessage());
        }
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
                        rs.getTimestamp(
                                "created_at").toLocalDateTime(),
                        rs.getInt("author_id")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vlogs;
    }

}
