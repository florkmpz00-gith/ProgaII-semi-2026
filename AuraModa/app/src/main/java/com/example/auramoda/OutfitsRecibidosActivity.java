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

public class OutfitsRecibidosActivity extends AppCompatActivity {

    RecyclerView rvOutfitsRecibidos;
    ProgressBar progressRecibidos;
    LinearLayout layoutEmpty;
    List<JSONObject> outfits = new ArrayList<>();
    OutfitsAdapter adapter;
    String miEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outfits_recibidos);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        miEmail = prefs.getString("user_email", "");

        rvOutfitsRecibidos = findViewById(R.id.rvOutfitsRecibidos);
        progressRecibidos = findViewById(R.id.progressRecibidos);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        adapter = new OutfitsAdapter();
        rvOutfitsRecibidos.setLayoutManager(new LinearLayoutManager(this));
        rvOutfitsRecibidos.setAdapter(adapter);

        cargarOutfits();
    }

    void cargarOutfits() {
        progressRecibidos.setVisibility(View.VISIBLE);
        CouchDbHelper.getOutfitsRecibidos(miEmail, new CouchDbHelper.CouchListCallback() {
            @Override
            public void onSuccess(List<JSONObject> docs) {
                progressRecibidos.setVisibility(View.GONE);
                outfits.clear();
                outfits.addAll(docs);
                adapter.notifyDataSetChanged();
                if (outfits.isEmpty()) {
                    rvOutfitsRecibidos.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                } else {
                    rvOutfitsRecibidos.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                }
            }
            @Override
            public void onError(String error) {
                progressRecibidos.setVisibility(View.GONE);
            }
        });
    }

    class OutfitsAdapter extends RecyclerView.Adapter<OutfitsAdapter.VH> {

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
                JSONObject outfit = outfits.get(position);
                String de = outfit.optString("de", "Alguien");
                String mensaje = outfit.optString("mensaje", "");
                String imagenUrl = outfit.optString("imagen_url", "");
                String fechaMs = outfit.optString("fecha", "");

                Glide.with(OutfitsRecibidosActivity.this).load(imagenUrl).into(holder.ivOutfit);
                holder.tvDe.setText("De: " + de);
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
        public int getItemCount() { return outfits.size(); }

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