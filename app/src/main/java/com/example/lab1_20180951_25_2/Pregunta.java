package com.example.lab1_20180951_25_2;

import java.util.List;

public class Pregunta {
    private String enunciado;
    private List<String> opciones;
    private int indiceCorrecto;
    private String pista;

    public Pregunta() {} // necesario para Gson

    // Getters
    public String getEnunciado() { return enunciado; }
    public List<String> getOpciones() { return opciones; }
    public int getIndiceCorrecto() { return indiceCorrecto; }
    public String getPista() { return pista; }
}
