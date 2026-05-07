package com.example.auramoda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.auramoda.R;
import java.util.List;

public class GaleriaAdapter extends RecyclerView.Adapter<GaleriaAdapter.ViewHolder> {

    Context context;
    List<String> imageUrls;
    OnItemClickListener listener;

    public interface OnItemClickListener {
        void onFavoritoClick(String url);
    }

    public GaleriaAdapter(Context context, List<String> imageUrls, OnItemClickListener listener) {
        this.context = context;
        this.imageUrls = imageUrls;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_galeria, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String url = imageUrls.get(position);
        Glide.with(context).load(url).into(holder.ivFoto);
        holder.ivFavorito.setOnClickListener(v -> listener.onFavoritoClick(url));
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto, ivFavorito;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivFoto);
            ivFavorito = itemView.findViewById(R.id.ivFavorito);
        }
    }
}