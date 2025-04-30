package models;

import java.time.LocalDate;

public class TransportReservation {
    private int id;
    private int transportId;
    private int userId;
    private LocalDate reservationDate;
    private int nombrePersonnes;
    private String pdfPath;
    
    public TransportReservation() {
    }
    
    public TransportReservation(int transportId, int userId, LocalDate reservationDate, int nombrePersonnes, String pdfPath) {
        this.transportId = transportId;
        this.userId = userId;
        this.reservationDate = reservationDate;
        this.nombrePersonnes = nombrePersonnes;
        this.pdfPath = pdfPath;
    }
    
    public TransportReservation(int id, int transportId, int userId, LocalDate reservationDate, int nombrePersonnes, String pdfPath) {
        this.id = id;
        this.transportId = transportId;
        this.userId = userId;
        this.reservationDate = reservationDate;
        this.nombrePersonnes = nombrePersonnes;
        this.pdfPath = pdfPath;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getTransportId() {
        return transportId;
    }
    
    public void setTransportId(int transportId) {
        this.transportId = transportId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public LocalDate getReservationDate() {
        return reservationDate;
    }
    
    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }
    
    public int getNombrePersonnes() {
        return nombrePersonnes;
    }
    
    public void setNombrePersonnes(int nombrePersonnes) {
        this.nombrePersonnes = nombrePersonnes;
    }
    
    public String getPdfPath() {
        return pdfPath;
    }
    
    public void setPdfPath(String pdfPath) {
        this.pdfPath = pdfPath;
    }
    
    @Override
    public String toString() {
        return "TransportReservation{" +
                "id=" + id +
                ", transportId=" + transportId +
                ", userId=" + userId +
                ", reservationDate=" + reservationDate +
                ", nombrePersonnes=" + nombrePersonnes +
                ", pdfPath='" + pdfPath + '\'' +
                '}';
    }
}
