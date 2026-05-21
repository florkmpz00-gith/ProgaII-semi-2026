package com.example.auramoda;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.auramoda.api.PexelsHelper;
import java.util.ArrayList;
import java.util.List;

public class GaleriaActivity extends AppCompatActivity {

    RecyclerView rvGaleria;
    ProgressBar progressGaleria;
    GaleriaAdapter adapter;
    List<String> imageUrls = new ArrayList<>();
    String filtroActual = "fashion outfit";

    TextView filterTodos, filterCasual, filterElegante, filterUrbano, filterFormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_galeria);

        findViewById(R.id.tvBack).setOnClickListener(v -> finish());

        rvGaleria = findViewById(R.id.rvGaleria);
        progressGaleria = findViewById(R.id.progressGaleria);
        filterTodos = findViewById(R.id.filterTodos);
        filterCasual = findViewById(R.id.filterCasual);
        filterElegante = findViewById(R.id.filterElegante);
        filterUrbano = findViewById(R.id.filterUrbano);
        filterFormal = findViewById(R.id.filterFormal);

        rvGaleria.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GaleriaAdapter(this, imageUrls, url -> {
            Intent intent = new Intent(this, DetalleOutfitActivity.class);
            intent.putExtra("image_url", url);
            intent.putExtra("estilo", filtroActual);
            startActivity(intent);
        });
        rvGaleria.setAdapter(adapter);

        filterTodos.setOnClickListener(v -> {
            filtroActual = "fashion outfit";
            actualizarFiltros(filterTodos);
            buscar("woman fashion model outfit white background");
        });
        filterCasual.setOnClickListener(v -> {
            filtroActual = "casual outfit";
            actualizarFiltros(filterCasual);
            buscar("woman casual style outfit lookbook");
        });
        filterElegante.setOnClickListener(v -> {
            filtroActual = "elegant fashion";
            actualizarFiltros(filterElegante);
            buscar("woman elegant dress fashion model studio");
        });
        filterUrbano.setOnClickListener(v -> {
            filtroActual = "urban style outfit";
            actualizarFiltros(filterUrbano);
            buscar("woman streetwear urban fashion lookbook");
        });
        filterFormal.setOnClickListener(v -> {
            filtroActual = "formal wear fashion";
            actualizarFiltros(filterFormal);
            buscar("woman formal business outfit model");
        });

        actualizarFiltros(filterTodos);
        buscar("woman fashion model outfit white background");
    }

    void actualizarFiltros(TextView filtroActivo) {
        TextView[] filtros = {filterTodos, filterCasual, filterElegante, filterUrbano, filterFormal};
        for (TextView f : filtros) {
            f.setBackgroundResource(R.drawable.bg_input);
            f.setTextColor(getResources().getColor(R.color.gray_muted));
        }
        filtroActivo.setBackgroundResource(R.drawable.bg_chip_active);
        filtroActivo.setTextColor(getResources().getColor(R.color.black_primary));
    }

    void buscar(String query) {
        progressGaleria.setVisibility(View.VISIBLE);
        PexelsHelper.buscarFotos(query, new PexelsHelper.PexelsCallback() {
            @Override
            public void onSuccess(List<String> urls) {
                progressGaleria.setVisibility(View.GONE);
                imageUrls.clear();
                imageUrls.addAll(urls);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                progressGaleria.setVisibility(View.GONE);
                Toast.makeText(GaleriaActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}