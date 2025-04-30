package service;

import models.Transport;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TransportService {

    private final Connection con;

    public TransportService() {
        this.con = MyDatabase.getInstance().getCon();
    }

    // Méthode helper pour mapper ResultSet à Transport
    private Transport mapResultSetToTransport(ResultSet rs) throws SQLException {
        Transport transport = new Transport();
        transport.setId(rs.getInt("id"));
        transport.setType(rs.getString("type"));
        transport.setDescription(rs.getString("description"));
        transport.setPrix(rs.getDouble("prix"));
        transport.setImage(rs.getString("image"));
        transport.setUserId(rs.getInt("user_id"));
        
        // Set default disponibilite to true if column doesn't exist
        try {
            transport.setDisponibilite(rs.getBoolean("disponibilite"));
        } catch (SQLException e) {
            transport.setDisponibilite(true);
        }
        
        return transport;
    }

    // CREATE - Ajouter un transport
    public void addTransport(Transport transport) throws SQLException {
        if (!verifyUserExists(transport.getUserId())) {
            throw new SQLException("User does not exist");
        }

        String sql = "INSERT INTO transport (type, description, prix, image, user_id) VALUES (?, ?, ?, ?, ?)";

        try {
            try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, transport.getType());
                ps.setString(2, transport.getDescription());
                ps.setDouble(3, transport.getPrix());
                ps.setString(4, transport.getImage());
                ps.setInt(5, transport.getUserId());

                System.out.println("Adding transport with user_id: " + transport.getUserId()); // Debug log

                ps.executeUpdate();

                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transport.setId(generatedKeys.getInt(1));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding transport: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // READ - Récupérer tous les transports
    public List<Transport> getAllTransports() throws SQLException {
        List<Transport> transports = new ArrayList<>();
        String sql = "SELECT * FROM transport";

        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                transports.add(mapResultSetToTransport(rs));
            }
        }
        return transports;
    }

    // READ - Récupérer un transport par ID
    public Transport getTransportById(int id) throws SQLException {
        String sql = "SELECT * FROM transport WHERE id = ?";
        Transport transport = null;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    transport = mapResultSetToTransport(rs);
                }
            }
        }

        return transport;
    }

    // UPDATE - Mettre à jour un transport
    public void updateTransport(Transport transport) throws SQLException {
        if (!verifyUserExists(transport.getUserId())) {
            throw new SQLException("User does not exist");
        }

        String sql = "UPDATE transport SET type = ?, description = ?, prix = ?, image = ?, user_id = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, transport.getType());
            ps.setString(2, transport.getDescription());
            ps.setDouble(3, transport.getPrix());
            ps.setString(4, transport.getImage());
            ps.setInt(5, transport.getUserId());
            ps.setInt(6, transport.getId());

            ps.executeUpdate();
        }
    }

    // DELETE - Supprimer un transport
    public void deleteTransport(int id) throws SQLException {
        String sql = "DELETE FROM transport WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
    
    // Get transports by user ID
    public List<Transport> getTransportsByUserId(int userId) throws SQLException {
        if (!verifyUserExists(userId)) {
            throw new SQLException("User does not exist");
        }

        List<Transport> transports = new ArrayList<>();
        String sql = "SELECT * FROM transport WHERE user_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transports.add(mapResultSetToTransport(rs));
                }
            }
        }
        return transports;
    }

    // READ - Récupérer les transports par type
    public List<Transport> getTransportsByType(String type) throws SQLException {
        List<Transport> transports = new ArrayList<>();
        String sql = "SELECT * FROM transport WHERE type = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, type);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    transports.add(mapResultSetToTransport(rs));
                }
            }
        }
        
        return transports;
    }

    // Get unique transport types
    public List<String> getAllTransportTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        String sql = "SELECT DISTINCT type FROM transport ORDER BY type";
        
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                types.add(rs.getString("type"));
            }
        }
        
        return types;
    }

    // Verify if user exists
    public boolean verifyUserExists(int userId) throws SQLException {
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