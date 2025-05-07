package service;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import models.Notification;
import utils.MyDatabase;
import utils.Session;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final Connection con;

    public NotificationService() {
        this.con = MyDatabase.getInstance().getCon();
    }

    public void sendNotification(int receiverId, String message) {
        String sql = "INSERT INTO notification (receiver_id, message, created_at, is_read) VALUES (?, ?, ?, false)";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.setString(2, message);
            stmt.setObject(3, LocalDateTime.now());
            stmt.executeUpdate();

            // Si c'est le même utilisateur connecté → Afficher pop-up
            if (receiverId == Session.getCurrentUser().getId()) {
                showPopupNotification(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showPopupNotification(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Nouvelle Notification");
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.show();
        });
    }

    public List<Notification> getUnreadNotifications(int receiverId) {
        List<Notification> notifications = new ArrayList<>();
        String sql = "SELECT * FROM notification WHERE receiver_id = ? AND is_read = false ORDER BY created_at DESC";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Notification notif = new Notification(
                        rs.getInt("id"),
                        rs.getInt("receiver_id"),
                        rs.getString("message"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getBoolean("is_read")
                );
                notifications.add(notif);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public void markAllAsRead(int receiverId) {
        String sql = "UPDATE notification SET is_read = true WHERE receiver_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setInt(1, receiverId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
