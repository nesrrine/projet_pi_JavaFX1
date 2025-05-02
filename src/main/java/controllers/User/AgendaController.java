package controllers.User;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Reservation;
import service.ReservationService;
import javafx.util.Duration;

import java.time.LocalDate;
import java.util.List;

public class AgendaController {

    @FXML private DatePicker dpSelectMonth;  // Le DatePicker pour sélectionner le mois
    @FXML private GridPane calendarGrid;  // Le GridPane pour afficher les jours du mois

    private final ReservationService reservationService = new ReservationService();  // Service pour récupérer les réservations

    // Méthode appelée au démarrage pour initialiser l'affichage
    public void initialize() {
        // Ajouter un écouteur de changement sur le DatePicker pour charger le calendrier du mois sélectionné
        dpSelectMonth.valueProperty().addListener((observable, oldValue, newValue) -> loadCalendar(newValue));

        // Charger le calendrier pour le mois actuel par défaut
        loadCalendar(LocalDate.now());
    }

    // Méthode pour charger le calendrier pour un mois donné
    private void loadCalendar(LocalDate date) {
        // Effacer les anciens boutons de calendrier
        calendarGrid.getChildren().clear();

        // Obtenir le premier et dernier jour du mois sélectionné
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());

        // Obtenir le jour de la semaine du premier jour du mois (0 = Lundi, 6 = Dimanche)
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        // Afficher le mois sélectionné dans le DatePicker
        dpSelectMonth.setValue(date);

        // Afficher les jours du mois dans le GridPane
        int row = 1, col = firstDayOfWeek;  // Commencer à partir du premier jour de la semaine
        for (LocalDate day = firstDayOfMonth; !day.isAfter(lastDayOfMonth); day = day.plusDays(1)) {
            // Créer un bouton pour chaque jour
            Button dayButton = new Button(String.valueOf(day.getDayOfMonth()));
            dayButton.setPrefWidth(50);
            dayButton.setPrefHeight(50);
            dayButton.setStyle("-fx-font-size: 14px; -fx-background-color: #ecf0f1; -fx-border-radius: 5px; -fx-text-fill: #34495e;");

            // Animation de l'apparition du bouton
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), dayButton);
            scaleTransition.setFromX(0.5);
            scaleTransition.setFromY(0.5);
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.play();

            // Ajouter un gestionnaire d'événements pour afficher les réservations pour ce jour
            LocalDate finalDay = day;
            dayButton.setOnAction(event -> showReservationsForDay(finalDay));

            // Ajouter le bouton au GridPane
            calendarGrid.add(dayButton, col, row);

            // Passer au jour suivant (colonne suivante)
            col++;

            // Si on atteint la fin de la semaine (7 jours), on passe à la ligne suivante
            if (col > 6) {
                col = 0;
                row++;
            }
        }
    }

    // Méthode pour afficher les réservations pour un jour spécifique
    private void showReservationsForDay(LocalDate day) {
        // Récupérer les réservations pour ce jour
        List<Reservation> reservations = reservationService.getReservationsForDay(day);
        if (!reservations.isEmpty()) {
            // Créer une fenêtre contextuelle pour afficher les détails des réservations
            StringBuilder reservationDetails = new StringBuilder();
            for (Reservation reservation : reservations) {
                reservationDetails.append("Titre: ").append(reservation.getTitre())
                        .append(", Statut: ").append(reservation.getStatut())
                        .append("\n");
            }

            // Afficher les détails dans une fenêtre contextuelle
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Réservations du " + day);
            alert.setHeaderText(null);
            alert.setContentText(reservationDetails.toString());
            alert.showAndWait();
        } else {
            // Afficher un message si aucune réservation n'est présente pour ce jour
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Aucune réservation");
            alert.setHeaderText(null);
            alert.setContentText("Il n'y a aucune réservation pour cette date.");
            alert.showAndWait();
        }
    }
}
