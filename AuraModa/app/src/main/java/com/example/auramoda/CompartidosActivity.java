package com.example.auramoda;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CompartidosActivity extends AppCompatActivity {

    RecyclerView rvCompartidos;
    ProgressBar progressCompartidos;
    LinearLayout layoutEmpty;
    TextView tabRecibidos, tabEnviados, tvEmptyMsg;
    List<JSONObject> items = new ArrayList<>();
    CompartidosAdapter adapter;
    String miEmail;
    boolean mostrandoRecibidos = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compartidos);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        miEmail = prefs.getString("user_email", "");

        rvCompartidos = findViewById(R.id.rvCompartidos);
        progressCompartidos = findViewById(R.id.progressCompartidos);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        tabRecibidos = findViewById(R.id.tabRecibidos);
        tabEnviados = findViewById(R.id.tabEnviados);
        tvEmptyMsg = findViewById(R.id.tvEmptyMsg);

        adapter = new CompartidosAdapter();
        rvCompartidos.setLayoutManager(new LinearLayoutManager(this));
        rvCompartidos.setAdapter(adapter);

        tabRecibidos.setOnClickListener(v -> {
            mostrandoRecibidos = true;
            tabRecibidos.setTextColor(getResources().getColor(R.color.gold));
            tabRecibidos.setBackgroundResource(R.drawable.bg_nav_selected);
            tabEnviados.setTextColor(getResources().getColor(R.color.gray_muted));
            tabEnviados.setBackground(null);
            cargarDatos();
        });

        tabEnviados.setOnClickListener(v -> {
            mostrandoRecibidos = false;
            tabEnviados.setTextColor(getResources().getColor(R.color.gold));
            tabEnviados.setBackgroundResource(R.drawable.bg_nav_selected);
            tabRecibidos.setTextColor(getResources().getColor(R.color.gray_muted));
            tabRecibidos.setBackground(null);
            cargarDatos();
        });

        cargarDatos();
    }

    void cargarDatos() {
        progressCompartidos.setVisibility(View.VISIBLE);
        rvCompartidos.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        CouchDbHelper.CouchListCallback callback = new CouchDbHelper.CouchListCallback() {
            @Override
            public void onSuccess(List<JSONObject> docs) {
                progressCompartidos.setVisibility(View.GONE);
                items.clear();
                items.addAll(docs);
                adapter.notifyDataSetChanged();
                if (items.isEmpty()) {
                    rvCompartidos.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                    tvEmptyMsg.setText(mostrandoRecibidos ?
                            "No has recibido outfits aún" : "No has enviado outfits aún");
                } else {
                    rvCompartidos.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(String error) {
                progressCompartidos.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                tvEmptyMsg.setText("Error cargando datos");
            }
        };

        if (mostrandoRecibidos) {
            CouchDbHelper.getOutfitsRecibidos(miEmail, callback);
        } else {
            CouchDbHelper.getOutfitsEnviados(miEmail, callback);
        }
    }

    class CompartidosAdapter extends RecyclerView.Adapter<CompartidosAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_outfit_recibido, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            try {
                JSONObject item = items.get(position);
                String contraparte = mostrandoRecibidos ?
                        "De: " + item.optString("de", "Alguien") :
                        "Para: " + item.optString("para", "Alguien");
                String mensaje = item.optString("mensaje", "");
                String imagenUrl = item.optString("imagen_url", "");
                String fechaMs = item.optString("fecha", "");

                Glide.with(CompartidosActivity.this).load(imagenUrl).into(holder.ivOutfit);
                holder.tvDe.setText(contraparte);
                holder.tvMensaje.setText(mensaje);

                try {
                    long millis = Long.parseLong(fechaMs);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    holder.tvFecha.setText(sdf.format(new Date(millis)));
                } catch (Exception e) {
                    holder.tvFecha.setText("");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() { return items.size(); }

        class VH extends RecyclerView.ViewHolder {
            ImageView ivOutfit;
            TextView tvDe, tvMensaje, tvFecha;
            VH(View v) {
                super(v);
                ivOutfit = v.findViewById(R.id.ivOutfitRecibido);
                tvDe = v.findViewById(R.id.tvOutfitDe);
                tvMensaje = v.findViewById(R.id.tvOutfitMensaje);
                tvFecha = v.findViewById(R.id.tvOutfitFecha);
            }
        }
    }
}