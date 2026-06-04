package com.example.auramoda;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AgregarPrendaActivity extends AppCompatActivity {

    EditText etNombre, etColor;
    TextView btnGuardar, tvTipoSeleccionado, tvTallaSeleccionada;
    TextView chipCamiseta, chipPantalon, chipVestido, chipChaqueta,
            chipZapatos, chipAccesorio, chipFalda, chipBlusa, chipShorts;
    TextView chipXS, chipS, chipM, chipL, chipXL;
    DatabaseHelper db;
    String tipoSeleccionado = "Camiseta";
    String tallaSeleccionada = "S";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_prenda);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        db = new DatabaseHelper(this);
        etNombre = findViewById(R.id.etNombrePrenda);
        etColor = findViewById(R.id.etColorPrenda);
        btnGuardar = findViewById(R.id.btnGuardarPrenda);
        tvTipoSeleccionado = findViewById(R.id.tvTipoSeleccionado);
        tvTallaSeleccionada = findViewById(R.id.tvTallaSeleccionada);

        chipCamiseta = findViewById(R.id.chipCamiseta);
        chipPantalon = findViewById(R.id.chipPantalon);
        chipVestido = findViewById(R.id.chipVestido);
        chipChaqueta = findViewById(R.id.chipChaqueta);
        chipZapatos = findViewById(R.id.chipZapatos);
        chipAccesorio = findViewById(R.id.chipAccesorio);
        chipFalda = findViewById(R.id.chipFalda);
        chipBlusa = findViewById(R.id.chipBlusa);
        chipShorts = findViewById(R.id.chipShorts);

        chipXS = findViewById(R.id.chipXS);
        chipS = findViewById(R.id.chipS);
        chipM = findViewById(R.id.chipM);
        chipL = findViewById(R.id.chipL);
        chipXL = findViewById(R.id.chipXL);

        setupChipsTipo();
        setupChipsTalla();

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String color = etColor.getText().toString().trim();

            if (nombre.isEmpty() || color.isEmpty()) {
                Toast.makeText(this, "Completa nombre y color", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardamos tipo + talla en el campo tipo
            String tipoCompleto = tipoSeleccionado + " talla " + tallaSeleccionada;
            if (db.agregarPrenda(nombre, tipoCompleto, color)) {
                Toast.makeText(this, "✓ Prenda guardada!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    void setupChipsTipo() {
        TextView[] chips = {chipCamiseta, chipPantalon, chipVestido, chipChaqueta,
                chipZapatos, chipAccesorio, chipFalda, chipBlusa, chipShorts};
        String[] tipos = {"Camiseta", "Pantalón", "Vestido", "Chaqueta",
                "Zapatos", "Accesorio", "Falda", "Blusa", "Shorts"};

        for (int i = 0; i < chips.length; i++) {
            int index = i;
            chips[i].setOnClickListener(v -> {
                tipoSeleccionado = tipos[index];
                tvTipoSeleccionado.setText("Seleccionado: " + tipoSeleccionado);
                for (TextView chip : chips) {
                    chip.setBackgroundResource(R.drawable.bg_input);
                    chip.setTextColor(getResources().getColor(R.color.gray_muted));
                }
                chips[index].setBackgroundResource(R.drawable.bg_chip_active);
                chips[index].setTextColor(getResources().getColor(R.color.black_primary));
            });
        }
    }

    void setupChipsTalla() {
        TextView[] chips = {chipXS, chipS, chipM, chipL, chipXL};
        String[] tallas = {"XS", "S", "M", "L", "XL"};

        for (int i = 0; i < chips.length; i++) {
            int index = i;
            chips[i].setOnClickListener(v -> {
                tallaSeleccionada = tallas[index];
                tvTallaSeleccionada.setText("Seleccionada: " + tallaSeleccionada);
                for (TextView chip : chips) {
                    chip.setBackgroundResource(R.drawable.bg_input);
                    chip.setTextColor(getResources().getColor(R.color.gray_muted));
                }
                chips[index].setBackgroundResource(R.drawable.bg_chip_active);
                chips[index].setTextColor(getResources().getColor(R.color.black_primary));
            });
        }
    }
}