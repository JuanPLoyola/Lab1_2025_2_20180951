package com.example.lab1_20180951_25_2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab1_20180951_25_2.databinding.ActivityResultBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private ActivityResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtener datos enviados
        int puntaje = getIntent().getIntExtra("puntajeFinal", 0);
        String tema = getIntent().getStringExtra("tema");

        // Mostrar el puntaje en pantalla
        binding.tvPuntajeNumero.setText(String.valueOf(puntaje));
        binding.tvTema.setText(tema);

        // Cambiar color de fondo
        if (puntaje >= 0) {
            binding.tvPuntajeNumero.setBackgroundColor(Color.parseColor("#A5D6A7")); // Verde suave
        } else {
            binding.tvPuntajeNumero.setBackgroundColor(Color.parseColor("#EF9A9A")); // Rojo suave
        }

        // Registrar partida terminada en historial
        guardarPartidaEnHistorial(tema, puntaje);

        // Botón volver a jugar
        binding.btnVolverAJugar.setOnClickListener(v -> {
            Intent intent = new Intent(this, TopicSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        // Botón volver atrás
        binding.btnAnterior.setOnClickListener(v -> finish());
    }

    private void guardarPartidaEnHistorial(String tema, int puntaje) {
        SharedPreferences prefs = getSharedPreferences("juego", MODE_PRIVATE);
        Gson gson = new Gson();

        // Obtener hora de inicio y duración
        String horaInicio = prefs.getString("horaInicioPartida", "");
        long inicioMillis = prefs.getLong("inicioMillisPartida", System.currentTimeMillis());
        long duracion = System.currentTimeMillis() - inicioMillis;

        // Obtener historial anterior
        String jsonHistorial = prefs.getString("historial", "[]");
        Type type = new TypeToken<List<Partida>>() {}.getType();
        List<Partida> historial = gson.fromJson(jsonHistorial, type);

        // Crear nueva partida
        Partida partida = new Partida(tema, horaInicio);
        partida.marcarTerminada(puntaje, duracion);

        // Agregar y guardar
        historial.add(partida);
        prefs.edit().putString("historial", gson.toJson(historial)).apply();
    }
}
