package service;

import models.Reservation;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IService<Reservation> {

    private final Connection con;

    public ReservationService() {
        this.con = MyDatabase.getInstance().getCon();
    }

    @Override
    public void add(Reservation r) {
        String sql = "INSERT INTO reservation (titre, datedebut, datefin, statut) VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getTitre());
            ps.setDate(2, Date.valueOf(r.getDateDebut()));
            ps.setDate(3, Date.valueOf(r.getDateFin()));
            ps.setString(4, r.getStatut());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réservation : " + e.getMessage());
        }
    }

    @Override
    public void update(Reservation r) {
        String sql = "UPDATE reservation SET titre=?, datedebut=?, datefin=?, statut=? WHERE id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, r.getTitre());
            ps.setDate(2, Date.valueOf(r.getDateDebut()));
            ps.setDate(3, Date.valueOf(r.getDateFin()));
            ps.setString(4, r.getStatut());
            ps.setInt(5, r.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour de la réservation : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM reservation WHERE id=?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réservation : " + e.getMessage());
        }
    }

    @Override
    public List<Reservation> display() {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation";

        try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Reservation r = new Reservation(
                        rs.getInt("id"),
                        rs.getString("titre"),
                        rs.getDate("datedebut").toLocalDate(),
                        rs.getDate("datefin").toLocalDate(),
                        rs.getString("statut")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des réservations : " + e.getMessage());
        }

        return list;
    }

    public List<Reservation> getReservationsForDay(LocalDate date) {
        List<Reservation> reservationsForDay = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE (datedebut <= ? AND datefin >= ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(date));
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Reservation r = new Reservation(
                            rs.getInt("id"),
                            rs.getString("titre"),
                            rs.getDate("datedebut").toLocalDate(),
                            rs.getDate("datefin").toLocalDate(),
                            rs.getString("statut")
                    );
                    reservationsForDay.add(r);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réservations pour le jour : " + e.getMessage());
        }
        return reservationsForDay;
    }
}
