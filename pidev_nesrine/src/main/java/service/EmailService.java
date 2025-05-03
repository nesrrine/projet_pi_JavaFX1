package service;


import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

public class EmailService {
    private final String username = "medrayen.bendhia@gmail.com";
    private final String password = "arnb mowa pxnq ojap";

    /**
     * Envoie un email de bienvenue à un nouvel utilisateur
     * @param recipientEmail L'adresse email du destinataire
     * @param firstName Le prénom de l'utilisateur
     * @param lastName Le nom de l'utilisateur
     * @return true si l'email a été envoyé avec succès, false sinon
     */
    public boolean sendWelcomeEmail(String recipientEmail, String firstName, String lastName) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));

            // Sujet de l'email
            message.setSubject("Bienvenue sur Home_Swap !");

            // Date actuelle formatée
            String currentDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Corps de l'email en HTML pour un meilleur formatage
            String htmlContent =
                "<html>" +
                "<head>" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }" +
                "    .content { padding: 20px; background-color: #f9f9f9; }" +
                "    .footer { font-size: 12px; text-align: center; margin-top: 20px; color: #777; }" +
                "    .button { background-color: #4CAF50; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h1>Bienvenue sur Home_Swap !</h1>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <p>Bonjour <strong>" + firstName + " " + lastName + "</strong>,</p>" +
                "      <p>Nous sommes ravis de vous accueillir sur Home_Swap, votre nouvelle plateforme d'échange de maisons !</p>" +
                "      <p>Votre compte a été créé avec succès le " + currentDate + ".</p>" +
                "      <p>Avec Home_Swap, vous pouvez :</p>" +
                "      <ul>" +
                "        <li>Publier votre logement pour des échanges</li>" +
                "        <li>Découvrir des logements disponibles partout dans le monde</li>" +
                "        <li>Communiquer avec d'autres membres</li>" +
                "        <li>Organiser vos échanges en toute sécurité</li>" +
                "      </ul>" +
                "      <p>Nous espérons que vous apprécierez votre expérience sur Home_Swap.</p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>" +
                "      <p>&copy; 2025 Home_Swap. Tous droits réservés.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";

            // Définir le contenu HTML
            message.setContent(htmlContent, "text/html; charset=utf-8");

            // Envoyer le message
            Transport.send(message);

            System.out.println("Email de bienvenue envoyé avec succès à " + recipientEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de bienvenue: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Envoie un email de réinitialisation de mot de passe
     * @param recipientEmail L'adresse email du destinataire
     * @param resetToken Le token de réinitialisation
     * @return true si l'email a été envoyé avec succès, false sinon
     */
    public boolean sendPasswordResetEmail(String recipientEmail, String resetToken) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Réinitialisation de votre mot de passe Home_Swap");

            String htmlContent =
                "<html>" +
                "<head>" +
                "  <style>" +
                "    body { font-family: Arial, sans-serif; line-height: 1.6; }" +
                "    .container { max-width: 600px; margin: 0 auto; padding: 20px; }" +
                "    .header { background-color: #4CAF50; color: white; padding: 10px; text-align: center; }" +
                "    .content { padding: 20px; background-color: #f9f9f9; }" +
                "    .token { font-size: 24px; font-weight: bold; text-align: center; margin: 20px 0; letter-spacing: 2px; }" +
                "    .footer { font-size: 12px; text-align: center; margin-top: 20px; color: #777; }" +
                "  </style>" +
                "</head>" +
                "<body>" +
                "  <div class='container'>" +
                "    <div class='header'>" +
                "      <h1>Réinitialisation de mot de passe</h1>" +
                "    </div>" +
                "    <div class='content'>" +
                "      <p>Vous avez demandé la réinitialisation de votre mot de passe sur Home_Swap.</p>" +
                "      <p>Voici votre code de réinitialisation :</p>" +
                "      <div class='token'>" + resetToken + "</div>" +
                "      <p>Ce code est valable pendant 24 heures. Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.</p>" +
                "    </div>" +
                "    <div class='footer'>" +
                "      <p>Cet email a été envoyé automatiquement, merci de ne pas y répondre.</p>" +
                "      <p>&copy; 2023 Home_Swap. Tous droits réservés.</p>" +
                "    </div>" +
                "  </div>" +
                "</body>" +
                "</html>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            Transport.send(message);

            System.out.println("Email de réinitialisation envoyé avec succès à " + recipientEmail);
            return true;

        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi de l'email de réinitialisation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
