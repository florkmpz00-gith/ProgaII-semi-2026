package com.example.auramoda;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class CompartirOutfitActivity extends AppCompatActivity {

    RecyclerView rvUsuarios;
    ProgressBar progressUsuarios;
    EditText etMensaje;
    TextView btnEnviar;
    ImageView ivOutfitPreview;
    String imageUrl, miEmail, miNombre;
    String usuarioSeleccionadoEmail = "";
    String usuarioSeleccionadoNombre = "";
    List<JSONObject> usuarios = new ArrayList<>();
    UsuariosAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartir_outfit);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        miEmail = prefs.getString("user_email", "");
        miNombre = prefs.getString("user_name", "");
        imageUrl = getIntent().getStringExtra("image_url");

        ivOutfitPreview = findViewById(R.id.ivOutfitPreview);
        rvUsuarios = findViewById(R.id.rvUsuarios);
        progressUsuarios = findViewById(R.id.progressUsuarios);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        Glide.with(this).load(imageUrl).into(ivOutfitPreview);

        adapter = new UsuariosAdapter();
        rvUsuarios.setLayoutManager(new LinearLayoutManager(this));
        rvUsuarios.setAdapter(adapter);

        cargarUsuarios();

        btnEnviar.setOnClickListener(v -> {
            if (usuarioSeleccionadoEmail.isEmpty()) {
                Toast.makeText(this, "Selecciona un usuario", Toast.LENGTH_SHORT).show();
                return;
            }
            String mensaje = etMensaje.getText().toString().trim();
            if (mensaje.isEmpty()) mensaje = miNombre + " te compartió un outfit!";

            btnEnviar.setEnabled(false);
            String mensajeFinal = mensaje;
            CouchDbHelper.enviarOutfit(miEmail, usuarioSeleccionadoEmail, imageUrl, mensajeFinal,
                    new CouchDbHelper.CouchCallback() {
                        @Override
                        public void onSuccess(String resultado) {
                            Toast.makeText(CompartirOutfitActivity.this,
                                    "Outfit enviado a " + usuarioSeleccionadoNombre + "! ❤️",
                                    Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        @Override
                        public void onError(String error) {
                            btnEnviar.setEnabled(true);
                            Toast.makeText(CompartirOutfitActivity.this,
                                    "Error al enviar: " + error, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    void cargarUsuarios() {
        progressUsuarios.setVisibility(View.VISIBLE);
        CouchDbHelper.getUsuarios(miEmail, new CouchDbHelper.CouchListCallback() {
            @Override
            public void onSuccess(List<JSONObject> docs) {
                progressUsuarios.setVisibility(View.GONE);
                usuarios.clear();
                usuarios.addAll(docs);
                adapter.notifyDataSetChanged();
                if (usuarios.isEmpty()) {
                    Toast.makeText(CompartirOutfitActivity.this,
                            "No hay otros usuarios registrados aún", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onError(String error) {
                progressUsuarios.setVisibility(View.GONE);
                Toast.makeText(CompartirOutfitActivity.this,
                        "Error cargando usuarios", Toast.LENGTH_SHORT).show();
            }
        });
    }

    class UsuariosAdapter extends RecyclerView.Adapter<UsuariosAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(android.R.layout.simple_list_item_1, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            try {
                JSONObject user = usuarios.get(position);
                String nombre = user.optString("nombre", "Usuario");
                String email = user.optString("email", "");
                holder.tvNombre.setText(nombre);

                boolean seleccionado = email.equals(usuarioSeleccionadoEmail);
                holder.tvNombre.setBackgroundResource(seleccionado ?
                        R.drawable.bg_chip_active : R.drawable.bg_card_light);
                holder.tvNombre.setPadding(24, 16, 24, 16);

                holder.itemView.setOnClickListener(v -> {
                    usuarioSeleccionadoEmail = email;
                    usuarioSeleccionadoNombre = nombre;
                    notifyDataSetChanged();
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() { return usuarios.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView tvNombre;
            VH(View v) {
                super(v);
                tvNombre = v.findViewById(android.R.id.text1);
            }
        }
    }
}