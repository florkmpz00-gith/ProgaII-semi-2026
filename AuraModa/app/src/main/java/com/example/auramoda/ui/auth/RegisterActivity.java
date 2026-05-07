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
import com.example.auramoda.R;
import com.example.auramoda.ui.onboarding.OnboardingActivity;

public class RegisterActivity extends AppCompatActivity {

    EditText etNombre, etEmail, etPassword, etConfirmPassword;
    Button btnRegister;
    TextView tvLogin, tvBack;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db = new DatabaseHelper(this);
        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        tvLogin = findViewById(R.id.tvLogin);
        tvBack = findViewById(R.id.tvBack);

        tvBack.setOnClickListener(v -> finish());

        btnRegister.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.emailExiste(email)) {
                Toast.makeText(this, "Este correo ya está registrado", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.registrarUsuario(nombre, email, password)) {
                getSharedPreferences("AuraModa", MODE_PRIVATE)
                        .edit()
                        .putString("user_name", nombre)
                        .putString("user_email", email)
                        .apply();

                Toast.makeText(this, "¡Cuenta creada!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, OnboardingActivity.class));
                finish();
            } else {
                Toast.makeText(this, "Error al crear cuenta", Toast.LENGTH_SHORT).show();
            }
        });

        tvLogin.setOnClickListener(v -> finish());
    }
}