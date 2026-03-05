package com.example.parcial_i;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TabHost tabHost = findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.addTab(tabHost.newTabSpec("agua").setIndicator("Agua").setContent(R.id.tab_agua));
        tabHost.addTab(tabHost.newTabSpec("area").setIndicator("Área").setContent(R.id.tab_area));

        EditText txtMetros = findViewById(R.id.txtMetros);
        TextView txtResultadoAgua = findViewById(R.id.txtResultadoAgua);

        findViewById(R.id.btnAgua).setOnClickListener(v -> {
            String s = txtMetros.getText().toString();
            if (s.isEmpty()) return;
            double m = Double.parseDouble(s);
            double total;
            if (m <= 18)      total = 6.0;
            else if (m <= 28) total = 6.0 + (m - 18) * 0.45;
            else              total = 6.0 + (10 * 0.45) + (m - 28) * 0.65;
            txtResultadoAgua.setText("Total: $" + String.format("%.2f", total));
        });

        String[] unidades = {"m²", "km²", "cm²", "hectárea"};

        double[] factores = {1, 1_000_000, 0.0001, 10_000};

        Spinner spinnerDe = findViewById(R.id.spinnerDe);
        Spinner spinnerA  = findViewById(R.id.spinnerA);
        EditText txtArea  = findViewById(R.id.txtArea);
        TextView txtResultadoArea = findViewById(R.id.txtResultadoArea);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, unidades);
        spinnerDe.setAdapter(adapter);
        spinnerA.setAdapter(adapter);

        findViewById(R.id.btnArea).setOnClickListener(v -> {
            String s = txtArea.getText().toString();
            if (s.isEmpty()) return;
            double valor = Double.parseDouble(s);
            double enMetros = valor * factores[spinnerDe.getSelectedItemPosition()];
            double resultado = enMetros / factores[spinnerA.getSelectedItemPosition()];
            txtResultadoArea.setText("Resultado: " + String.format("%.4f", resultado) + " " + unidades[spinnerA.getSelectedItemPosition()]);
        });
    }
}