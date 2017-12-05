package com.geolocalizacionsoft.geolocalizacion.MisClases;

import com.google.android.gms.maps.model.Circle;

public class Ubicacion {
    private String titulo;
    private String descripcion;
    private String latitud;
    private String longitud;

    public String getTitulo() {
        return titulo;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public String getLatitud() {
        return latitud;
    }
    public String getLongitud() {
        return longitud;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public void setLatitud(String posicion) {
        this.latitud = posicion;
    }
    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
