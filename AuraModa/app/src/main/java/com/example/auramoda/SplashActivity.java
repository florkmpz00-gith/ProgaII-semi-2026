package com.example.auramoda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.auramoda.ui.auth.LoginActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        boolean modoOscuro = prefs.getBoolean("modo_oscuro", false);
        AppCompatDelegate.setDefaultNightMode(
                modoOscuro ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);

        setContentView(R.layout.activity_splash);

        ImageView ivLogo = findViewById(R.id.ivLogo);
        Animation floatAnim = AnimationUtils.loadAnimation(this, R.anim.float_logo);
        ivLogo.startAnimation(floatAnim);

        new Handler().postDelayed(() -> {
            String nombre = prefs.getString("user_name", "");
            String email = prefs.getString("user_email", "");

            if (!nombre.isEmpty() && !email.isEmpty()) {
                // Registrar en CouchDB cada vez que abre la app
                CouchDbHelper.registrarUsuario(nombre, email);
                startActivity(new Intent(this, MainActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
            finish();
        }, 3000);
    }
}