package com.example.miclovt.nutriapp;

import android.app.Application;
import android.content.Context;
import android.app.Application;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class registro extends AppCompatActivity {
    private String[] gen = {"selelccione genero", "masculino", "femenino"};
    de.hdodenhof.circleimageview.CircleImageView imagen;
    base_de_datos con;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        con = new base_de_datos(this,"bdpacientes",null,1);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gen));
        imagen=(de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.foto);
        //botoncargar=(Button) findViewById(R.id.buttonañadir);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,0);
            }
        });
    }
    public boolean validarFecha(String fecha) {

        try {

            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");

            formatoFecha.setLenient(false);

            formatoFecha.parse(fecha);

        } catch (ParseException e) {
            Toast.makeText(this,"fecha erronea", Toast.LENGTH_SHORT).show();
            return false;

        }

        return true;

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imagen.setImageBitmap(bitmap);
        }
    }
    public static boolean parse(String args) {
        try {
            int x=Integer.parseInt(args);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    public void cargar(View view){
        String op="";

        //obtenemos la imagen del imageviewcircular
        de.hdodenhof.circleimageview.CircleImageView dirfoto=(de.hdodenhof.circleimageview.CircleImageView)findViewById(R.id.foto);
        Bitmap foto=((BitmapDrawable)dirfoto.getDrawable()).getBitmap();
        //obtenemos nombre y apellido
        EditText nombreText=(EditText) findViewById(R.id.nom);
        EditText apellidoText=(EditText) findViewById(R.id.ape);
        // obtenemos el genero seleccionado en el spinner
        Spinner spinner = findViewById(R.id.spinner);
        op=spinner.getSelectedItem() .toString();
        //obtenemos los datos de la fecha de nacimiento
        EditText diaText=(EditText) findViewById(R.id.dia);
        EditText mesText=(EditText) findViewById(R.id.mes);
        EditText anioText=(EditText) findViewById(R.id.anio);
        // guardamos los valores obtenidos en nuevas variables
        String nombre=nombreText.getText().toString();
        String apellido=apellidoText.getText().toString();
        ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
        foto.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte []fotob = baos.toByteArray();
        int dia,mes,anio;
        if(!diaText.getText().toString().equals("") && parse(diaText.getText().toString())) {dia= Integer.parseInt(diaText.getText().toString());}else{dia= 0;}
        if(!mesText.getText().toString().equals("") && parse(mesText.getText().toString())) {mes= Integer.parseInt(mesText.getText().toString());}else{mes= 0;}
        if(!anioText.getText().toString().equals("") && parse(anioText.getText().toString())) {anio= Integer.parseInt(anioText.getText().toString());}else{anio=0;}
        String fecha= dia+"-"+mes+"-"+anio;
        SimpleDateFormat format= new SimpleDateFormat("dd-mm-yyyy");
        //Date fe=format.
        if( !op.equals("selelccione genero") && !nombre.trim().equals("")&& !apellido.trim().equals("")  ) {
            if (validarFecha(fecha)) {
                try {
                    SQLiteDatabase bdW=con.getWritableDatabase();
                    Date fechaformat=format.parse(fecha);
                    //String insert ="INSERT INTO paciente (foto,nombre,apellido,genero,fnac) VALUES("+fotob+","+nombre+","+apellido+","+op+",null)";
                    String sql ="INSERT INTO paciente (foto,nombre,apellido,genero,fnac) VALUES(?,?,?,?,?)";
                    // aqui tenemos el byte[] con el imagen comprimido, ahora lo guardemos en SQLite
                    SQLiteStatement insert = bdW.compileStatement(sql);
                    insert.clearBindings();
                    insert.bindBlob(1, fotob);
                    insert.bindString(2,nombre);
                    insert.bindString(3,apellido);
                    insert.bindString(4,op);
                    insert.bindString(5,format.format(fechaformat));
                    insert.executeInsert();
                    bdW.close();
                    Toast.makeText(this, "se adiciono correctamente", Toast.LENGTH_SHORT).show();
                }catch (Exception e) {
                    Toast.makeText(this, "Ups hubo un problema y la adicion no se completo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "La fecha que ingreso no existe", Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(this, "hay campos vacios que debe completar", Toast.LENGTH_SHORT).show();
        }
        SQLiteDatabase bdR=con.getReadableDatabase();
        byte[] fotoget=null;
        Cursor cursor = bdR.rawQuery("SELECT fnac fROM paciente ",null);
        TextView data=(TextView)findViewById(R.id.textView5);
        if (cursor.moveToFirst()) {
            //Recorremos el cursor hasta que no haya más registros
            do {
                /*fotoget=cursor.getBlob(0);
                Bitmap img=BitmapFactory.decodeByteArray(fotoget,0,fotoget.length);
                ImageView  fotom=(ImageView)findViewById(R.id.imageView2);
                fotom.setImageBitmap(img);
                //data.setText(data.getText()+" "+nombreget);

            */String fn=cursor.getString(0);
            //System.out.println(fn);
            data.setText(fn);
            } while(cursor.moveToNext());
        }
        cursor.close();
        bdR.close();
        /*Intent intent=new Intent(this,pacientes.class);
        startActivity(intent);*/



    }
}