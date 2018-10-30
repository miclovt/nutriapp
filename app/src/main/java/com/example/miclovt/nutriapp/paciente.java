package com.example.miclovt.nutriapp;

import android.graphics.Bitmap;

public class paciente {
    private byte[] img;
    private String nom;
    private String Edad;

    public paciente(byte[] img, String nom, String edad) {
        this.img = img;
        this.nom = nom;
        Edad = edad;
    }

    public byte[] getImg() {
        return img;
    }

    public String getNom() {
        return nom;
    }

    public String getEdad() {
        return Edad;
    }
}
