package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import models.Comment;
import utils.SentimentChartBuilder;
import utils.SentimentStats;

import java.util.List;
import java.util.Map;

public class AnalyseSentimentController {

    @FXML
    private VBox sentimentChartContainer;

    /**
     * Analyse les sentiments des commentaires et affiche les statistiques
     * pour chaque Vlog sous forme de graphique.
     *
     * @param commentaires Liste des commentaires à analyser.
     */
    public void analyserEtAfficherParVlog(List<Comment> commentaires) {
        if (commentaires == null || commentaires.isEmpty()) {
            sentimentChartContainer.getChildren().clear();
            Label noDataLabel = new Label("Aucun commentaire disponible pour l'analyse.");
            noDataLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
            sentimentChartContainer.getChildren().add(noDataLabel);
            return;
        }

        Map<Integer, Map<String, Integer>> statsParVlog = SentimentStats.getSentimentCountsParVlog(commentaires);
        sentimentChartContainer.getChildren().clear();

        for (Map.Entry<Integer, Map<String, Integer>> entry : statsParVlog.entrySet()) {
            Integer vlogId = entry.getKey();
            Map<String, Integer> stats = entry.getValue();

            // Création du graphique pour ce vlog
            VBox chartBox = SentimentChartBuilder.buildChart(stats);

            // Création du label titre
            Label label = new Label("Statistiques du Vlog ID : " + vlogId);
            label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

            // Conteneur individuel par vlog
            VBox container = new VBox(10, label, chartBox);
            sentimentChartContainer.getChildren().add(container);
        }
    }
}
//"Franchement, ce vlog était une perte de temps. Aucun intérêt, mauvaise qualité, et le contenu était complètement nul."
//
//Tu peux aussi varier les exemples négatifs pour enrichir tes tests :
//
//"Je n’ai rien appris, c'était ennuyeux du début à la fin."
//
//"La vidéo est trop longue pour rien, et l’auteur ne sait pas de quoi il parle."
//
//"C’est l’un des pires vlogs que j’ai vus."
//
//Souhaites-tu aussi un exemple positif ou ne