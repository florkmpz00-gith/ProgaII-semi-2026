package com.example.auramoda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "auramoda.db";
    private static final int DB_VERSION = 2;

    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COL_ID = "id";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_EMAIL = "email";
    public static final String COL_PASSWORD = "password";
    public static final String COL_EDAD = "edad";
    public static final String COL_ESTILOS = "estilos";

    public static final String TABLE_FAVORITOS = "favoritos";
    public static final String COL_FAV_ID = "id";
    public static final String COL_FAV_URL = "url";
    public static final String COL_FAV_TIPO = "tipo";
    public static final String COL_FAV_FECHA = "fecha";

    public static final String TABLE_PRENDAS = "prendas";
    public static final String COL_PRENDA_ID = "id";
    public static final String COL_PRENDA_NOMBRE = "nombre";
    public static final String COL_PRENDA_TIPO = "tipo";
    public static final String COL_PRENDA_COLOR = "color";
    public static final String COL_PRENDA_FECHA = "fecha";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USUARIOS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NOMBRE + " TEXT NOT NULL, " +
                COL_EMAIL + " TEXT UNIQUE NOT NULL, " +
                COL_PASSWORD + " TEXT NOT NULL, " +
                COL_EDAD + " TEXT, " +
                COL_ESTILOS + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_FAVORITOS + " (" +
                COL_FAV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_FAV_URL + " TEXT NOT NULL, " +
                COL_FAV_TIPO + " TEXT, " +
                COL_FAV_FECHA + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_PRENDAS + " (" +
                COL_PRENDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_PRENDA_NOMBRE + " TEXT NOT NULL, " +
                COL_PRENDA_TIPO + " TEXT NOT NULL, " +
                COL_PRENDA_COLOR + " TEXT NOT NULL, " +
                COL_PRENDA_FECHA + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_PRENDAS + " (" +
                    COL_PRENDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_PRENDA_NOMBRE + " TEXT NOT NULL, " +
                    COL_PRENDA_TIPO + " TEXT NOT NULL, " +
                    COL_PRENDA_COLOR + " TEXT NOT NULL, " +
                    COL_PRENDA_FECHA + " TEXT)");
        }
    }

    // Usuarios
    public boolean registrarUsuario(String nombre, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NOMBRE, nombre);
        values.put(COL_EMAIL, email);
        values.put(COL_PASSWORD, password);
        long result = db.insert(TABLE_USUARIOS, null, values);
        db.close();
        return result != -1;
    }

    public boolean loginUsuario(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIOS +
                " WHERE email=? AND password=?", new String[]{email, password});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public String getNombreUsuario(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre FROM " + TABLE_USUARIOS +
                " WHERE email=?", new String[]{email});
        String nombre = "";
        if (cursor.moveToFirst()) {
            nombre = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return nombre;
    }

    public boolean emailExiste(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_USUARIOS +
                " WHERE email=?", new String[]{email});
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }

    public void actualizarPerfil(String email, String edad, String estilos) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_EDAD, edad);
        values.put(COL_ESTILOS, estilos);
        db.update(TABLE_USUARIOS, values, "email=?", new String[]{email});
        db.close();
    }

    // Favoritos
    public boolean agregarFavorito(String url, String tipo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_FAV_URL, url);
        values.put(COL_FAV_TIPO, tipo);
        values.put(COL_FAV_FECHA, String.valueOf(System.currentTimeMillis()));
        long result = db.insert(TABLE_FAVORITOS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getFavoritos() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_FAVORITOS +
                " ORDER BY fecha DESC", null);
    }

    public boolean eliminarFavorito(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_FAVORITOS, "id=?",
                new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    // Prendas
    public boolean agregarPrenda(String nombre, String tipo, String color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PRENDA_NOMBRE, nombre);
        values.put(COL_PRENDA_TIPO, tipo);
        values.put(COL_PRENDA_COLOR, color);
        values.put(COL_PRENDA_FECHA, String.valueOf(System.currentTimeMillis()));
        long result = db.insert(TABLE_PRENDAS, null, values);
        db.close();
        return result != -1;
    }

    public Cursor getPrendas() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_PRENDAS +
                " ORDER BY tipo ASC", null);
    }

    public boolean eliminarPrenda(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_PRENDAS, "id=?",
                new String[]{String.valueOf(id)});
        db.close();
        return result > 0;
    }

    public String getPrendasTexto() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT nombre, tipo, color FROM " + TABLE_PRENDAS, null);
        StringBuilder sb = new StringBuilder();
        if (cursor.moveToFirst()) {
            do {
                sb.append(cursor.getString(0)).append(" (")
                        .append(cursor.getString(1)).append(", ")
                        .append(cursor.getString(2)).append("), ");
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return sb.toString();
    }
}