package com.example.auramoda.api;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PexelsHelper {

    private static final String API_KEY = "It3qUZG7c7dkoY2f1b0WM4fZYPjwUZBoFbrubD4xjvMzUFkwDvmomxZv";
    private static final String BASE_URL = "https://api.pexels.com/v1/search?per_page=20&query=";

    public interface PexelsCallback {
        void onSuccess(List<String> imageUrls);
        void onError(String error);
    }

    public static void buscarFotos(String query, PexelsCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + query.replace(" ", "%20"));
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Authorization", API_KEY);
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                JSONArray photos = json.getJSONArray("photos");
                List<String> urls = new ArrayList<>();

                for (int i = 0; i < photos.length(); i++) {
                    JSONObject photo = photos.getJSONObject(i);
                    String imgUrl = photo.getJSONObject("src").getString("medium");
                    urls.add(imgUrl);
                }

                handler.post(() -> callback.onSuccess(urls));

            } catch (Exception e) {
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}