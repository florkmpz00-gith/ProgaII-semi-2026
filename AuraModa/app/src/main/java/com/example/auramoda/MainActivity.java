package com.example.auramoda;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;
import java.util.Calendar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        String nombre = prefs.getString("user_name", "");

        TextView tvSaludo = findViewById(R.id.tvSaludo);
        TextView tvUserAvatar = findViewById(R.id.tvUserAvatar);

        // Saludo según hora del día
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

        // Inicial del nombre en avatar
        if (!nombre.isEmpty()) {
            tvUserAvatar.setText(String.valueOf(nombre.charAt(0)).toUpperCase());
        }
    }
}