package com.example.auramoda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class PerfilActivity extends AppCompatActivity {

    TextView tvAvatar, tvNombre, tvFavCount, tvEdad, tvModoToggle;
    LinearLayout layoutEstilos, menuFavoritos, menuHistorial, menuModo, menuCerrar;
    DatabaseHelper db;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        // Nav
        findViewById(R.id.navHome).setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
        findViewById(R.id.navChat).setOnClickListener(v -> {
            startActivity(new Intent(this, ChatActivity.class));
            finish();
        });
        findViewById(R.id.navTryOn).setOnClickListener(v -> {
            startActivity(new Intent(this, TryOnActivity.class));
            finish();
        });

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "");
        String estilos = prefs.getString("user_estilos", "");
        String edad = prefs.getString("user_edad", "-");

        tvAvatar = findViewById(R.id.tvAvatar);
        tvNombre = findViewById(R.id.tvNombre);
        tvFavCount = findViewById(R.id.tvFavCount);
        tvEdad = findViewById(R.id.tvEdad);
        tvModoToggle = findViewById(R.id.tvModoToggle);
        layoutEstilos = findViewById(R.id.layoutEstilos);
        menuFavoritos = findViewById(R.id.menuFavoritos);
        menuHistorial = findViewById(R.id.menuHistorial);
        menuModo = findViewById(R.id.menuModo);
        menuCerrar = findViewById(R.id.menuCerrar);

        if (!nombre.isEmpty()) {
            tvAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
            tvNombre.setText(nombre);
        }
        tvEdad.setText(edad);

        Cursor cursor = db.getFavoritos();
        tvFavCount.setText(String.valueOf(cursor.getCount()));
        cursor.close();

        if (!estilos.isEmpty()) {
            String[] arr = estilos.split(", ");
            for (String estilo : arr) {
                if (estilo.trim().isEmpty()) continue;
                TextView chip = new TextView(this);
                chip.setText(estilo.trim());
                chip.setTextColor(Color.parseColor("#C9A547"));
                chip.setTextSize(10);
                chip.setPadding(20, 6, 20, 6);
                chip.setBackgroundResource(R.drawable.bg_input);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMarginEnd(6);
                chip.setLayoutParams(params);
                layoutEstilos.addView(chip);
            }
        }

        boolean modoOscuro = prefs.getBoolean("modo_oscuro", true);
        tvModoToggle.setText(modoOscuro ? "ON" : "OFF");
        tvModoToggle.setTextColor(modoOscuro ?
                Color.parseColor("#C9A547") : Color.parseColor("#555555"));

        menuModo.setOnClickListener(v -> {
            boolean oscuroActual = prefs.getBoolean("modo_oscuro", true);
            boolean nuevoModo = !oscuroActual;
            prefs.edit().putBoolean("modo_oscuro", nuevoModo).apply();
            tvModoToggle.setText(nuevoModo ? "ON" : "OFF");
            tvModoToggle.setTextColor(nuevoModo ?
                    Color.parseColor("#C9A547") : Color.parseColor("#555555"));
            AppCompatDelegate.setDefaultNightMode(
                    nuevoModo ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        });

        menuFavoritos.setOnClickListener(v ->
                startActivity(new Intent(this, FavoritosActivity.class)));

        menuHistorial.setOnClickListener(v ->
                startActivity(new Intent(this, FavoritosActivity.class)));

        menuCerrar.setOnClickListener(v -> {
            prefs.edit()
                    .remove("user_name")
                    .remove("user_email")
                    .apply();
            startActivity(new Intent(this, SplashActivity.class));
            finishAffinity();
        });
    }
}