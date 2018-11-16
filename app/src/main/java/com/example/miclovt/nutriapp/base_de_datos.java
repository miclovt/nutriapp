package com.example.miclovt.nutriapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

public class base_de_datos extends SQLiteOpenHelper {
    String creartablapacientes="create table paciente(_id integer PRIMARY KEY AUTOINCREMENT,foto blob,nombre varchar(40),apellido varchar(40),genero varchar(20),fnac date);" ;
    String creartablaconsultas="create table consulta(_id integer PRIMARY KEY AUTOINCREMENT,idpac integer not null ,fecha date,peso float,tallalong float,FOREIGN KEY(idpac) REFERENCES paciente(_id));";

    public base_de_datos( Context context,  String name,SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("PRAGMA page_size=30000000");
        db.execSQL("PRAGMA cache_size=30000000");
        db.execSQL(creartablapacientes);
        db.execSQL(creartablaconsultas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
