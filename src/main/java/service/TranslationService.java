package service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

public class TranslationService {
    public static String translateText(String text, String sourceLang, String targetLang) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String encodedText = URLEncoder.encode(text, StandardCharsets.UTF_8);
        String encodedLangPair = URLEncoder.encode(sourceLang + "|" + targetLang, StandardCharsets.UTF_8);
        String url = "https://api.mymemory.translated.net/get?q=" + encodedText + "&langpair=" + encodedLangPair;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Ici on parse directement la réponse JSON
        JSONObject obj = new JSONObject(response.body());
        String translatedText = obj.getJSONObject("responseData").getString("translatedText");

        return translatedText;
    }
}