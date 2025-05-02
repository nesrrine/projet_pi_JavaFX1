package service;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.util.Properties;

public class EmailService {

    private final String fromEmail = "projetfreelenceer@gmail.com"; // Remplacez par votre adresse Gmail
    private final String password = "ywdy hurt rbsm khon"; // Générez un mot de passe d'application


    public void envoyerEmail(String toEmail, String sujet, String titre, String dateDebut, String dateFin, String statut) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail, "Réservations SpringBoot")); // Nom visible de l'expéditeur
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(sujet);
            msg.setHeader("Content-Type", "text/html; charset=UTF-8");

            String contenuHtml = "<html><body>" +
                    "<h2>Confirmation de Réservation</h2>" +
                    "<p><strong>Titre :</strong> " + titre + "</p>" +
                    "<p><strong>Date Début :</strong> " + dateDebut + "</p>" +
                    "<p><strong>Date Fin :</strong> " + dateFin + "</p>" +
                    "<p><strong>Statut :</strong> " + statut + "</p>" +
                    "<br/><p style='color:gray;font-size:12px;'>Ceci est un email automatique généré par votre application JavaFX.</p>" +
                    "</body></html>";

            msg.setContent(contenuHtml, "text/html; charset=UTF-8");

            Transport.send(msg);
            System.out.println("Email envoyé avec succès !");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'email : " + e.getMessage());
        }
    }
}




