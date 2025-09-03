package com.example.lab1_20180951_25_2;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import com.example.lab1_20180951_25_2.databinding.ActivityTopicSelectionBinding;

public class TopicSelectionActivity extends AppCompatActivity {

    ActivityTopicSelectionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTopicSelectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String nombre = getIntent().getStringExtra("nombreUsuario");
        binding.tvBienvenida.setText("¡Hola " + nombre + "! Elige una temática:");

        // Configurar AppBar
        setSupportActionBar(binding.topAppBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ← botón atrás


        binding.btnMicroondas.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("tema", "Microondas");
            startActivity(intent);
        });

        binding.btnRedes.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("tema", "Redes");
            startActivity(intent);
        });

        binding.btnCiberseguridad.setOnClickListener(v -> {
            Intent intent = new Intent(this, QuizActivity.class);
            intent.putExtra("tema", "Ciberseguridad");
            startActivity(intent);
        });




    }



    // Inflar el menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar_menu, menu);
        return true;
    }

    // Acciones del menú
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Botón back
            finish(); // ← vuelve a MainActivity
            return true;
        } else if (id == R.id.action_profile) {
            // Botón Ver Perfil
            Intent intent = new Intent(this, ProfileActivity.class); // la creamos en el siguiente paso
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }



}
