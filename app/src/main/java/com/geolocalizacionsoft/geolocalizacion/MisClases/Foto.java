package com.geolocalizacionsoft.geolocalizacion.MisClases;

import android.graphics.Bitmap;

public class Foto {
    private String name,description;
    private Bitmap image;


    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Bitmap getImage() {
        return image;
    }
}