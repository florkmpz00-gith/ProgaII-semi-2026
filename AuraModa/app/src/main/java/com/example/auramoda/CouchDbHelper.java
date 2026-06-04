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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CouchDbHelper {

    private static final String BASE_URL = "http://192.168.82.149:5984";
    private static final String USER = "Flor11";
    private static final String PASS = "123456";
    private static final String DB_OUTFITS = "outfits_compartidos";
    private static final String DB_USUARIOS = "flor";

    public interface CouchCallback {
        void onSuccess(String resultado);
        void onError(String error);
    }

    public interface CouchListCallback {
        void onSuccess(List<JSONObject> docs);
        void onError(String error);
    }

    private static String getAuth() {
        String credentials = USER + ":" + PASS;
        return "Basic " + android.util.Base64.encodeToString(
                credentials.getBytes(), android.util.Base64.NO_WRAP);
    }

    public static void registrarUsuario(String nombre, String email) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                URL urlCheck = new URL(BASE_URL + "/" + DB_USUARIOS + "/_find");
                HttpURLConnection conn = (HttpURLConnection) urlCheck.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", getAuth());
                conn.setDoOutput(true);

                JSONObject query = new JSONObject();
                JSONObject selector = new JSONObject();
                selector.put("email", email);
                query.put("selector", selector);

                OutputStream os = conn.getOutputStream();
                os.write(query.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject result = new JSONObject(response.toString());
                JSONArray docs = result.getJSONArray("docs");

                if (docs.length() == 0) {
                    URL urlCreate = new URL(BASE_URL + "/" + DB_USUARIOS);
                    HttpURLConnection connCreate = (HttpURLConnection) urlCreate.openConnection();
                    connCreate.setRequestMethod("POST");
                    connCreate.setRequestProperty("Content-Type", "application/json");
                    connCreate.setRequestProperty("Authorization", getAuth());
                    connCreate.setDoOutput(true);

                    JSONObject doc = new JSONObject();
                    doc.put("tipo", "usuario");
                    doc.put("nombre", nombre);
                    doc.put("email", email);

                    OutputStream osCreate = connCreate.getOutputStream();
                    osCreate.write(doc.toString().getBytes("UTF-8"));
                    osCreate.close();
                    connCreate.getInputStream().close();
                    android.util.Log.d("CouchDb", "Usuario registrado: " + nombre);
                } else {
                    android.util.Log.d("CouchDb", "Usuario ya existe: " + nombre);
                }
            } catch (Exception e) {
                android.util.Log.e("CouchDb", "Error registrando usuario: " + e.getMessage());
            }
        });
    }

    public static void getUsuarios(String miEmail, CouchListCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                // Obtener TODOS los docs de la base sin filtrar por tipo
                URL url = new URL(BASE_URL + "/" + DB_USUARIOS + "/_all_docs?include_docs=true");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", getAuth());

                int code = conn.getResponseCode();
                android.util.Log.d("CouchDb", "getUsuarios response code: " + code);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                android.util.Log.d("CouchDb", "getUsuarios response: " + response.toString());

                JSONObject result = new JSONObject(response.toString());
                JSONArray rows = result.getJSONArray("rows");

                List<JSONObject> usuarios = new ArrayList<>();
                for (int i = 0; i < rows.length(); i++) {
                    JSONObject doc = rows.getJSONObject(i).optJSONObject("doc");
                    if (doc == null) continue;
                    // Saltar diseño de documentos
                    if (doc.optString("_id").startsWith("_design")) continue;
                    // Excluir al usuario actual
                    if (!doc.optString("email").equals(miEmail)) {
                        usuarios.add(doc);
                    }
                }

                android.util.Log.d("CouchDb", "Usuarios encontrados: " + usuarios.size());
                handler.post(() -> callback.onSuccess(usuarios));

            } catch (Exception e) {
                android.util.Log.e("CouchDb", "Error getUsuarios: " + e.getMessage());
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void enviarOutfit(String de, String para, String imageUrl, String mensaje, CouchCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/" + DB_OUTFITS);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", getAuth());
                conn.setDoOutput(true);

                JSONObject doc = new JSONObject();
                doc.put("de", de);
                doc.put("para", para);
                doc.put("imagen_url", imageUrl);
                doc.put("mensaje", mensaje);
                doc.put("fecha", String.valueOf(System.currentTimeMillis()));
                doc.put("visto", false);

                OutputStream os = conn.getOutputStream();
                os.write(doc.toString().getBytes("UTF-8"));
                os.close();

                int code = conn.getResponseCode();
                if (code == 201) {
                    handler.post(() -> callback.onSuccess("Outfit enviado!"));
                } else {
                    handler.post(() -> callback.onError("Error " + code));
                }

            } catch (Exception e) {
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void getOutfitsRecibidos(String miEmail, CouchListCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/" + DB_OUTFITS + "/_find");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", getAuth());
                conn.setDoOutput(true);

                JSONObject query = new JSONObject();
                JSONObject selector = new JSONObject();
                selector.put("para", miEmail);
                query.put("selector", selector);
                query.put("limit", 50);

                OutputStream os = conn.getOutputStream();
                os.write(query.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject result = new JSONObject(response.toString());
                JSONArray docs = result.getJSONArray("docs");

                List<JSONObject> outfits = new ArrayList<>();
                for (int i = 0; i < docs.length(); i++) {
                    outfits.add(docs.getJSONObject(i));
                }

                handler.post(() -> callback.onSuccess(outfits));

            } catch (Exception e) {
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    public static void getOutfitsEnviados(String miEmail, CouchListCallback callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                URL url = new URL(BASE_URL + "/" + DB_OUTFITS + "/_find");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", getAuth());
                conn.setDoOutput(true);

                JSONObject query = new JSONObject();
                JSONObject selector = new JSONObject();
                selector.put("de", miEmail);
                query.put("selector", selector);
                query.put("limit", 50);

                OutputStream os = conn.getOutputStream();
                os.write(query.toString().getBytes("UTF-8"));
                os.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject result = new JSONObject(response.toString());
                JSONArray docs = result.getJSONArray("docs");

                List<JSONObject> outfits = new ArrayList<>();
                for (int i = 0; i < docs.length(); i++) {
                    outfits.add(docs.getJSONObject(i));
                }

                handler.post(() -> callback.onSuccess(outfits));

            } catch (Exception e) {
                handler.post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}