package com.example.auramoda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import java.io.FileOutputStream;
import java.io.InputStream;

public class EditarPerfilActivity extends AppCompatActivity {

    EditText etNombre, etEdad;
    TextView btnGuardar, btnFotoGaleria, btnFotoCamara;
    ImageView ivFotoPerfil;
    SharedPreferences prefs;

    ActivityResultLauncher<Intent> galeriaLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri uri = result.getData().getData();
                    guardarFoto(uri);
                }
            });

    ActivityResultLauncher<Intent> camaraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap foto = (Bitmap) result.getData().getExtras().get("data");
                    guardarFotoBitmap(foto);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);

        etNombre = findViewById(R.id.etNombre);
        etEdad = findViewById(R.id.etEdad);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnFotoGaleria = findViewById(R.id.btnFotoGaleria);
        btnFotoCamara = findViewById(R.id.btnFotoCamara);
        ivFotoPerfil = findViewById(R.id.ivFotoPerfil);

        etNombre.setText(prefs.getString("user_name", ""));
        etEdad.setText(prefs.getString("user_edad", ""));

        // Cargar foto si existe
        String fotoPath = prefs.getString("user_foto", "");
        if (!fotoPath.isEmpty()) {
            try {
                ivFotoPerfil.setImageURI(Uri.parse(fotoPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnFotoGaleria.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galeriaLauncher.launch(intent);
        });

        btnFotoCamara.setOnClickListener(v -> {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            camaraLauncher.launch(intent);
        });

        btnGuardar.setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String edad = etEdad.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            prefs.edit()
                    .putString("user_name", nombre)
                    .putString("user_edad", edad)
                    .apply();

            String email = prefs.getString("user_email", "");
            DatabaseHelper db = new DatabaseHelper(this);
            db.actualizarPerfil(email, edad, prefs.getString("user_estilos", ""));

            Toast.makeText(this, "Perfil actualizado!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    void guardarFoto(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            FileOutputStream fos = openFileOutput("foto_perfil.jpg", MODE_PRIVATE);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
            fos.close();
            inputStream.close();
            String path = "file://" + getFilesDir() + "/foto_perfil.jpg";
            prefs.edit().putString("user_foto", path).apply();
            ivFotoPerfil.setImageURI(Uri.parse(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void guardarFotoBitmap(Bitmap bitmap) {
        try {
            FileOutputStream fos = openFileOutput("foto_perfil.jpg", MODE_PRIVATE);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
            String path = "file://" + getFilesDir() + "/foto_perfil.jpg";
            prefs.edit().putString("user_foto", path).apply();
            ivFotoPerfil.setImageURI(Uri.parse(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}