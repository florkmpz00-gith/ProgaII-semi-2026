package com.example.auramoda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TextView tvSaludo, tvUserAvatar, tvOutfitIA, tvNombrePerfil, tvAvatarPerfil;
    LinearLayout cardIA, cardGaleria, cardFavoritos, cardPerfil, layoutEstilos;
    TextView navHome, navGaleria, navChat, navPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "");
        String estilos = prefs.getString("user_estilos", "casual");

        tvSaludo = findViewById(R.id.tvSaludo);
        tvUserAvatar = findViewById(R.id.tvUserAvatar);
        tvOutfitIA = findViewById(R.id.tvOutfitIA);
        tvNombrePerfil = findViewById(R.id.tvNombrePerfil);
        tvAvatarPerfil = findViewById(R.id.tvAvatarPerfil);
        cardIA = findViewById(R.id.cardIA);
        cardGaleria = findViewById(R.id.cardGaleria);
        cardFavoritos = findViewById(R.id.cardFavoritos);
        cardPerfil = findViewById(R.id.cardPerfil);
        layoutEstilos = findViewById(R.id.layoutEstilos);
        navHome = findViewById(R.id.navHome);
        navGaleria = findViewById(R.id.navGaleria);
        navChat = findViewById(R.id.navChat);
        navPerfil = findViewById(R.id.navPerfil);

        // Saludo según hora
        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        String saludo;
        if (hora >= 5 && hora < 12) {
            saludo = "Buenos días";
        } else if (hora >= 12 && hora < 18) {
            saludo = "Buenas tardes";
        } else {
            saludo = "Buenas noches";
        }
        tvSaludo.setText(saludo + ", " + nombre + "!");

        // Avatar
        if (!nombre.isEmpty()) {
            String inicial = String.valueOf(nombre.charAt(0)).toUpperCase();
            tvUserAvatar.setText(inicial);
            tvAvatarPerfil.setText(inicial);
            tvNombrePerfil.setText(nombre);
        }

        // Estilos chips
        if (!estilos.isEmpty()) {
            String[] arr = estilos.split(", ");
            for (String estilo : arr) {
                if (estilo.trim().isEmpty()) continue;
                TextView chip = new TextView(this);
                chip.setText(estilo.trim());
                chip.setTextColor(Color.parseColor("#8B5CF6"));
                chip.setTextSize(11);
                chip.setPadding(24, 8, 24, 8);
                chip.setBackgroundResource(R.drawable.bg_input);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd(8);
                chip.setLayoutParams(params);
                layoutEstilos.addView(chip);
            }
        }

        // Outfit del dia con Gemini
        GeminiHelper.preguntar(
                "Dame una recomendacion de outfit corta para hoy, considerando mis estilos favoritos: " + estilos,
                nombre, estilos,
                new GeminiHelper.GeminiCallback() {
                    @Override
                    public void onRespuesta(String respuesta) {
                        tvOutfitIA.setText(respuesta);
                    }
                    @Override
                    public void onError(String error) {
                        tvOutfitIA.setText("Look casual: jeans + camisa basica + sneakers blancos.");
                    }
                });

        // Navegacion
        cardIA.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.btnVerOutfit).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        cardGaleria.setOnClickListener(v -> startActivity(new Intent(this, GaleriaActivity.class)));
        cardFavoritos.setOnClickListener(v -> startActivity(new Intent(this, FavoritosActivity.class)));
        cardPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));
        navGaleria.setOnClickListener(v -> startActivity(new Intent(this, GaleriaActivity.class)));
        navChat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        navPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));
    }
}