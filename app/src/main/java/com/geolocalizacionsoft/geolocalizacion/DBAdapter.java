package com.geolocalizacionsoft.geolocalizacion;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBAdapter {

    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "MapsSoft";

    private SQLiteUbicaciones ubicaciones;

    public DBAdapter(Context context){
        dbHelper = new DBHelper(context);
    }

    public void Open(){
        sqLiteDatabase = dbHelper.getWritableDatabase();
        ubicaciones = new SQLiteUbicaciones(sqLiteDatabase);
    }

    public void Close() {
        sqLiteDatabase.close();
    }

    public boolean UbicacionesIsEmpty(){
        return ubicaciones.BaseIsEmpty();
    }

    public boolean UbicacionesInsert(Ubicacion ubicacion){
        return ubicaciones.Insert(ubicacion);
    }

    public boolean Delete(int id){
        return ubicaciones.Delete(id);
    }

    public Cursor getDatosUbicacion(){
        return ubicaciones.getDatos();
    }


    private class DBHelper extends SQLiteOpenHelper{

        public DBHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(SQLiteUbicaciones.TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL(String.format("droptableifexists %s", SQLiteUbicaciones.TABLE));
        }
    }
}
