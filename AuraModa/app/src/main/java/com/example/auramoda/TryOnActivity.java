package com.example.auramoda;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class TryOnActivity extends AppCompatActivity {

    ImageView ivPersona;
    TextView btnSubirFoto, btnProcesar, tvEstado;
    EditText etOutfit;
    LinearLayout layoutResultado;
    ProgressBar progressTryOn;
    TextView tvResultado;

    Uri uriPersona = null;

    ActivityResultLauncher<Intent> imagePicker = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    uriPersona = result.getData().getData();
                    ivPersona.setImageURI(uriPersona);
                    tvEstado.setText("Foto cargada! Ahora describe el outfit.");
                    tvEstado.setVisibility(View.VISIBLE);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_on);

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
        findViewById(R.id.navPerfil).setOnClickListener(v -> {
            startActivity(new Intent(this, PerfilActivity.class));
            finish();
        });

        ivPersona = findViewById(R.id.ivPersona);
        btnSubirFoto = findViewById(R.id.btnSubirPersona);
        btnProcesar = findViewById(R.id.btnProcesar);
        tvEstado = findViewById(R.id.tvEstado);
        etOutfit = findViewById(R.id.etOutfit);
        layoutResultado = findViewById(R.id.layoutResultado);
        progressTryOn = findViewById(R.id.progressTryOn);
        tvResultado = findViewById(R.id.tvResultado);

        btnSubirFoto.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePicker.launch(intent);
        });

        btnProcesar.setOnClickListener(v -> {
            String outfit = etOutfit.getText().toString().trim();
            if (uriPersona == null) {
                Toast.makeText(this, "Sube tu foto primero", Toast.LENGTH_SHORT).show();
                return;
            }
            if (outfit.isEmpty()) {
                Toast.makeText(this, "Describe el outfit que quieres probar", Toast.LENGTH_SHORT).show();
                return;
            }
            procesarTryOn(outfit);
        });
    }

    void procesarTryOn(String outfit) {
        progressTryOn.setVisibility(View.VISIBLE);
        btnProcesar.setAlpha(0.5f);
        btnProcesar.setEnabled(false);
        tvEstado.setText("La IA está visualizando tu look...");
        tvEstado.setVisibility(View.VISIBLE);
        layoutResultado.setVisibility(View.GONE);

        android.content.SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        String userName = prefs.getString("user_name", "");
        String userEstilos = prefs.getString("user_estilos", "casual");

        String prompt = "Eres un estilista experto de moda con mucha imaginación visual. " +
                "El usuario se llama " + userName + " y sube una foto suya. " +
                "Describe de forma muy detallada y emocionante cómo se vería usando este outfit: \"" + outfit + "\". " +
                "Incluye: cómo le quedaría la silueta, qué colores resaltarían, cómo combina con su estilo (" + userEstilos + "), " +
                "qué accesorios complementarían el look, y una puntuación del 1 al 10 de qué tan bien le quedaría. " +
                "Sé específico, visual y entusiasta. Máximo 150 palabras.";

        GeminiHelper.preguntar(prompt, userName, userEstilos, new GeminiHelper.GeminiCallback() {
            @Override
            public void onRespuesta(String respuesta) {
                progressTryOn.setVisibility(View.GONE);
                btnProcesar.setAlpha(1f);
                btnProcesar.setEnabled(true);
                tvEstado.setText("¡Así te verías con ese outfit! ✨");
                layoutResultado.setVisibility(View.VISIBLE);
                tvResultado.setText(respuesta);
            }

            @Override
            public void onError(String error) {
                progressTryOn.setVisibility(View.GONE);
                btnProcesar.setAlpha(1f);
                btnProcesar.setEnabled(true);
                tvEstado.setText("Error al procesar. Intenta de nuevo.");
            }
        });
    }
}