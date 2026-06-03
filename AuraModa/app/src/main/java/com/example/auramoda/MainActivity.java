package com.example.auramoda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    TextView tvSaludo, tvUserAvatar, tvOutfitIA, tvNombrePerfil, tvAvatarPerfil;
    LinearLayout cardIA, cardGaleria, cardArmario, cardTryOn, cardPerfil, layoutEstilos;
    TextView navHome, navChat, navTryOn, navPerfil;

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
        cardArmario = findViewById(R.id.cardArmario);
        cardTryOn = findViewById(R.id.cardTryOn);
        cardPerfil = findViewById(R.id.cardPerfil);
        layoutEstilos = findViewById(R.id.layoutEstilos);
        navHome = findViewById(R.id.navHome);
        navChat = findViewById(R.id.navChat);
        navTryOn = findViewById(R.id.navTryOn);
        navPerfil = findViewById(R.id.navPerfil);

        Calendar calendar = Calendar.getInstance();
        int hora = calendar.get(Calendar.HOUR_OF_DAY);
        String saludo;
        if (hora >= 5 && hora < 12) saludo = "Buenos días";
        else if (hora >= 12 && hora < 18) saludo = "Buenas tardes";
        else saludo = "Buenas noches";
        tvSaludo.setText(saludo + ", " + nombre + "!");

        if (!nombre.isEmpty()) {
            String inicial = String.valueOf(nombre.charAt(0)).toUpperCase();
            tvUserAvatar.setText(inicial);
            tvAvatarPerfil.setText(inicial);
            tvNombrePerfil.setText(nombre);
        }

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

        String fechaHoy = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String fechaGuardada = prefs.getString("outfit_fecha", "");
        String outfitGuardado = prefs.getString("outfit_del_dia", "");

        if (fechaHoy.equals(fechaGuardada) && !outfitGuardado.isEmpty()) {
            tvOutfitIA.setText(outfitGuardado);
        } else {
            tvOutfitIA.setText("Generando outfit del día...");
            GeminiHelper.preguntar(
                    "Eres AuraModa, estilista experta. Dame el outfit del día para " + nombre + " considerando sus estilos favoritos: " + estilos + ". Formato exacto:\n✨ OUTFIT DEL DÍA\n[Nombre del look]\n\n👗 Prendas:\n• [prenda 1]\n• [prenda 2]\n• [prenda 3]\n\n👠 Zapatos: [opción]\n👜 Accesorio: [opción]\n\n💡 Tip: [consejo corto de estilo]\n\nSolo esto, sin saludos, max 8 líneas.",
                    nombre, estilos,
                    new GeminiHelper.GeminiCallback() {
                        @Override
                        public void onRespuesta(String respuesta) {
                            tvOutfitIA.setText(respuesta);
                            prefs.edit()
                                    .putString("outfit_del_dia", respuesta)
                                    .putString("outfit_fecha", fechaHoy)
                                    .apply();
                        }
                        @Override
                        public void onError(String error) {
                            tvOutfitIA.setText("✨ OUTFIT DEL DÍA\nChic Casual Vibes\n\n👗 Prendas:\n• Blusa oversize beige\n• Mom jeans azul\n• Blazer crema\n\n👠 Zapatos: Mules nude\n👜 Accesorio: Bolso mini marrón\n\n💡 Tip: Metete la blusa por delante.");
                        }
                    });
        }

        cardIA.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        findViewById(R.id.btnVerOutfit).setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        cardGaleria.setOnClickListener(v -> startActivity(new Intent(this, GaleriaActivity.class)));
        cardArmario.setOnClickListener(v -> startActivity(new Intent(this, ArmarioActivity.class)));
        cardTryOn.setOnClickListener(v -> startActivity(new Intent(this, TryOnActivity.class)));
        cardPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));
        navChat.setOnClickListener(v -> startActivity(new Intent(this, ChatActivity.class)));
        navTryOn.setOnClickListener(v -> startActivity(new Intent(this, TryOnActivity.class)));
        navPerfil.setOnClickListener(v -> startActivity(new Intent(this, PerfilActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "");
        if (!nombre.isEmpty()) {
            tvNombrePerfil.setText(nombre);
            tvSaludo.setText(obtenerSaludo() + ", " + nombre + "!");
            String inicial = String.valueOf(nombre.charAt(0)).toUpperCase();
            tvUserAvatar.setText(inicial);
            tvAvatarPerfil.setText(inicial);
        }
    }

    String obtenerSaludo() {
        int hora = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hora >= 5 && hora < 12) return "Buenos días";
        else if (hora >= 12 && hora < 18) return "Buenas tardes";
        else return "Buenas noches";
    }
}