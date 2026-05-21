package com.example.auramoda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class GaleriaAdapter extends RecyclerView.Adapter<GaleriaAdapter.ViewHolder> {

    private Context context;
    private List<String> imageUrls;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(String imageUrl);
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
        Glide.with(context)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.bg_avatar)
                .into(holder.ivFoto);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(url);
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    public void actualizarLista(List<String> nuevasUrls) {
        this.imageUrls = nuevasUrls;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        ImageView ivFavorito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivFoto);
            ivFavorito = itemView.findViewById(R.id.ivFavorito);
        }
    }
}