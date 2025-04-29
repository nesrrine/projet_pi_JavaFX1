package service;

import models.Restaurant;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RestaurantService {

    private final Connection con;

    public RestaurantService() {
        this.con = MyDatabase.getInstance().getCon();
    }

    // Méthode helper pour mapper ResultSet à Restaurant
    private Restaurant mapResultSetToRestaurant(ResultSet rs) throws SQLException {
        Restaurant restaurant = new Restaurant();
        restaurant.setId(rs.getInt("id"));
        restaurant.setNom(rs.getString("nom"));
        restaurant.setLocalisation(rs.getString("localisation"));
        restaurant.setImage(rs.getString("image"));
        restaurant.setDescription(rs.getString("description"));
        restaurant.setPrix(rs.getDouble("prix"));
        restaurant.setLat(rs.getDouble("lat"));
        restaurant.setLng(rs.getDouble("lng"));
        restaurant.setImage1(rs.getString("image1"));
        restaurant.setImage2(rs.getString("image2"));
        restaurant.setUserId(rs.getInt("user_id"));
        
        // Handle promotion field (with default false if column doesn't exist)
        try {
            restaurant.setPromotion(rs.getBoolean("promotion"));
        } catch (SQLException e) {
            restaurant.setPromotion(false);
        }
        
        return restaurant;
    }

    // CREATE - Ajouter un restaurant
    public void addRestaurant(Restaurant restaurant) throws SQLException {
        if (!verifyUserExists(restaurant.getUserId())) {
            throw new SQLException("User does not exist");
        }

        String sql = "INSERT INTO restaurant (nom, localisation, image, description, prix, lat, lng, image1, image2, user_id, promotion) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, restaurant.getNom());
            ps.setString(2, restaurant.getLocalisation());
            ps.setString(3, restaurant.getImage());
            ps.setString(4, restaurant.getDescription());
            ps.setDouble(5, restaurant.getPrix());
            ps.setDouble(6, restaurant.getLat());
            ps.setDouble(7, restaurant.getLng());
            ps.setString(8, restaurant.getImage1());
            ps.setString(9, restaurant.getImage2());
            ps.setInt(10, restaurant.getUserId());
            ps.setBoolean(11, restaurant.isPromotion());

            System.out.println("Adding restaurant with user_id: " + restaurant.getUserId()); // Debug log

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    restaurant.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding restaurant: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // READ - Récupérer tous les restaurants
    public List<Restaurant> getAllRestaurants() throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM restaurant";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                restaurants.add(mapResultSetToRestaurant(rs));
            }
        }

        return restaurants;
    }

    // READ - Récupérer un restaurant par ID
    public Restaurant getRestaurantById(int id) throws SQLException {
        String sql = "SELECT * FROM restaurant WHERE id = ?";
        Restaurant restaurant = null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    restaurant = mapResultSetToRestaurant(rs);
                }
            }
        }

        return restaurant;
    }

    // UPDATE - Mettre à jour un restaurant
    public void updateRestaurant(Restaurant restaurant) throws SQLException {
        if (!verifyUserExists(restaurant.getUserId())) {
            throw new SQLException("User does not exist");
        }

        String sql = "UPDATE restaurant SET nom = ?, localisation = ?, image = ?, description = ?, " +
                "prix = ?, lat = ?, lng = ?, image1 = ?, image2 = ?, user_id = ?, promotion = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, restaurant.getNom());
            ps.setString(2, restaurant.getLocalisation());
            ps.setString(3, restaurant.getImage());
            ps.setString(4, restaurant.getDescription());
            ps.setDouble(5, restaurant.getPrix());
            ps.setDouble(6, restaurant.getLat());
            ps.setDouble(7, restaurant.getLng());
            ps.setString(8, restaurant.getImage1());
            ps.setString(9, restaurant.getImage2());
            ps.setInt(10, restaurant.getUserId());
            ps.setBoolean(11, restaurant.isPromotion());
            ps.setInt(12, restaurant.getId());

            ps.executeUpdate();
        }
    }

    // DELETE - Supprimer un restaurant
    public void deleteRestaurant(int id) throws SQLException {
        String sql = "DELETE FROM restaurant WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // Get restaurants by user ID
    public List<Restaurant> getRestaurantsByUserId(int userId) throws SQLException {
        if (!verifyUserExists(userId)) {
            throw new SQLException("User does not exist");
        }

        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM restaurant WHERE user_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    restaurants.add(mapResultSetToRestaurant(rs));
                }
            }
        }
        return restaurants;
    }

    // Get restaurants with promotion
    public List<Restaurant> getRestaurantsWithPromotion() throws SQLException {
        List<Restaurant> restaurants = new ArrayList<>();
        String sql = "SELECT * FROM restaurant WHERE promotion = TRUE";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                restaurants.add(mapResultSetToRestaurant(rs));
            }
        }

        return restaurants;
    }
    
    // Toggle promotion status
    public void togglePromotion(int restaurantId, boolean promotionStatus) throws SQLException {
        String sql = "UPDATE restaurant SET promotion = ? WHERE id = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBoolean(1, promotionStatus);
            ps.setInt(2, restaurantId);
            ps.executeUpdate();
        }
    }
    
    // Get highest restaurant price
    public double getHighestRestaurantPrice() throws SQLException {
        String sql = "SELECT MAX(prix) FROM restaurant";
        
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        
        return 100.0; // Default value if no restaurants exist
    }

    // Verify if user exists
    private boolean verifyUserExists(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("User exists check for ID " + userId + ": " + (count > 0)); // Debug log
                    return count > 0;
                }
            }
        }
        return false;
    }
}