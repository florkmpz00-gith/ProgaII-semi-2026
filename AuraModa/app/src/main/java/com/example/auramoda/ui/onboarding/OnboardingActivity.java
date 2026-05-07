package com.example.auramoda.ui.onboarding;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.auramoda.MainActivity;
import com.example.auramoda.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class OnboardingActivity extends AppCompatActivity {

    int paso = 1;
    String edadSeleccionada = "";

    TextView tvTitle, tvSubtitle, chip1, chip2, chip3, chip4;
    EditText etInput;
    Button btnContinuar;
    View dot1, dot2, dot3;
    View layoutEdad;
    ChipGroup chipGroupEstilo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        tvTitle = findViewById(R.id.tvOnboardingTitle);
        tvSubtitle = findViewById(R.id.tvOnboardingSubtitle);
        etInput = findViewById(R.id.etOnboardingInput);
        btnContinuar = findViewById(R.id.btnContinuar);
        dot1 = findViewById(R.id.dot1);
        dot2 = findViewById(R.id.dot2);
        dot3 = findViewById(R.id.dot3);
        layoutEdad = findViewById(R.id.layoutEdad);
        chipGroupEstilo = findViewById(R.id.chipGroupEstilo);
        chip1 = findViewById(R.id.chip1);
        chip2 = findViewById(R.id.chip2);
        chip3 = findViewById(R.id.chip3);
        chip4 = findViewById(R.id.chip4);

        // Si ya tiene nombre guardado del registro, saltar al paso 2
        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        String nombreGuardado = prefs.getString("user_name", "");
        if (!nombreGuardado.isEmpty()) {
            paso = 2;
            mostrarPaso2();
        }

        // Chips de edad
        View.OnClickListener chipEdadClick = v -> {
            chip1.setTextColor(getColor(R.color.gray_muted));
            chip2.setTextColor(getColor(R.color.gray_muted));
            chip3.setTextColor(getColor(R.color.gray_muted));
            chip4.setTextColor(getColor(R.color.gray_muted));
            ((TextView) v).setTextColor(getColor(R.color.purple));
            edadSeleccionada = ((TextView) v).getText().toString();
        };
        chip1.setOnClickListener(chipEdadClick);
        chip2.setOnClickListener(chipEdadClick);
        chip3.setOnClickListener(chipEdadClick);
        chip4.setOnClickListener(chipEdadClick);

        btnContinuar.setOnClickListener(v -> {
            SharedPreferences.Editor editor = prefs.edit();

            if (paso == 1) {
                String nombre = etInput.getText().toString().trim();
                if (nombre.isEmpty()) {
                    Toast.makeText(this, "Escribe tu nombre", Toast.LENGTH_SHORT).show();
                    return;
                }
                editor.putString("user_name", nombre).apply();
                paso = 2;
                mostrarPaso2();

            } else if (paso == 2) {
                if (edadSeleccionada.isEmpty()) {
                    Toast.makeText(this, "Selecciona tu edad", Toast.LENGTH_SHORT).show();
                    return;
                }
                editor.putString("user_edad", edadSeleccionada).apply();
                paso = 3;
                mostrarPaso3();

            } else if (paso == 3) {
                StringBuilder estilos = new StringBuilder();
                for (int id : chipGroupEstilo.getCheckedChipIds()) {
                    Chip chip = findViewById(id);
                    estilos.append(chip.getText()).append(", ");
                }
                if (estilos.length() == 0) {
                    Toast.makeText(this, "Selecciona al menos un estilo", Toast.LENGTH_SHORT).show();
                    return;
                }
                editor.putString("user_estilos", estilos.toString())
                        .putBoolean("onboarding_done", true)
                        .apply();
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });
    }

    void mostrarPaso2() {
        tvTitle.setText("¿Qué edad tienes?");
        tvSubtitle.setText("Para recomendarte estilos de tu edad");
        etInput.setVisibility(View.GONE);
        layoutEdad.setVisibility(View.VISIBLE);
        dot1.setBackgroundColor(getColor(R.color.gray_dark));
        dot2.setBackgroundColor(getColor(R.color.gold));
        btnContinuar.setBackgroundTintList(getColorStateList(R.color.purple));
    }

    void mostrarPaso3() {
        tvTitle.setText("¿Cuál es tu estilo?");
        tvSubtitle.setText("Puedes elegir varios");
        layoutEdad.setVisibility(View.GONE);
        chipGroupEstilo.setVisibility(View.VISIBLE);
        dot2.setBackgroundColor(getColor(R.color.gray_dark));
        dot3.setBackgroundColor(getColor(R.color.gold));
        btnContinuar.setText("¡Listo! Entrar a AuraModa");
        btnContinuar.setBackgroundTintList(getColorStateList(R.color.rose));
    }
}