package com.geolocalizacionsoft.geolocalizacion.MisClases;


import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.WindowManager;

import com.geolocalizacionsoft.geolocalizacion.BaseDeDatos.BaseApplication;
import com.geolocalizacionsoft.geolocalizacion.R;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

public class MisLugares extends AppCompatActivity {
    ArrayList<Lugares> listMisLugares;
    RecyclerView recyclerMisLugares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lugares);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        listMisLugares = new ArrayList<>();
        recyclerMisLugares = findViewById(R.id.recyclerMisLugares);
        recyclerMisLugares.setLayoutManager(new LinearLayoutManager(this));
        CrearGaleria();
    }

    public void CrearGaleria(){
       Cursor c = LeerLugares();
        if(c!=null) {
            if(c.moveToFirst()){
                do{
                    byte[] blob = c.getBlob(2);
                    ByteArrayInputStream imageStream = new ByteArrayInputStream(blob);
                    Bitmap image = BitmapFactory.decodeStream(imageStream);
                    listMisLugares.add(new Lugares(c.getString(0),c.getString(1),image));
                }while(c.moveToNext());


            }
        }
        GaleriaAdapter galeriaAdapter = new GaleriaAdapter(listMisLugares);
        recyclerMisLugares.setAdapter(galeriaAdapter);
    }

    public Cursor LeerLugares(){return (((BaseApplication)getApplication()).getLugares());}


}
