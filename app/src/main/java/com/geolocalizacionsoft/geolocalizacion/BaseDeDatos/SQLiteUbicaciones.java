package com.geolocalizacionsoft.geolocalizacion.BaseDeDatos;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import com.geolocalizacionsoft.geolocalizacion.MisClases.Ubicacion;


public class SQLiteUbicaciones {
    private static final String NAME = "ubicacion",NAME_IMG = "imagen";
    private SQLiteDatabase SqlDb;

    public SQLiteUbicaciones(SQLiteDatabase SqlDb) {
        this.SqlDb=SqlDb;
    }

    private class Columns implements BaseColumns{
        public static final String
                ID = "id_Ubicacion",
                TITULO = "titulo",
                DESCRIPCION = "descripcion",
                LATITUD = "latitud",
                LONGITUD = "longitud",
                ID_IMG = "id_Imagen",
                IMAGEN = "imagen";
    }
    private final static String[] COLUMNS = {Columns.ID,Columns.TITULO,Columns.DESCRIPCION,Columns.LATITUD,Columns.LONGITUD};
    private final static String[] COLUMNS_IMG = {Columns.ID_IMG,Columns.IMAGEN,Columns.ID};

    public final static String TABLE = "CREATE TABLE IF NOT EXISTS " + NAME + "(" + Columns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            Columns.TITULO + " TEXT NOT NULL, " + Columns.DESCRIPCION + " TEXT NOT NULL, " + Columns.LATITUD + " TEXT NOT NULL, " +
            Columns.LONGITUD + " TEXT NOT NULL) ";

    public final static String TABLE_IMG = "CREATE TABLE IF NOT EXISTS " + NAME_IMG + "(" + Columns.ID_IMG +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + Columns.IMAGEN + " BLOB, " + Columns.ID + " TEXT NOT NULL) ";

    public boolean Insert(Ubicacion ubicacion){
        ContentValues values = new ContentValues();
        values.put(Columns.TITULO,ubicacion.getTitulo());
        values.put(Columns.DESCRIPCION,ubicacion.getDescripcion());
        values.put(Columns.LATITUD,ubicacion.getLatitud());
        values.put(Columns.LONGITUD,ubicacion.getLongitud());
        return (SqlDb.insert(NAME,null,values) > 0);
    }

    public boolean InsertIMG(String idLocation,byte[] imagen) {

        ContentValues values = new ContentValues();
        values.put(Columns.ID, idLocation);
        values.put(Columns.IMAGEN, imagen);
        return (SqlDb.insert(NAME_IMG, null, values) > 0);

    }

    public boolean Delete(int id) {
        String condicion = "id_Ubicacion=?";
        String[] argumentos = {String.valueOf(id)};
        return (SqlDb.delete(NAME,condicion,argumentos) > 0);
    }

    public Cursor getPosiciones(){
        String[] columns = {Columns.LATITUD,Columns.LONGITUD};
        return SqlDb.query(NAME,columns,null,null,null,null,null);
        //return SqlDb.rawQuery(" SELECT latitud,longitud FROM ubicaciones", null);
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

    public Cursor getDatosImagenes(){
        return (SqlDb.query(NAME_IMG,COLUMNS_IMG,null,null,null,null,null));
    }

    public Cursor getLugares(){
        return (SqlDb.rawQuery("SELECT titulo,descripcion,imagen,latitud,longitud FROM ubicacion,imagen WHERE ubicacion.id_Ubicacion == imagen.id_Ubicacion",null));
    }

    public Cursor getIdUltimaUbicacion(){
        return (SqlDb.rawQuery("SELECT MAX(id_Ubicacion) AS id FROM ubicacion",null));
    }

    public Cursor getUbicacion(String lat,String lng) {
        return (SqlDb.rawQuery("SELECT titulo,descripcion " +
                                    "FROM ubicacion " +
                                    "WHERE latitud == "+ lat +" AND longitud ==" + lng,null));
    }

    /*public Cursor getUbicacion(String lat,String lng){
        return (SqlDb.rawQuery(
                        "SELECT titulo,descripcion,imagen " +
                        "FROM (SELECT id_Ubicacion,titulo,descripcion " +
                                "FROM ubicacion " +
                                "WHERE latitud LIKE lat AND longitud LIKE lng) AS location " +
                        "WHERE location.id_Ubicacion == id_Ubicacion"
                ,null));
    }*/
}
