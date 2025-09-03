package com.example.lab1_20180951_25_2;

public class Partida {
    private String categoria;
    private int puntaje;
    private long tiempoDuracion;
    private boolean terminada;
    private boolean cancelada;
    private String horaInicio;

    public Partida(String categoria, String horaInicio) {
        this.categoria = categoria;
        this.horaInicio = horaInicio;
        this.puntaje = 0;
        this.tiempoDuracion = 0;
        this.terminada = false;
        this.cancelada = false;
    }

    public void marcarTerminada(int puntaje, long duracion) {
        this.puntaje = puntaje;
        this.tiempoDuracion = duracion;
        this.terminada = true;
        this.cancelada = false;
    }

    public void marcarCancelada() {
        this.cancelada = true;
        this.terminada = false;
    }

    public boolean estaEnCurso() {
        return !terminada && !cancelada;
    }

    // Getters

    public String getCategoria() { return categoria; }
    public int getPuntaje() { return puntaje; }
    public long getTiempoDuracion() { return tiempoDuracion; }
    public boolean isTerminada() { return terminada; }
    public boolean isCancelada() { return cancelada; }
    public String getHoraInicio() { return horaInicio; }
}
