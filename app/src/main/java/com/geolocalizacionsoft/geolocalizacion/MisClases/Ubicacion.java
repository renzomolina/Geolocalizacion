package com.geolocalizacionsoft.geolocalizacion.MisClases;

public class Ubicacion {
    private String titulo;
    private String descripcion;
    private String latitud;
    private String longitud;
    private byte[] imagen;

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
    public byte[] getImagen() {
        return imagen;
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
    public void setImagen(byte[] imagen) {
        this.imagen = imagen;
    }
}
