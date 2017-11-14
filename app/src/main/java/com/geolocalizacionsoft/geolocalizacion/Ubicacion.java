package com.geolocalizacionsoft.geolocalizacion;

public class Ubicacion {
    private String titulo;
    private String descripcion;
    private String posicion;

    public String getTitulo() {
        return titulo;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public String getPosicion() {
        return posicion;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setPosicion(String posicion) {
        this.posicion = posicion;
    }
}
