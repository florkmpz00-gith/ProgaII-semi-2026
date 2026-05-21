package com.example.auramoda;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AgregarPrendaActivity extends AppCompatActivity {

    EditText etNombre, etColor;
    TextView btnGuardar, tvTipoSeleccionado;
    TextView chipCamiseta, chipPantalon, chipVestido, chipChaqueta, chipZapatos, chipAccesorio;
    DatabaseHelper db;
    String tipoSeleccionado = "Camiseta";

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

        chipCamiseta = findViewById(R.id.chipCamiseta);
        chipPantalon = findViewById(R.id.chipPantalon);
        chipVestido = findViewById(R.id.chipVestido);
        chipChaqueta = findViewById(R.id.chipChaqueta);
        chipZapatos = findViewById(R.id.chipZapatos);
        chipAccesorio = findViewById(R.id.chipAccesorio);

        setupChips();

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String color = etColor.getText().toString().trim();

            if (nombre.isEmpty() || color.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            if (db.agregarPrenda(nombre, tipoSeleccionado, color)) {
                Toast.makeText(this, "Prenda agregada!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    void setupChips() {
        TextView[] chips = {chipCamiseta, chipPantalon, chipVestido, chipChaqueta, chipZapatos, chipAccesorio};
        String[] tipos = {"Camiseta", "Pantalón", "Vestido", "Chaqueta", "Zapatos", "Accesorio"};

        for (int i = 0; i < chips.length; i++) {
            int index = i;
            chips[i].setOnClickListener(v -> {
                tipoSeleccionado = tipos[index];
                tvTipoSeleccionado.setText("Tipo: " + tipoSeleccionado);
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