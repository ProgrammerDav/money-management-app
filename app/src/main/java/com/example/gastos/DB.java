package com.example.gastos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class DB extends SQLiteOpenHelper {

    private static final String DB_NAME = "gastos_db";
    private static final int DB_VERSION = 2;

    public DB(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE personas (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT)");

        db.execSQL("CREATE TABLE gastos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "concepto TEXT, " +
                "tienda TEXT, " +
                "cantidad REAL, " +
                "fecha TEXT, " +
                "persona TEXT)");

        db.execSQL("INSERT INTO personas (nombre) VALUES ('Nombre1')");
        db.execSQL("INSERT INTO personas (nombre) VALUES ('Nombre2')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS gastos");
        db.execSQL("DROP TABLE IF EXISTS personas");
        onCreate(db);
    }

    // ---------------- REGISTRAR GASTO ----------------

    public boolean registrar(String concepto, String tienda, double cantidad, String fecha, String persona) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("concepto", concepto);
        values.put("tienda", tienda);
        values.put("cantidad", cantidad);
        values.put("fecha", fecha);
        values.put("persona", persona);

        long resultado = db.insert("gastos", null, values);
        db.close();

        return resultado != -1;
    }

    // ---------------- OBTENER PERSONAS ----------------

    public ArrayList<String> obtenerPersonas() {

        ArrayList<String> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT nombre FROM personas", null);

        if (cursor.moveToFirst()) {
            do {
                lista.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return lista;
    }

    // ---------------- MODIFICAR PERSONAS ----------------

    public void modificarPersonas(String nombre1, String nombre2) {

        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM personas");

        db.execSQL("INSERT INTO personas (nombre) VALUES (?)", new Object[]{nombre1});
        db.execSQL("INSERT INTO personas (nombre) VALUES (?)", new Object[]{nombre2});

        db.close();
    }

    // ---------------- RESET (SOLO GASTOS) ----------------

    public void resetDatos() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM gastos");
        db.close();
    }

    // ---------------- TOTAL POR PERSONA ----------------

    public double totalPorPersona(String persona) {

        double total = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(cantidad) FROM gastos WHERE persona = ?",
                new String[]{persona}
        );

        if (cursor.moveToFirst() && cursor.getString(0) != null) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return total;
    }
    // ---------------- INFO GASTOS POR PERSONA ----------------

    public ArrayList<HashMap<String, String>> infoGastos(String persona) {

        ArrayList<HashMap<String, String>> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT concepto, tienda, cantidad, fecha FROM gastos WHERE persona = ?",
                new String[]{persona}
        );

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> gasto = new HashMap<>();

                gasto.put("concepto", cursor.getString(0));
                gasto.put("tienda", cursor.getString(1));
                gasto.put("cantidad", String.valueOf(cursor.getDouble(2)));
                gasto.put("fecha", cursor.getString(3));

                lista.add(gasto);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return lista;
    }

}