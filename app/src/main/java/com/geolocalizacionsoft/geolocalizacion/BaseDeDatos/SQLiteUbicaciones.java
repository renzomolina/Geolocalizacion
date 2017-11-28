package com.geolocalizacionsoft.geolocalizacion.BaseDeDatos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.geolocalizacionsoft.geolocalizacion.MisClases.Ubicacion;


public class SQLiteUbicaciones {
    private static final String NAME = "ubicaciones";
    private SQLiteDatabase SqlDb;

    public SQLiteUbicaciones(SQLiteDatabase SqlDb) {
        this.SqlDb=SqlDb;
    }

    private class Columns implements BaseColumns{
        public static final String ID = "id_Ubicacion";
        public static final String TITULO = "titulo";//TITULO = NOMBRE
        public static final String DESCRIPCION = "descripcion";
        public static final String POSICION = "posicion";
       // public Image IMAGEN = null;


    }
    private final static  String[] COLUMNS = {Columns.ID,Columns.TITULO,Columns.DESCRIPCION,Columns.POSICION};


    public final static String TABLE = "CREATE TABLE IF NOT EXISTS "+ NAME + "(" + Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Columns.TITULO +
            " TEXT NOT NULL, " + Columns.DESCRIPCION + " TEXT NOT NULL, " + Columns.POSICION + " TEXT NOT NULL)";


    public boolean Insert(Ubicacion ubicacion){
        ContentValues values = new ContentValues();
        values.put(Columns.TITULO,ubicacion.getTitulo());
        values.put(Columns.DESCRIPCION,ubicacion.getDescripcion());
        values.put(Columns.POSICION,ubicacion.getPosicion());
        return (SqlDb.insert(NAME,null,values) > 0);
    }

    public boolean Delete(int id) {
        String condicion = "id_Ubicacion=?";
        String[] argumentos = {String.valueOf(id)};
        return (SqlDb.delete(NAME,condicion,argumentos) > 0);
    }

    public Cursor getPosiciones(){
        String[] columns = {Columns.POSICION};
        return SqlDb.query(NAME,columns,null,null,null,null,null);
    }

    public String getName(){
        return NAME;
    }

    public String[] getColumns(){
        return COLUMNS;
    }

    public boolean BaseIsEmpty(){
        return (SqlDb.query(NAME,COLUMNS,null,null,null,null,null).getCount() == 0);
    }

    public Cursor getDatos(){
        return (SqlDb.query(NAME,COLUMNS,null,null,null,null,null));
    }
}
