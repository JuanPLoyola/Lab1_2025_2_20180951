package com.example.lab1_20180951_25_2;

import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.lab1_20180951_25_2.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;  // ViewBinding

    public static ArrayList<Partida> historialPartidas = new ArrayList<>();
    public static String nombreJugador = "";
    public static String fechaInicio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnIngresar.setOnClickListener(v -> {
            String nombre = binding.etNombre.getText().toString().trim();

            if (nombre.isEmpty()) {
                Toast.makeText(this, "Ingresa tu nombre", Toast.LENGTH_SHORT).show();
                return;
            }

            // Guardar la fecha solo la primera vez
            if (fechaInicio.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
                fechaInicio = sdf.format(new Date());
            }

            // Guardar en SharedPreferences (clave CORRECTA)
            getSharedPreferences("juego", MODE_PRIVATE)
                    .edit()
                    .putString("nombre", nombre)
                    .putString("inicio", fechaInicio)
                    .apply();

            nombreJugador = nombre;  // si quieres seguir usando la variable también

            Intent intent = new Intent(this, TopicSelectionActivity.class);
            intent.putExtra("nombreUsuario", nombre);
            startActivity(intent);
        });

    }
}
