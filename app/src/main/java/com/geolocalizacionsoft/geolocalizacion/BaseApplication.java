package com.geolocalizacionsoft.geolocalizacion;

import android.app.Application;
import android.database.Cursor;

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
}
