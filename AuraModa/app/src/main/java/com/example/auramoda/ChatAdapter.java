package com.example.auramoda;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TIPO_USUARIO = 0;
    private static final int TIPO_IA = 1;

    List<ChatMensaje> mensajes;

    public ChatAdapter(List<ChatMensaje> mensajes) {
        this.mensajes = mensajes;
    }

    @Override
    public int getItemViewType(int position) {
        return mensajes.get(position).esUsuario ? TIPO_USUARIO : TIPO_IA;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TIPO_USUARIO) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_usuario, parent, false);
            return new ViewHolderUsuario(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_mensaje_ia, parent, false);
            return new ViewHolderIA(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMensaje mensaje = mensajes.get(position);
        if (holder instanceof ViewHolderUsuario) {
            ((ViewHolderUsuario) holder).tvMensaje.setText(mensaje.texto);
        } else {
            ((ViewHolderIA) holder).tvMensaje.setText(mensaje.texto);
        }
    }

    @Override
    public int getItemCount() {
        return mensajes.size();
    }

    static class ViewHolderUsuario extends RecyclerView.ViewHolder {
        TextView tvMensaje;
        ViewHolderUsuario(View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
        }
    }

    static class ViewHolderIA extends RecyclerView.ViewHolder {
        TextView tvMensaje;
        ViewHolderIA(View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensaje);
        }
    }
}