package com.example.auramoda;

public class ChatMensaje {
    public String texto;
    public boolean esUsuario;

    public ChatMensaje(String texto, boolean esUsuario) {
        this.texto = texto;
        this.esUsuario = esUsuario;
    }
}