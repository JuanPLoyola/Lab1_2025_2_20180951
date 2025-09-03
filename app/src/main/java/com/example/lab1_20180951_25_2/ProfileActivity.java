package com.example.lab1_20180951_25_2;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import com.example.lab1_20180951_25_2.databinding.ActivityProfileBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private SharedPreferences prefs;
    private List<Partida> historialList = new ArrayList<>(); // ✅ CORRECTO: ahora es List<Partida>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        prefs = getSharedPreferences("juego", MODE_PRIVATE);

        // Mostrar nombre, inicio, partidas
        String nombre = prefs.getString("nombre", "Sin nombre");
        String inicio = prefs.getString("inicio", "Sin fecha");
        String historialRaw = prefs.getString("historial", "[]");

        // ✅ Usamos GSON para convertir el JSON en List<Partida>
        Gson gson = new Gson();
        Type type = new TypeToken<List<Partida>>() {}.getType();
        historialList = gson.fromJson(historialRaw, type); // ✅ Guardamos en la variable global

        binding.tvNombreJugador.setText("Jugador: " + nombre);
        binding.tvInicio.setText("Inicio: " + inicio);
        binding.tvCantidadPartidas.setText("Cantidad de Partidas: " + historialList.size());

        mostrarHistorial(); // ✅ Mostrar el historial como texto
    }

    private void mostrarHistorial() {
        binding.contenedorHistorial.removeAllViews();

        for (int i = 0; i < historialList.size(); i++) {
            Partida partida = historialList.get(i); // ✅ usamos objeto Partida
            TextView tv = new TextView(this);
            tv.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            tv.setTextSize(16f);

            // Construir texto de entrada
            String entrada = "Juego " + (i + 1) + ": " + partida.getCategoria();

            if (partida.isCancelada()) {
                entrada += " | Canceló";
                tv.setTextColor(Color.GRAY);
            } else if (partida.isTerminada()) {
                entrada += " | Tiempo: " + partida.getTiempoDuracion() + "s | Puntaje: " + partida.getPuntaje();
                tv.setTextColor(partida.getPuntaje() >= 0 ? Color.parseColor("#388E3C") : Color.RED);
            } else {
                entrada += " | Inicio: " + partida.getHoraInicio() + " | En curso";
                tv.setTextColor(Color.rgb(255, 140, 0)); // naranja
            }

            tv.setText(entrada);
            binding.contenedorHistorial.addView(tv);
        }
    }
}
