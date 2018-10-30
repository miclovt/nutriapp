package com.example.miclovt.nutriapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

public class pacientes extends AppCompatActivity {

    /*ListView lv_pacientes;
    ListView listaSeQuiH;
    ArrayList<paciente>  listpacientes;
    ArrayList<String> listaListviewPacientes;
    ArrayAdapter adaptadorListviewpaciente;
    String cadcondiSQL="";
    base_de_datos con;
    SimpleCursorAdapter adaptador;
    */
    ListView lv_pacientes;
    base_de_datos con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);
        con = new base_de_datos(this,"bdpacientes",null,1);
        SQLiteDatabase bdL=con.getReadableDatabase();
        int id[]={R.id.nomlista,R.id.edadlista};
        String campos[] = {"nombre","fnac"};
        lv_pacientes=(ListView)findViewById(R.id.lvpacientes);
        Cursor cursor = bdL.rawQuery("SELECT * FROM paciente", null);
        SimpleCursorAdapter adaptador = new SimpleCursorAdapter(this, R.layout.adapter, cursor, campos, id);
        lv_pacientes.setAdapter(adaptador);

        //adaptadorListviewpaciente = new ArrayAdapter(this, R.layout.adapter,listaListviewPacientes);
        //listaSeQuiH.setAdapter(adaptadorListviewpaciente);

    }
    public void  reg(View view){
        Intent pass=new Intent(this,registro.class);
        startActivity(pass);
    }

    /*private void consultarListaSeQuiH() {
        SQLiteDatabase bdL=con.getReadableDatabase();
        paciente pac=null;
        listpacientes = new ArrayList<paciente>();
        Cursor cursor = bdL.rawQuery("SELECT * FROM paciente", null);
        while(cursor.moveToNext())
        {
            byte[] imb =cursor.getBlob(1);
            Bitmap img=BitmapFactory.decodeByteArray(imb,0,imb.length);
            pac= new paciente(img, cursor.getString(1), cursor.getString(5));
            listpacientes.add(pac);
        }
        obtenetlistaSQHaf();
        adaptadorListviewsqh = new ArrayAdapter(this, android.R.layout.simple_list_item_1,listaListviewSQH);
        listaSeQuiH.setAdapter(adaptadorListviewsqh);
    }
    private void obtenetlistaSQHaf() {
        listaListviewPacientes= new ArrayList<String>();
        for (int i = 0; i < listpacientes.size(); i++) {
            listaListviewPacientes.add(listaSqhFecha.get(i).getIdf()+"-"+listaSqhFecha.get(i).getDia()+"/"+listaSqhFecha.get(i).getMes()+"/"+listaSqhFecha.get(i).getAno()+"\n" +
                    listaSqh.get(i).getHora()+":"+listaSqh.get(i).getMinuto()+"\n" +
                    listaSqhActividad.get(i).getIdAct()+"-"+listaSqhActividad.get(i).getDescripcion());
        }
    }*/
}
