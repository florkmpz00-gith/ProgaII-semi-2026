package com.example.auramoda;

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

        rvGaleria = findViewById(R.id.rvGaleria);
        progressGaleria = findViewById(R.id.progressGaleria);
        filterTodos = findViewById(R.id.filterTodos);
        filterCasual = findViewById(R.id.filterCasual);
        filterElegante = findViewById(R.id.filterElegante);
        filterUrbano = findViewById(R.id.filterUrbano);
        filterFormal = findViewById(R.id.filterFormal);

        rvGaleria.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new GaleriaAdapter(this, imageUrls, url -> {
            Toast.makeText(this, "Guardado en favoritos!", Toast.LENGTH_SHORT).show();
        });
        rvGaleria.setAdapter(adapter);

        // Filtros
        filterTodos.setOnClickListener(v -> buscar("fashion outfit"));
        filterCasual.setOnClickListener(v -> buscar("casual outfit"));
        filterElegante.setOnClickListener(v -> buscar("elegant fashion"));
        filterUrbano.setOnClickListener(v -> buscar("urban style outfit"));
        filterFormal.setOnClickListener(v -> buscar("formal wear fashion"));

        buscar(filtroActual);
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