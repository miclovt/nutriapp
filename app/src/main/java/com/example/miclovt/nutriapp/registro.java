package com.example.miclovt.nutriapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class registro extends AppCompatActivity {
    private String[] gen = {"selelccione genero", "masculino", "femenino"};
    de.hdodenhof.circleimageview.CircleImageView imagen;
    base_de_datos con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        mensajeerror();
        con = new base_de_datos(this,"bdpacientes",null,1);
        Spinner spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gen));
        imagen=(de.hdodenhof.circleimageview.CircleImageView) findViewById(R.id.foto);
        imagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent,0);
            }
        });
    }@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");
        Bitmap scaled=Bitmap.createScaledBitmap(bitmap,128,128,true);
        imagen.setImageBitmap(bitmap);
    }
    public boolean validarFecha(String fecha) {
        try {
            SimpleDateFormat formatoFecha = new SimpleDateFormat("dd-MM-yyyy");
            formatoFecha.setLenient(false);
            formatoFecha.parse(fecha);
        } catch (ParseException e) {
            Toast.makeText(this, "fecha erronea", Toast.LENGTH_SHORT).show();
            return false;
        }return true;
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
        Drawable imgd=getResources().getDrawable(R.drawable.imagenpro);
        BitmapDrawable imgdb=(BitmapDrawable)imgd;
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
        float height= (float) 128.0,width= (float) 128.0;
        Matrix matrix=new Matrix();
        matrix.postScale(width,height);

        Bitmap scaled=Bitmap.createScaledBitmap(foto,128,128+32,true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(20480);
        scaled.compress(Bitmap.CompressFormat.PNG,100,baos);
        byte []fotob = baos.toByteArray();
        int dia,mes,anio;
        if(!diaText.getText().toString().equals("") && parse(diaText.getText().toString())) {dia= Integer.parseInt(diaText.getText().toString());}else{dia= 0;}
        if(!mesText.getText().toString().equals("") && parse(mesText.getText().toString())) {mes= Integer.parseInt(mesText.getText().toString());}else{mes= 0;}
        if( !anioText.getText().toString().equals("") && parse(anioText.getText().toString())) {anio= Integer.parseInt(anioText.getText().toString());}else{anio=0;}
        String fecha= dia+"-"+mes+"-"+anio;
        SimpleDateFormat format= new SimpleDateFormat("dd-mm-yyyy");
        //Date fe=format.
        if(!op.equals("selelccione genero") && !nombre.trim().equals("")&& !apellido.trim().equals("")  ) {
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
                    Intent intent=new Intent(this,pacientes.class);
                    startActivity(intent);
                }catch (Exception e) {
                    Toast.makeText(this, "Ups hubo un problema y la adicion no se completo", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "La fecha que ingreso no existe", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "hay campos vacios que debe completar", Toast.LENGTH_SHORT).show();
        }
    }public void mensajeerror(){
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