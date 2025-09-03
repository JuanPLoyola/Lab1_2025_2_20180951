package com.example.lab1_20180951_25_2;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.lab1_20180951_25_2.databinding.ActivityQuizBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {
    private long tiempoInicio; // para medir duración
    private int partidaActualIndex; // para guardar en el historial

    private ActivityQuizBinding binding;
    private List<Pregunta> preguntas;
    private int preguntaActual = 0;
    private int puntaje = 0;
    private int racha = 0;
    private List<Integer> respuestasUsuario = new ArrayList<>(); // -1 = no respondida

    private int pistasUsadas = 0;
    private static final int MAX_PISTAS = 3;
    private List<Integer> preguntasConPista = new ArrayList<>();

    private List<Boolean> preguntasValidadas = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String temaSeleccionado = getIntent().getStringExtra("tema");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String hora = sdf.format(new Date());

        Partida nuevaPartida = new Partida(temaSeleccionado, hora);
        MainActivity.historialPartidas.add(nuevaPartida);
        partidaActualIndex = MainActivity.historialPartidas.size() - 1;

// Guardar hora de inicio
        tiempoInicio = System.currentTimeMillis();

        // Simulamos preguntas precargadas (luego podrías cargarlas desde archivos o Firestore)
        preguntas = obtenerPreguntasDesdeJson(temaSeleccionado);

        for (int i = 0; i < preguntas.size(); i++) {
            respuestasUsuario.add(-1); // sin responder
        }


        for (int i = 0; i < preguntas.size(); i++) {
            preguntasValidadas.add(false); // ninguna validada aún
        }

        mostrarPregunta();

        binding.btnSiguiente.setOnClickListener(v -> {
            if (respuestaSeleccionada()) {
                validarRespuesta();
                if (preguntaActual < preguntas.size() - 1) {
                    preguntaActual++;
                    mostrarPregunta();
                } else {
                    getSharedPreferences("juego", MODE_PRIVATE)
                            .edit()
                            .putInt("puntajeFinal", puntaje)
                            .apply();

                    long tiempoFin = System.currentTimeMillis();
                    long duracion = (tiempoFin - tiempoInicio) / 1000;

                    MainActivity.historialPartidas.get(partidaActualIndex)
                            .marcarTerminada(puntaje, duracion);

                    Intent intent = new Intent(this, ResultActivity.class);
                    intent.putExtra("puntajeFinal", puntaje);
                    intent.putExtra("tema", "Microondas"); // Puedes reemplazar con temática real
                    startActivity(intent);
                    finish(); // Cierra QuizActivity para que no vuelva al presionar "atrás"


                }
            } else {
                Toast.makeText(this, "Debes seleccionar una respuesta", Toast.LENGTH_SHORT).show();
            }
        });

        binding.btnAnterior.setOnClickListener(v -> {
            if (preguntaActual > 0) {
                preguntaActual--;
                mostrarPregunta();
            }
        });

        binding.btnPista.setOnClickListener(v -> {
            if (pistasUsadas >= MAX_PISTAS) {
                Toast.makeText(this, "Ya usaste todas tus pistas.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (preguntasConPista.contains(preguntaActual)) {
                Toast.makeText(this, "Ya usaste pista en esta pregunta.", Toast.LENGTH_SHORT).show();
                return;
            }

            Pregunta pregunta = preguntas.get(preguntaActual);
            int correctaIndex = pregunta.getIndiceCorrecto();

            // Buscar una opción incorrecta al azar
            List<Integer> incorrectos = new ArrayList<>();
            for (int i = 0; i < binding.rgOpciones.getChildCount(); i++) {
                RadioButton rb = (RadioButton) binding.rgOpciones.getChildAt(i);
                String texto = rb.getText().toString();
                if (!texto.equals(pregunta.getOpciones().get(correctaIndex))) {
                    incorrectos.add(i);
                }
            }

            if (!incorrectos.isEmpty()) {
                // Eliminar una opción incorrecta
                int indexAEliminar = incorrectos.get((int) (Math.random() * incorrectos.size()));
                RadioButton eliminar = (RadioButton) binding.rgOpciones.getChildAt(indexAEliminar);
                eliminar.setEnabled(false);
                eliminar.setAlpha(0.5f); // visualmente más claro
            }

            pistasUsadas++;
            preguntasConPista.add(preguntaActual);

            Toast.makeText(this, "Pista usada. Se eliminó una opción incorrecta.", Toast.LENGTH_SHORT).show();
        });

    }

    private void mostrarPregunta() {
        Pregunta p = preguntas.get(preguntaActual);
        binding.tvPregunta.setText(p.getEnunciado());
        binding.tvTituloTema.setText("Tema: Redes"); // O pasa la temática por Intent

        // Limpiar opciones anteriores
        binding.rgOpciones.removeAllViews();

        // Barajar opciones
        List<String> opciones = new ArrayList<>(p.getOpciones());
        Collections.shuffle(opciones);

        for (int i = 0; i < opciones.size(); i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(opciones.get(i));
            rb.setId(i);
            binding.rgOpciones.addView(rb);

            // Ya fue respondida
            int seleccion = respuestasUsuario.get(preguntaActual);

            if (seleccion != -1) {
                String respuestaUsuario = preguntas.get(preguntaActual).getOpciones().get(seleccion);
                String opcionActual = rb.getText().toString();
                String opcionCorrecta = preguntas.get(preguntaActual).getOpciones().get(preguntas.get(preguntaActual).getIndiceCorrecto());

                // Bloquea para no cambiar respuesta
                rb.setEnabled(false);

                // Pinta la respuesta correcta en verde
                if (opcionActual.equals(opcionCorrecta)) {
                    rb.setBackgroundColor(Color.parseColor("#C8E6C9")); // verde claro
                }

                // Si esta fue la seleccionada por el usuario
                if (opcionActual.equals(respuestaUsuario)) {
                    rb.setChecked(true);

                    // Si fue incorrecta, marcar en rojo
                    if (!respuestaUsuario.equals(opcionCorrecta)) {
                        rb.setBackgroundColor(Color.parseColor("#FFCDD2")); // rojo claro
                    }
                }
            }
        }


        binding.tvPuntaje.setText("Puntaje: " + puntaje);
    }

    private boolean respuestaSeleccionada() {
        return binding.rgOpciones.getCheckedRadioButtonId() != -1;
    }

    private void validarRespuesta() {

        // Verificar si ya fue validada
        if (preguntasValidadas.get(preguntaActual)) {
            return;
        }

        int seleccionId = binding.rgOpciones.getCheckedRadioButtonId();
        RadioButton seleccionada = findViewById(seleccionId);
        String textoSeleccionado = seleccionada.getText().toString();

        Pregunta p = preguntas.get(preguntaActual);
        int indiceCorrecto = p.getIndiceCorrecto();
        String opcionCorrecta = p.getOpciones().get(indiceCorrecto);

        // Guardar selección del usuario
        for (int i = 0; i < p.getOpciones().size(); i++) {
            if (p.getOpciones().get(i).equals(textoSeleccionado)) {
                respuestasUsuario.set(preguntaActual, i);
                break;
            }
        }

        if (textoSeleccionado.equals(opcionCorrecta)) {
            racha++;
            int puntosGanados = (int) Math.pow(2, racha);
            puntaje += puntosGanados;
            Toast.makeText(this, "¡Correcto! +" + puntosGanados + " puntos", Toast.LENGTH_SHORT).show();
        } else {
            racha = 0; // reinicia racha
            puntaje -= 2;
            Toast.makeText(this, "Incorrecto. Respuesta correcta: " + opcionCorrecta + "\n-2 puntos", Toast.LENGTH_SHORT).show();
        }

        // Marcar como validada
        preguntasValidadas.set(preguntaActual, true);
    }


    private List<Pregunta> obtenerPreguntasDesdeJson(String tema) {
        try {
            InputStream is = getResources().openRawResource(R.raw.preguntas);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder jsonBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBuilder.append(line);
            }

            String json = jsonBuilder.toString();
            Gson gson = new Gson();
            Type tipoMapa = new TypeToken<Map<String, List<Pregunta>>>() {}.getType();
            Map<String, List<Pregunta>> mapaPreguntas = gson.fromJson(json, tipoMapa);

            return mapaPreguntas.getOrDefault(tema, new ArrayList<>());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al cargar preguntas", Toast.LENGTH_SHORT).show();
            return new ArrayList<>();
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("¿Cancelar partida?")
                .setMessage("¿Estás seguro de volver? Esta partida contará como cancelada.")
                .setPositiveButton("Sí", (dialog, which) -> {
                    MainActivity.historialPartidas.get(partidaActualIndex).marcarCancelada();
                    finish(); // vuelve al tema
                })
                .setNegativeButton("No", null)
                .show();
    }


}
