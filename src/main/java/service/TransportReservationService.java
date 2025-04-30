package service;

import models.TransportReservation;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransportReservationService {
    
    private final Connection con;
    
    public TransportReservationService() {
        this.con = MyDatabase.getInstance().getCon();
    }
    
    /**
     * Add a new transport reservation to the database
     */
    public void addReservation(TransportReservation reservation) throws SQLException {
        String sql = "INSERT INTO transport_reservation (transport_id, user_id, reservation_date, nombre_personnes, pdf_path) " +
                "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reservation.getTransportId());
            ps.setInt(2, reservation.getUserId());
            ps.setDate(3, java.sql.Date.valueOf(reservation.getReservationDate()));
            ps.setInt(4, reservation.getNombrePersonnes());
            ps.setString(5, reservation.getPdfPath());
            
            ps.executeUpdate();
            
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    /**
     * Get all reservations for a specific transport
     */
    public List<TransportReservation> getReservationsByTransportId(int transportId) throws SQLException {
        List<TransportReservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM transport_reservation WHERE transport_id = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transportId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        }
        
        return reservations;
    }
    
    /**
     * Get all reservations for a specific user
     */
    public List<TransportReservation> getReservationsByUserId(int userId) throws SQLException {
        List<TransportReservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM transport_reservation WHERE user_id = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    reservations.add(mapResultSetToReservation(rs));
                }
            }
        }
        
        return reservations;
    }
    
    /**
     * Check if a transport is reserved on a specific date
     */
    public boolean isTransportReservedOnDate(int transportId, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transport_reservation WHERE transport_id = ? AND reservation_date = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, transportId);
            ps.setDate(2, java.sql.Date.valueOf(date));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Delete a reservation by ID
     */
    public void deleteReservation(int reservationId) throws SQLException {
        String sql = "DELETE FROM transport_reservation WHERE id = ?";
        
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, reservationId);
            ps.executeUpdate();
        }
    }
    
    /**
     * Map ResultSet to TransportReservation object
     */
    private TransportReservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        return new TransportReservation(
            rs.getInt("id"),
            rs.getInt("transport_id"),
            rs.getInt("user_id"),
            rs.getDate("reservation_date").toLocalDate(),
            rs.getInt("nombre_personnes"),
            rs.getString("pdf_path")
        );
    }
    
    /**
     * Create the transport_reservation table if it doesn't exist
     */
    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS transport_reservation (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "transport_id INT NOT NULL, " +
                "user_id INT NOT NULL, " +
                "reservation_date DATE NOT NULL, " +
                "nombre_personnes INT NOT NULL, " +
                "pdf_path VARCHAR(255), " +
                "FOREIGN KEY (transport_id) REFERENCES transport(id), " +
                "FOREIGN KEY (user_id) REFERENCES users(id)" +
                ")";
        
        try (Statement stmt = con.createStatement()) {
            stmt.execute(sql);
        }
    }
}
