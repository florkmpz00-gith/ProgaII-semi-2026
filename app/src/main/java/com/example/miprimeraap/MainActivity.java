package com.example.miprimeraap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    TextView tempVal;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btnCalcular);
        btn.setOnClickListener(v -> calcular());

    }

    private void calcular() {
        tempVal = findViewById(R.id.txtNum1);
        Double Num1 = Double.parseDouble(tempVal.getText().toString());

        tempVal = findViewById(R.id.txtNum2);
        Double Num2 = Double.parseDouble(tempVal.getText().toString());

        double respuesta = Num1 + Num2;
        tempVal = findViewById(R.id.txtRespuesta);
        tempVal.setText("Respuesta:" + respuesta);
    }
};