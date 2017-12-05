package com.geolocalizacionsoft.geolocalizacion.MisClases;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;


public class Lugares extends AppCompatActivity{


    private String nombre;
    private String descripcion;
    private Bitmap imagen;
    //Agregar direccion


    public Lugares(String nombre, String descripcion, Bitmap imagen) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }
    public Lugares(){}

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Bitmap getImagen() {
        return imagen;
    }

}