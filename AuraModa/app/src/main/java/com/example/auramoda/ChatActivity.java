package com.example.auramoda;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    RecyclerView rvChat;
    EditText etMensaje;
    TextView btnEnviar;
    ChatAdapter adapter;
    List<ChatMensaje> mensajes = new ArrayList<>();
    String userName, userEstilos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences prefs = getSharedPreferences("AuraModa", MODE_PRIVATE);
        userName = prefs.getString("user_name", "");
        userEstilos = prefs.getString("user_estilos", "casual");

        rvChat = findViewById(R.id.rvChat);
        etMensaje = findViewById(R.id.etMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        adapter = new ChatAdapter(mensajes);
        rvChat.setLayoutManager(new LinearLayoutManager(this));
        rvChat.setAdapter(adapter);

        // Mensaje de bienvenida
        agregarMensajeIA("¡Hola " + userName + "! Soy tu estilista personal. ¿Para qué ocasión necesitas un outfit hoy?");

        btnEnviar.setOnClickListener(v -> {
            String texto = etMensaje.getText().toString().trim();
            if (texto.isEmpty()) return;

            agregarMensajeUsuario(texto);
            etMensaje.setText("");

            agregarMensajeIA("...");
            int posLoading = mensajes.size() - 1;

            GeminiHelper.preguntar(texto, userName, userEstilos, new GeminiHelper.GeminiCallback() {
                @Override
                public void onRespuesta(String respuesta) {
                    mensajes.set(posLoading, new ChatMensaje(respuesta, false));
                    adapter.notifyItemChanged(posLoading);
                    rvChat.scrollToPosition(mensajes.size() - 1);
                }

                @Override
                public void onError(String error) {
                    mensajes.set(posLoading, new ChatMensaje("Lo siento, hubo un error. Intenta de nuevo.", false));
                    adapter.notifyItemChanged(posLoading);
                }
            });
        });
    }

    void agregarMensajeUsuario(String texto) {
        mensajes.add(new ChatMensaje(texto, true));
        adapter.notifyItemInserted(mensajes.size() - 1);
        rvChat.scrollToPosition(mensajes.size() - 1);
    }

    void agregarMensajeIA(String texto) {
        mensajes.add(new ChatMensaje(texto, false));
        adapter.notifyItemInserted(mensajes.size() - 1);
        rvChat.scrollToPosition(mensajes.size() - 1);
    }
}