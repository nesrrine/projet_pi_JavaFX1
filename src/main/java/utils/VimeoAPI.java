package utils;

import okhttp3.*;

import java.io.File;

public class VimeoAPI {
    private static final String ACCESS_TOKEN = "be3533833a7e36c7045526c46c67e2dd";

    // Télécharge une vidéo sur Vimeo
    public static void uploadVideo(String filePath) throws Exception {
        OkHttpClient client = new OkHttpClient();

        // Créer le corps de la requête avec le fichier vidéo à uploader
        RequestBody body = RequestBody.create(new File(filePath), MediaType.parse("video/mp4"));
        Request request = new Request.Builder()
                .url("https://api.vimeo.com/me/videos")
                .post(body)
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Réponse du serveur : " + response.body().string());
    }

    // Exemple d'utilisation pour télécharger une vidéo
    public static void main(String[] args) throws Exception {
        uploadVideo("path_to_your_video.mp4");
    }
    public static void getVideoDetails(String videoId) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.vimeo.com/videos/" + videoId)
                .get()
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Détails de la vidéo : " + response.body().string());
    }
    public static void postComment(String videoId, String message) throws Exception {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("text", message)
                .build();
        Request request = new Request.Builder()
                .url("https://api.vimeo.com/videos/" + videoId + "/comments")
                .post(body)
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Réponse du serveur : " + response.body().string());
    }
    public static void likeVideo(String videoId) throws Exception {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.vimeo.com/videos/" + videoId + "/likes")
                .put(RequestBody.create(null, new byte[0])) // Utilise PUT pour ajouter un like
                .addHeader("Authorization", "Bearer " + ACCESS_TOKEN)
                .build();

        Response response = client.newCall(request).execute();
        System.out.println("Réponse du serveur : " + response.body().string());
    }

}
