package service;

import models.Comment;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CommentService {

    private final Connection connection;

    public CommentService() {
        this.connection = MyDatabase.instance.getCon();
    }

    // Ajouter un commentaire
    public void addComment(Comment comment) {
        String query = "INSERT INTO comments (vlog_id, user_id, content, timestamp, is_reported) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, comment.getVlogId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getContent());
            stmt.setString(4, comment.getTimestamp());
            stmt.setBoolean(5, comment.isReported());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout du commentaire : " + e.getMessage());
        }
    }

    // Mettre à jour un commentaire
    public void updateComment(Comment comment) {
        String query = "UPDATE comments SET content = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, comment.getContent());
            stmt.setInt(2, comment.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du commentaire : " + e.getMessage());
        }
    }

    // Supprimer un commentaire et ses réponses
    public void deleteComment(int commentId) {
        String deleteRepliesQuery = "DELETE FROM comment_replies WHERE parent_comment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteRepliesQuery)) {
            stmt.setInt(1, commentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des réponses : " + e.getMessage());
        }

        String deleteCommentQuery = "DELETE FROM comments WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteCommentQuery)) {
            stmt.setInt(1, commentId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression du commentaire : " + e.getMessage());
        }
    }

    // Obtenir les commentaires pour un vlog donné
    public List<Comment> getCommentsByVlog(int vlogId) {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM comments WHERE vlog_id = ? ORDER BY timestamp DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, vlogId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment(rs.getInt("vlog_id"), rs.getInt("user_id"), rs.getString("content"));
                comment.setId(rs.getInt("id"));
                comment.setTimestamp(rs.getString("timestamp"));
                comment.setReported(rs.getBoolean("is_reported"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des commentaires : " + e.getMessage());
        }
        return comments;
    }
    // Supprimer une réponse à un commentaire
    public void deleteReply(int replyId) {
        String deleteReplyQuery = "DELETE FROM comment_replies WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteReplyQuery)) {
            stmt.setInt(1, replyId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réponse : " + e.getMessage());
        }
    }

    // Ajouter une réponse à un commentaire
    public void addReply(int parentCommentId, Comment reply) {
        String query = "INSERT INTO comment_replies (parent_comment_id, user_id, content, timestamp) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, parentCommentId);
            stmt.setInt(2, reply.getUserId());
            stmt.setString(3, reply.getContent());
            stmt.setString(4, reply.getTimestamp());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'une réponse : " + e.getMessage());
        }
    }

    // Obtenir les réponses à un commentaire
    public List<Comment> getReplies(int parentCommentId) {
        List<Comment> replies = new ArrayList<>();
        String query = "SELECT * FROM comment_replies WHERE parent_comment_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, parentCommentId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Comment reply = new Comment(rs.getInt("parent_comment_id"), rs.getInt("user_id"), rs.getString("content"));
                reply.setId(rs.getInt("id"));
                reply.setTimestamp(rs.getString("timestamp"));
                replies.add(reply);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réponses : " + e.getMessage());
        }
        return replies;
    }

    // Vérifie si un utilisateur a déjà signalé un commentaire
    public boolean hasAlreadyReported(int userId, int commentId) {
        String sql = "SELECT 1 FROM comment_reports WHERE user_id = ? AND comment_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, commentId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de signalement : " + e.getMessage());
            return false;
        }
    }

    // Signaler un commentaire (si non déjà signalé)
    public void reportComment(int userId, int commentId) {
        if (hasAlreadyReported(userId, commentId)) {
            System.out.println("Ce commentaire a déjà été signalé par cet utilisateur.");
            return;
        }

        String sql = "INSERT INTO comment_reports (user_id, comment_id) VALUES (?, ?)";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, commentId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors du signalement du commentaire : " + e.getMessage());
        }
    }
    // Ajouter une réponse à un commentaire

    public List<String> getAllContents() {
        return getAllComments().stream()
                .map(Comment::getContent)
                .collect(Collectors.toList());
    }

    public List<Comment> getAllComments() {
        List<Comment> comments = new ArrayList<>();
        String query = "SELECT * FROM comments ORDER BY timestamp DESC";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Comment comment = new Comment(rs.getInt("vlog_id"), rs.getInt("user_id"), rs.getString("content"));
                comment.setId(rs.getInt("id"));
                comment.setTimestamp(rs.getString("timestamp"));
                comment.setReported(rs.getBoolean("is_reported"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de tous les commentaires : " + e.getMessage());
        }
        return comments;
    }



}
