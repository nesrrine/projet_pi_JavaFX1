package service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import models.Transport;
import models.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PDFService {
    
    /**
     * Generates a PDF for a transport reservation
     * 
     * @param transport The transport being reserved
     * @param user The user making the reservation
     * @param date The reservation date
     * @param nombrePersonnes Number of people for the reservation
     * @return The path to the generated PDF file
     * @throws DocumentException If there's an error creating the PDF
     * @throws IOException If there's an error writing the file
     */
    public String generateTransportReservationPDF(Transport transport, User user, LocalDate date, int nombrePersonnes) 
            throws DocumentException, IOException {
        // Create directory if it doesn't exist
        String dirPath = System.getProperty("user.home") + File.separator + "reservations";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Create a unique filename
        String fileName = "reservation_transport_" + user.getId() + "_" + System.currentTimeMillis() + ".pdf";
        String filePath = dirPath + File.separator + fileName;
        
        // Create PDF document
        Document document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();
        
        // Add header
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, BaseColor.BLUE);
        Paragraph title = new Paragraph("Confirmation de Réservation de Transport", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(Chunk.NEWLINE);
        
        // Add logo if available
        try {
            Image logo = Image.getInstance(getClass().getResource("/images/logo.png"));
            logo.setAlignment(Element.ALIGN_CENTER);
            logo.scaleToFit(200, 100);
            document.add(logo);
        } catch (Exception e) {
            // Logo not found, continue without it
            System.out.println("Logo not found: " + e.getMessage());
        }
        
        document.add(Chunk.NEWLINE);
        
        // Add reservation details
        Font normalFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
        Font boldFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
        
        // Format date
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        
        // Add customer information
        document.add(new Paragraph("Informations du Client:", boldFont));
        document.add(new Paragraph("Nom: " + user.getFirstName() + " " + user.getLastName(), normalFont));
        document.add(new Paragraph("Email: " + user.getEmail(), normalFont));
        document.add(new Paragraph("Téléphone: " + user.getPhone(), normalFont));
        document.add(Chunk.NEWLINE);
        
        // Add reservation details
        document.add(new Paragraph("Détails de la Réservation:", boldFont));
        document.add(new Paragraph("Type de Transport: " + transport.getType(), normalFont));
        document.add(new Paragraph("Description: " + transport.getDescription(), normalFont));
        document.add(new Paragraph("Date de Réservation: " + formattedDate, normalFont));
        document.add(new Paragraph("Nombre de Personnes: " + nombrePersonnes, normalFont));
        document.add(new Paragraph("Prix: " + String.format("%.2f TND", transport.getPrix()), normalFont));
        document.add(Chunk.NEWLINE);
        
        // Add transport image if available
        try {
            if (transport.getImage() != null && !transport.getImage().isEmpty()) {
                File imageFile = new File(transport.getImage());
                if (imageFile.exists()) {
                    Image transportImg = Image.getInstance(transport.getImage());
                    transportImg.setAlignment(Element.ALIGN_CENTER);
                    transportImg.scaleToFit(300, 200);
                    document.add(transportImg);
                }
            }
        } catch (Exception e) {
            // Image not found, continue without it
            System.out.println("Transport image not found: " + e.getMessage());
        }
        
        document.add(Chunk.NEWLINE);
        
        // Add terms and conditions
        document.add(new Paragraph("Termes et Conditions:", boldFont));
        document.add(new Paragraph("1. Veuillez présenter ce document lors de votre arrivée.", normalFont));
        document.add(new Paragraph("2. Annulation gratuite jusqu'à 24 heures avant la date de réservation.", normalFont));
        document.add(new Paragraph("3. En cas de retard, veuillez nous contacter au plus tôt.", normalFont));
        document.add(Chunk.NEWLINE);
        
        // Add footer with QR code
        PdfContentByte cb = writer.getDirectContent();
        BarcodeQRCode qrCode = new BarcodeQRCode(
            "ID: " + user.getId() + 
            ", Transport: " + transport.getType() + 
            ", Date: " + formattedDate, 
            100, 100, null
        );
        Image qrCodeImage = qrCode.getImage();
        qrCodeImage.setAbsolutePosition(450, 30);
        cb.addImage(qrCodeImage);
        
        // Add thank you message
        Font thankYouFont = new Font(Font.FontFamily.HELVETICA, 14, Font.ITALIC, BaseColor.BLUE);
        Paragraph thankYou = new Paragraph("Merci pour votre réservation!", thankYouFont);
        thankYou.setAlignment(Element.ALIGN_CENTER);
        document.add(thankYou);
        
        // Close document
        document.close();
        
        return filePath;
    }
}
