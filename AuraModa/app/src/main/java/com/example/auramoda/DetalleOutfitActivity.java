package com.example.auramoda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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
    TextView tvTituloEstilo;

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

        String imageUrl = getIntent().getStringExtra("image_url");
        String estilo = getIntent().getStringExtra("estilo");

        Glide.with(this).load(imageUrl).into(ivOutfit);
        tvTituloEstilo.setText("Estilo: " + estilo);

        buscarTiendas(estilo);
    }

    void buscarTiendas(String estilo) {
        progressTiendas.setVisibility(View.VISIBLE);
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            try {
                String prompt = "Eres un experto en moda. Para un outfit de estilo \"" + estilo + "\", " +
                        "recomienda exactamente 4 tiendas online con links de busqueda directa a prendas similares. " +
                        "Responde SOLO con JSON array sin texto extra ni markdown. " +
                        "Formato exacto: " +
                        "[{\"tienda\":\"SHEIN\",\"descripcion\":\"Vestidos casuales florales\",\"precio\":\"$8-$25\",\"url\":\"https://www.shein.com/search?q=casual+floral+dress\"}," +
                        "{\"tienda\":\"Zara\",\"descripcion\":\"Tops y pantalones modernos\",\"precio\":\"$20-$60\",\"url\":\"https://www.zara.com/us/en/search?searchTerm=casual+top\"}," +
                        "{\"tienda\":\"H&M\",\"descripcion\":\"Ropa casual asequible\",\"precio\":\"$10-$40\",\"url\":\"https://www2.hm.com/en_us/search-results.html?q=casual+outfit\"}," +
                        "{\"tienda\":\"Forever21\",\"descripcion\":\"Moda juvenil tendencia\",\"precio\":\"$10-$35\",\"url\":\"https://www.forever21.com/us/search?q=casual+dress\"}]" +
                        "Adapta los terminos de busqueda en las URLs al estilo especifico del outfit.";

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