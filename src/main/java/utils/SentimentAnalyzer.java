package utils;

import okhttp3.*;
import org.json.JSONObject;
import org.json.JSONException;

import java.io.IOException;

public class SentimentAnalyzer {

    private static final String API_URL = "https://api.nlpcloud.io/v1/distilbert-base-uncased-finetuned-sst-2-english/sentiment";
    private static final String API_KEY = "d225dece7ec8613889309efb932f22a3e4d85aa9"; // 🔐 Mets ta clé ici
    public static String analyze(String text) throws IOException {
        OkHttpClient client = new OkHttpClient();

        JSONObject json = new JSONObject();
        json.put("text", text);

        Request request = new Request.Builder()
                .url(API_URL)
                .post(RequestBody.create(json.toString(), MediaType.parse("application/json")))
                .addHeader("Authorization", "Token " + API_KEY)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Erreur API : " + response);
            }

            String responseBody = response.body().string();
            System.out.println("Réponse brute de l'API : " + responseBody);

            try {
                JSONObject result = new JSONObject(responseBody);
                if (result.has("scored_labels")) {
                    // Extraire le premier élément du tableau scored_labels
                    JSONObject labelObj = result.getJSONArray("scored_labels").getJSONObject(0);
                    return labelObj.getString("label"); // Retourne "POSITIVE" ou "NEGATIVE"
                } else {
                    throw new JSONException("La clé 'scored_labels' est absente de la réponse.");
                }
            } catch (JSONException e) {
                System.err.println("Erreur JSON : " + e.getMessage());
                throw e;
            }
        } catch (IOException e) {
            System.err.println("Erreur d'API ou de connexion : " + e.getMessage());
            throw e;
        }
    }

}
