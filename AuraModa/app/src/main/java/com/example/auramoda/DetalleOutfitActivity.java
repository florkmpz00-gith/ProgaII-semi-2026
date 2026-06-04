package com.example.auramoda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetalleOutfitActivity extends AppCompatActivity {

    ImageView ivOutfit;
    LinearLayout layoutTiendas;
    ProgressBar progressTiendas;
    TextView tvTituloEstilo, btnFavorito, btnFavoritoImagen, btnCompartir;
    boolean esFavorito = false;
    String imageUrlActual = "";
    String estiloActual = "";
    DatabaseHelper dbHelper;

    private static final String API_KEY = "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";
    private static final String URL_BASE = "https://api.groq.com/openai/v1/chat/completions";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_outfit);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        ivOutfit = findViewById(R.id.ivOutfit);
        layoutTiendas = findViewById(R.id.layoutTiendas);
        progressTiendas = findViewById(R.id.progressTiendas);
        tvTituloEstilo = findViewById(R.id.tvTituloEstilo);
        btnFavorito = findViewById(R.id.btnFavorito);
        btnFavoritoImagen = findViewById(R.id.btnFavoritoImagen);
        btnCompartir = findViewById(R.id.btnCompartir);

        imageUrlActual = getIntent().getStringExtra("image_url");
        estiloActual = getIntent().getStringExtra("estilo");

        dbHelper = new DatabaseHelper(this);

        Glide.with(this).load(imageUrlActual).into(ivOutfit);
        tvTituloEstilo.setText("Estilo: " + estiloActual);

        // Verificar si ya es favorito
        android.database.Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                "SELECT id FROM favoritos WHERE url = ?", new String[]{imageUrlActual});
        if (cursor.moveToFirst()) {
            esFavorito = true;
            btnFavorito.setText("❤️");
            btnFavoritoImagen.setText("❤️");
        }
        cursor.close();

        btnFavorito.setOnClickListener(v -> toggleFavorito());
        btnFavoritoImagen.setOnClickListener(v -> toggleFavorito());

        // Registrar usuario en CouchDB al entrar
        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "");
        String email = prefs.getString("user_email", "");
        CouchDbHelper.registrarUsuario(nombre, email);

        btnCompartir.setOnClickListener(v -> {
            Intent intent = new Intent(this, CompartirOutfitActivity.class);
            intent.putExtra("image_url", imageUrlActual);
            startActivity(intent);
        });

        buscarTiendas(estiloActual);
    }

    void toggleFavorito() {
        if (esFavorito) {
            android.database.Cursor cursor = dbHelper.getReadableDatabase().rawQuery(
                    "SELECT id FROM favoritos WHERE url = ?", new String[]{imageUrlActual});
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(0);
                dbHelper.eliminarFavorito(id);
            }
            cursor.close();
            esFavorito = false;
            btnFavorito.setText("🤍");
            btnFavoritoImagen.setText("🤍");
            Toast.makeText(this, "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.agregarFavorito(imageUrlActual, estiloActual);
            esFavorito = true;
            btnFavorito.setText("❤️");
            btnFavoritoImagen.setText("❤️");
            Toast.makeText(this, "¡Guardado en favoritos! ❤️", Toast.LENGTH_SHORT).show();
        }
    }

    void buscarTiendas(String estilo) {
        progressTiendas.setVisibility(View.VISIBLE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String termino = estilo.replace(" ", "+").toLowerCase();

                String prompt = "Eres un experto en moda. Para un outfit de estilo \"" + estilo + "\", " +
                        "recomienda exactamente 4 tiendas online con links de busqueda directa a prendas similares. " +
                        "Responde SOLO con JSON array sin texto extra ni markdown. " +
                        "Usa el termino de busqueda \"" + termino + "\" adaptado para cada tienda. " +
                        "Formato exacto: " +
                        "{\"tienda\":\"Zara\",\"descripcion\":\"Descripcion corta\",\"precio\":\"$20-$60\",\"url\":\"https://www.zara.com/us/en/search?searchTerm=" + termino + "\"}," +
                        "{\"tienda\":\"Forever21\",\"descripcion\":\"Descripcion corta\",\"precio\":\"$10-$35\",\"url\":\"https://www.forever21.com/us/search?q=" + termino + "\"}]";

                JSONObject systemMsg = new JSONObject();
                systemMsg.put("role", "system");
                systemMsg.put("content", "Eres un asistente de moda. Respondes SOLO con JSON puro, sin texto adicional.");

                JSONObject userMsg = new JSONObject();
                userMsg.put("role", "user");
                userMsg.put("content", prompt);

                JSONArray messages = new JSONArray();
                messages.put(systemMsg);
                messages.put(userMsg);

                JSONObject body = new JSONObject();
                body.put("model", "llama-3.3-70b-versatile");
                body.put("messages", messages);
                body.put("max_tokens", 600);

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

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) response.append(line);
                reader.close();

                JSONObject json = new JSONObject(response.toString());
                String contenido = json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content")
                        .trim();

                contenido = contenido.replace("```json", "").replace("```", "").trim();
                JSONArray tiendas = new JSONArray(contenido);

                handler.post(() -> {
                    progressTiendas.setVisibility(View.GONE);
                    mostrarTiendas(tiendas);
                });

            } catch (Exception e) {
                handler.post(() -> {
                    progressTiendas.setVisibility(View.GONE);
                    android.util.Log.e("DetalleOutfit", "Error: " + e.getMessage(), e);
                });
            }
        });
    }

    void mostrarTiendas(JSONArray tiendas) {
        layoutTiendas.removeAllViews();
        for (int i = 0; i < tiendas.length(); i++) {
            try {
                JSONObject t = tiendas.getJSONObject(i);
                String nombre = t.getString("tienda");
                String descripcion = t.getString("descripcion");
                String precio = t.getString("precio");
                String urlTienda = t.getString("url");

                View card = getLayoutInflater().inflate(R.layout.item_tienda, layoutTiendas, false);
                ((TextView) card.findViewById(R.id.tvTiendaNombre)).setText(nombre);
                ((TextView) card.findViewById(R.id.tvTiendaDesc)).setText(descripcion);
                ((TextView) card.findViewById(R.id.tvTiendaPrecio)).setText(precio);
                card.findViewById(R.id.btnVisitar).setOnClickListener(v -> {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlTienda));
                    startActivity(intent);
                });
                layoutTiendas.addView(card);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}