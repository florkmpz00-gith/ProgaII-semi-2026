package com.example.auramoda.ui.auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.auramoda.DatabaseHelper;
import com.example.auramoda.MainActivity;
import com.example.auramoda.R;
import com.example.auramoda.ui.onboarding.OnboardingActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;
    TextView tvRegister;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new DatabaseHelper(this);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.loginUsuario(email, password)) {
                String nombre = db.getNombreUsuario(email);
                SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
                prefs.edit()
                        .putString("user_name", nombre)
                        .putString("user_email", email)
                        .apply();

                boolean onboardingDone = prefs.getBoolean("onboarding_done", false);
                if (onboardingDone) {
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    startActivity(new Intent(this, OnboardingActivity.class));
                }
                finish();
            } else {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
            }
        });

        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(this, RegisterActivity.class));
        });
    }
}