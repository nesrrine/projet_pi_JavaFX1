package service;

import models.Like;
import models.User;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LikeService {

    private final Connection con = MyDatabase.getInstance().getCon();
    private final UserService userService = new UserService();  // Create an instance of UserService

    public void likeOrDislike(Like like) {
        String sql = "REPLACE INTO likes (vlog_id, user_id, is_like) VALUES (?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, like.getVlogId());
            ps.setInt(2, like.getUserId());
            ps.setBoolean(3, like.isLike());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeLike(int vlogId, int userId) {
        String sql = "DELETE FROM likes WHERE vlog_id = ? AND user_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vlogId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean hasLiked(int vlogId, int userId, boolean isLike) {
        String sql = "SELECT 1 FROM likes WHERE vlog_id = ? AND user_id = ? AND is_like = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vlogId);
            ps.setInt(2, userId);
            ps.setBoolean(3, isLike);
            ResultSet rs = ps.executeQuery();
            return rs.next();  // if a row exists, then the user has liked/disliked
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countLikes(int vlogId, boolean isLike) {
        String sql = "SELECT COUNT(*) FROM likes WHERE vlog_id=? AND is_like=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vlogId);
            ps.setBoolean(2, isLike);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<User> getUsersWhoLiked(int vlogId) {
        String sql = "SELECT * FROM likes WHERE vlog_id=? AND is_like=?";
        List<Like> likes = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vlogId);
            ps.setBoolean(2, true);  // true for likes
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Like like = new Like();
                like.setId(rs.getInt("id"));
                like.setVlogId(rs.getInt("vlog_id"));
                like.setUserId(rs.getInt("user_id"));
                like.setIsLike(rs.getBoolean("is_like"));
                likes.add(like);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return likes.stream()
                .map(like -> userService.getById(like.getUserId()))
                .collect(Collectors.toList());
    }

    public List<User> getUsersWhoDisliked(int vlogId) {
        String sql = "SELECT * FROM likes WHERE vlog_id=? AND is_like=?";
        List<Like> dislikes = new ArrayList<>();

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vlogId);
            ps.setBoolean(2, false);  // false for dislikes
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Like dislike = new Like();
                dislike.setId(rs.getInt("id"));
                dislike.setVlogId(rs.getInt("vlog_id"));
                dislike.setUserId(rs.getInt("user_id"));
                dislike.setIsLike(rs.getBoolean("is_like"));
                dislikes.add(dislike);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dislikes.stream()
                .map(dislike -> userService.getById(dislike.getUserId()))
                .collect(Collectors.toList());
    }
}
