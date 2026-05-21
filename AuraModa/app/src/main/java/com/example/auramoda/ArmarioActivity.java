package com.example.auramoda;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ArmarioActivity extends AppCompatActivity {

    RecyclerView rvPrendas;
    LinearLayout layoutEmpty, layoutSugerencia;
    TextView tvSugerencia, btnAgregar;
    ProgressBar progressSugerencia;
    DatabaseHelper db;
    PrendasAdapter adapter;
    List<PrendaItem> prendas = new ArrayList<>();
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_armario);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        db = new DatabaseHelper(this);
        prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);

        rvPrendas = findViewById(R.id.rvPrendas);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        layoutSugerencia = findViewById(R.id.layoutSugerencia);
        tvSugerencia = findViewById(R.id.tvSugerencia);
        btnAgregar = findViewById(R.id.btnAgregar);
        progressSugerencia = findViewById(R.id.progressSugerencia);

        rvPrendas.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PrendasAdapter();
        rvPrendas.setAdapter(adapter);

        btnAgregar.setOnClickListener(v ->
                startActivity(new Intent(this, AgregarPrendaActivity.class)));

        cargarPrendas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPrendas();
    }

    void cargarPrendas() {
        prendas.clear();
        Cursor cursor = db.getPrendas();
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow("tipo"));
                String color = cursor.getString(cursor.getColumnIndexOrThrow("color"));
                prendas.add(new PrendaItem(id, nombre, tipo, color));
            } while (cursor.moveToNext());
        }
        cursor.close();

        if (prendas.isEmpty()) {
            rvPrendas.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            layoutSugerencia.setVisibility(View.GONE);
        } else {
            rvPrendas.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            layoutSugerencia.setVisibility(View.VISIBLE);
            adapter.notifyDataSetChanged();
            generarSugerencia();
        }
    }

    void generarSugerencia() {
        String fechaHoy = java.text.DateFormat.getDateInstance().format(new java.util.Date());
        String semanaGuardada = prefs.getString("armario_semana", "");
        String sugerenciaGuardada = prefs.getString("armario_sugerencia", "");

        // Obtener numero de semana
        java.util.Calendar cal = java.util.Calendar.getInstance();
        String semanaActual = cal.get(java.util.Calendar.YEAR) + "-" + cal.get(java.util.Calendar.WEEK_OF_YEAR);

        if (semanaActual.equals(semanaGuardada) && !sugerenciaGuardada.isEmpty()) {
            tvSugerencia.setText(sugerenciaGuardada);
            return;
        }

        progressSugerencia.setVisibility(View.VISIBLE);
        tvSugerencia.setVisibility(View.GONE);

        String prendasTexto = db.getPrendasTexto();
        String userName = prefs.getString("user_name", "");
        String userEstilos = prefs.getString("user_estilos", "casual");

        GeminiHelper.preguntar(
                "Tengo estas prendas en mi armario: " + prendasTexto +
                        " Dame 3 combinaciones de outfits diferentes y creativos usando solo estas prendas. " +
                        "Formato: Outfit 1: [prendas], Outfit 2: [prendas], Outfit 3: [prendas]. Muy corto.",
                userName, userEstilos,
                new GeminiHelper.GeminiCallback() {
                    @Override
                    public void onRespuesta(String respuesta) {
                        progressSugerencia.setVisibility(View.GONE);
                        tvSugerencia.setVisibility(View.VISIBLE);
                        tvSugerencia.setText(respuesta);
                        prefs.edit()
                                .putString("armario_sugerencia", respuesta)
                                .putString("armario_semana", semanaActual)
                                .apply();
                    }
                    @Override
                    public void onError(String error) {
                        progressSugerencia.setVisibility(View.GONE);
                        tvSugerencia.setVisibility(View.VISIBLE);
                        tvSugerencia.setText("Agrega más prendas para recibir sugerencias.");
                    }
                });
    }

    class PrendasAdapter extends RecyclerView.Adapter<PrendasAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_prenda, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            PrendaItem item = prendas.get(position);
            holder.tvNombre.setText(item.nombre);
            holder.tvTipo.setText(item.tipo);
            holder.tvColor.setText(item.color);
            holder.btnEliminar.setOnClickListener(v -> {
                db.eliminarPrenda(item.id);
                prendas.remove(position);
                notifyItemRemoved(position);
                if (prendas.isEmpty()) {
                    rvPrendas.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                    layoutSugerencia.setVisibility(View.GONE);
                }
            });
        }

        @Override
        public int getItemCount() { return prendas.size(); }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvNombre, tvTipo, tvColor, btnEliminar;
            ViewHolder(View itemView) {
                super(itemView);
                tvNombre = itemView.findViewById(R.id.tvPrendaNombre);
                tvTipo = itemView.findViewById(R.id.tvPrendaTipo);
                tvColor = itemView.findViewById(R.id.tvPrendaColor);
                btnEliminar = itemView.findViewById(R.id.btnEliminarPrenda);
            }
        }
    }

    static class PrendaItem {
        int id;
        String nombre, tipo, color;
        PrendaItem(int id, String nombre, String tipo, String color) {
            this.id = id;
            this.nombre = nombre;
            this.tipo = tipo;
            this.color = color;
        }
    }
}