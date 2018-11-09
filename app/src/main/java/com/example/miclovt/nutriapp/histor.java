package com.example.miclovt.nutriapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import lib.fecha;

public class histor extends AppCompatActivity {
    base_de_datos con;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histor);
        //mensajeerror();
        con=new base_de_datos(this,"bdpacientes",null,1);

        SQLiteDatabase bdL=con.getReadableDatabase();
        bdL.execSQL("PRAGMA cache_size=3000000");
        Intent intent=getIntent();
        int id=intent.getIntExtra("idpac",0);
        Cursor cursor = bdL.rawQuery("SELECT * fROM paciente WHERE _id="+id,null);
        TextView nom=(TextView)findViewById(R.id.nomH);
        TextView data=(TextView)findViewById(R.id.sexedadH);
        ImageView i=(ImageView)findViewById(R.id.imageViewH);
        if(cursor.moveToFirst()){
            String fnac1=cursor.getString(cursor.getColumnIndex("fnac"));
            fecha edadtotal= null;
            byte[] img=cursor.getBlob(1);
            try {
                edadtotal = new fecha(fnac1);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String edad="";

            if(edadtotal.getyearslive()==0){
                if(edadtotal.getmonthslive()==0){
                    edad=edadtotal.getdayslive()+" Dia(s)";
                }else {
                    edad=edadtotal.getmonthslive()+" Mes(es)";
                }
            }else{
                edad=edadtotal.getyearslive()+" Año(s)";
            }
            Bitmap img1=BitmapFactory.decodeByteArray(img,0,img.length);
            String nomcom=cursor.getString(2)+" "+cursor.getString(3);
            edad="sexo:"+cursor.getString(cursor.getColumnIndex("genero"))+"    Edad:"+edad;
            nom.setText(nomcom);
            data.setText(edad);
            i.setImageBitmap(img1);
        }bdL.close();
    }public void  nuevahist(View view){
        Intent pass=new Intent(this,consulta.class);
        startActivity(pass);
    }
    public void  graficar(View view){

    }
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(this,pacientes.class);
        startActivity(setIntent);
    }
    public void mensajeerror(){
        long installed=100000000;
        long millisStart = Calendar.getInstance().getTimeInMillis();
        try {
            installed = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (millisStart-installed>=10000){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("APLICACION CADUCADA");
            alertDialogBuilder.setPositiveButton("yes",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    mensajeerror();
                }
            });
            alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mensajeerror();
                }
            });
            alertDialogBuilder.setCancelable(false);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}
