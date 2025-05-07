package utils;

import models.Comment;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SentimentStats {

    public static Map<Integer, Map<String, Integer>> getSentimentCountsParVlog(List<Comment> comments) {
        Map<Integer, Map<String, Integer>> vlogSentimentStats = new HashMap<>();

        for (Comment comment : comments) {
            int vlogId = comment.getVlogId();

            // Initialisation des compteurs pour ce vlog si pas encore fait
            vlogSentimentStats.putIfAbsent(vlogId, initSentimentMap());

            try {
                String sentiment = SentimentAnalyzer.analyze(comment.getContent().toLowerCase());
                Map<String, Integer> sentimentCounts = vlogSentimentStats.get(vlogId);
                sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            } catch (IOException e) {
                System.out.println("Erreur sentiment pour commentaire ID " + comment.getId() + " : " + e.getMessage());
            }
        }

        return vlogSentimentStats;
    }

    private static Map<String, Integer> initSentimentMap() {
        Map<String, Integer> map = new HashMap<>();
        map.put("POSITIVE", 0);
        map.put("NEGATIVE", 0);
        map.put("NEUTRAL", 0); // si le modèle retourne ce label
        return map;
    }
}
