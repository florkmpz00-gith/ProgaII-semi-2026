package com.example.auramoda;

import android.os.Handler;
import android.os.Looper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeminiHelper {

    private static final String API_KEY = "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";
    private static final String URL_BASE = "https://api.groq.com/openai/v1/chat/completions";

    public interface GeminiCallback {
        void onRespuesta(String respuesta);
        void onError(String error);
    }

    public static void preguntar(String prompt, String userName, String userEstilos, GeminiCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String contexto = "Eres AuraModa, un estilista personal con IA. " +
                        "El usuario se llama " + userName + ". " +
                        "Sus estilos favoritos son: " + userEstilos + ". " +
                        "Responde de forma amigable, corta y practica sobre moda y outfits. " +
                        "Siempre da recomendaciones especificas de prendas.";

                JSONObject systemMsg = new JSONObject();
                systemMsg.put("role", "system");
                systemMsg.put("content", contexto);

                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", prompt);

                JSONArray messages = new JSONArray();
                messages.put(systemMsg);
                messages.put(userMsg);

                JSONObject body = new JSONObject();
                body.put("model", "llama-3.3-70b-versatile");
                body.put("messages", messages);
                body.put("max_tokens", 500);

                URL url = new URL(URL_BASE);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setDoOutput(true);
                conn.setConnectTimeout(15000);
                conn.setReadTimeout(15000);

                OutputStream os = conn.getOutputStream();
                os.write(body.toString().getBytes("UTF-8"));
                os.close();

                int responseCode = conn.getResponseCode();
                BufferedReader reader;

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    reader.close();
                    android.util.Log.e("GeminiHelper", "HTTP Error " + responseCode + ": " + errorResponse);
                    handler.post(() -> callback.onError("Error " + responseCode + ": " + errorResponse));
                    return;
                }

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                String respuesta = json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                handler.post(() -> callback.onRespuesta(respuesta));

            } catch (Exception e) {
                android.util.Log.e("GeminiHelper", "Excepcion: " + e.getMessage(), e);
                handler.post(() -> callback.onError("Excepcion: " + e.getMessage()));
            }
        });
    }
}