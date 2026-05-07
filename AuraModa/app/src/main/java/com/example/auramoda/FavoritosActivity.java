package com.example.auramoda;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class FavoritosActivity extends AppCompatActivity {

    RecyclerView rvFavoritos;
    LinearLayout layoutEmpty;
    DatabaseHelper db;
    FavoritosAdapter adapter;
    List<FavoritoItem> favoritos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favoritos);

        db = new DatabaseHelper(this);
        rvFavoritos = findViewById(R.id.rvFavoritos);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        rvFavoritos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoritosAdapter();
        rvFavoritos.setAdapter(adapter);

        cargarFavoritos();
    }

    void cargarFavoritos() {
        favoritos.clear();
        Cursor cursor = db.getFavoritos();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"));
                favoritos.add(new FavoritoItem(id, url, tipo, fecha));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (favoritos.isEmpty()) {
            rvFavoritos.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvFavoritos.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }

    class FavoritosAdapter extends RecyclerView.Adapter<FavoritosAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_favoritos, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            FavoritoItem item = favoritos.get(position);
            Glide.with(FavoritosActivity.this).load(item.url).into(holder.ivFoto);
            holder.tvTipo.setText(item.tipo != null ? item.tipo : "Outfit");

            try {
                long millis = Long.parseLong(item.fecha);
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                holder.tvFecha.setText("Guardado el " + sdf.format(new Date(millis)));
            } catch (Exception e) {
                holder.tvFecha.setText("Guardado recientemente");
            }

            holder.btnEliminar.setOnClickListener(v -> {
                db.eliminarFavorito(item.id);
                favoritos.remove(position);
                notifyItemRemoved(position);
                if (favoritos.isEmpty()) {
                    rvFavoritos.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            });
        }

        @Override
        public int getItemCount() {
            return favoritos.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivFoto;
            TextView tvTipo, tvFecha, btnEliminar;
            ViewHolder(View itemView) {
                super(itemView);
                ivFoto = itemView.findViewById(R.id.ivFavFoto);
                tvTipo = itemView.findViewById(R.id.tvFavTipo);
                tvFecha = itemView.findViewById(R.id.tvFavFecha);
                btnEliminar = itemView.findViewById(R.id.btnEliminar);
            }
        }
    }

    static class FavoritoItem {
        int id;
        String url, tipo, fecha;
        FavoritoItem(int id, String url, String tipo, String fecha) {
            this.id = id;
            this.url = url;
            this.tipo = tipo;
            this.fecha = fecha;
        }
    }
}