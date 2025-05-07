package service;

import models.User;
import utils.MyDatabase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserService {

    private final Connection con;

    public UserService() {
        this.con = MyDatabase.getInstance().getCon();
    }

    public void signup(User user) {
        String sql = "INSERT INTO users (first_name, last_name, email, password, address, phone, birth_date, role) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, hashedPassword);
            ps.setString(5, user.getAddress());
            ps.setString(6, user.getPhone());
            ps.setDate(7, Date.valueOf(user.getBirthDate()));
            ps.setString(8, user.getRole());
            ps.executeUpdate();
            System.out.println("User signed up successfully"); // Debug log
        } catch (SQLException e) {
            System.err.println("Error signing up user: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
    }

    public boolean login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String hashedPassword = rs.getString("password");
                return BCrypt.checkpw(password, hashedPassword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public User getByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("role")
                );
                System.out.println("Found user by email: " + email + ", ID: " + user.getId()); // Debug log
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by email: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
        System.out.println("No user found for email: " + email); // Debug log
        return null;
    }

    public void update(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, email = ?, address = ?, phone = ?, birth_date = ?, role = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getAddress());
            ps.setString(5, user.getPhone());
            ps.setDate(6, Date.valueOf(user.getBirthDate()));
            ps.setString(7, user.getRole());
            ps.setInt(8, user.getId());
            ps.executeUpdate();
            System.out.println("Updated user with ID: " + user.getId()); // Debug log
        } catch (SQLException e) {
            System.err.println("Error updating user: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Deleted user with ID: " + id); // Debug log
        } catch (SQLException e) {
            System.err.println("Error deleting user: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
    }

    public List<User> display() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    "********", // Hide password
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("role")
                );
                users.add(user);
            }
            System.out.println("Retrieved " + users.size() + " users"); // Debug log
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }

        return users;
    }

    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                User user = new User(
                    rs.getInt("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("email"),
                    rs.getString("password"),
                    rs.getString("address"),
                    rs.getString("phone"),
                    rs.getDate("birth_date").toLocalDate(),
                    rs.getString("role")
                );
                System.out.println("Found user by ID: " + id); // Debug log
                return user;
            }
        } catch (SQLException e) {
            System.err.println("Error getting user by ID: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
        System.out.println("No user found for ID: " + id); // Debug log
        return null;
    }
    private int countReclamationsRecues(int userId) {
        String sql = "SELECT COUNT(*) FROM reclamation WHERE cible_id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du comptage des réclamations reçues : " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("email"),
                        "********", // Hide password
                        rs.getString("address"),
                        rs.getString("phone"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getString("role")
                );
                users.add(user);
            }
            System.out.println("Retrieved " + users.size() + " users"); // Debug log
        } catch (SQLException e) {
            System.err.println("Error fetching users: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }

        return users;
    }
    private List<String> getUsersList() {
        List<String> users = new ArrayList<>();
        try {
            // Appel de la méthode getAllUsers pour obtenir tous les utilisateurs
            List<User> userList = new UserService().getAllUsers();
            for (User user : userList) {
                users.add(user.getEmail());  // Ajout de l'email de chaque utilisateur dans la liste
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des utilisateurs : " + e.getMessage());
        }
        return users;
    }



}
