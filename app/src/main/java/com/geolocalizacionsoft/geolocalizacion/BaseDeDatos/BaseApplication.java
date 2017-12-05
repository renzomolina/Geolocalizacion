package com.geolocalizacionsoft.geolocalizacion.BaseDeDatos;

import android.app.Application;
import android.database.Cursor;

import com.geolocalizacionsoft.geolocalizacion.MisClases.Ubicacion;

import java.util.ArrayList;


public class BaseApplication extends Application{

    private DBAdapter dbAdapter;

    @Override
    public void onCreate() {
        super.onCreate();
        dbAdapter = new DBAdapter(getApplicationContext());
        dbAdapter.Open();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        dbAdapter.Close();
    }

    public boolean InsertarUbicacion(Ubicacion ubicacion) {
        return dbAdapter.UbicacionesInsert(ubicacion);
    }
    public ArrayList<String> titulosUbicaciones(){
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = dbAdapter.getDatosUbicacion();
        if(cursor.moveToFirst()) {
            do {
                list.add(cursor.getString(1));
            }while (cursor.moveToNext());
        }
        return list;
    }
    public Cursor DatosUbicacion(){
        return dbAdapter.getDatosUbicacion();
    }
    public Cursor Posiciones(){
        return dbAdapter.getPosiciones();
    }
    public Cursor getUbicacion(String lat,String lng){
        return dbAdapter.getUbicacion(lat,lng);
    }


    public boolean InsertarImagenes(String idLocation,byte[] imagen){
        return dbAdapter.ImagenesInsert(idLocation,imagen);
    }
    public Cursor DatosImagenes() {
        return dbAdapter.getDatosImagenes();
    }
    public Cursor IdUltimaUbicacion(){return dbAdapter.getIdUltimaUbicacion();}
    public Cursor getLugares(){return dbAdapter.getLugares();}
}
