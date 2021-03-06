package com.example.miclovt.nutriapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lib.fecha;

public class MainActivity extends AppCompatActivity {
    base_de_datos con;
    ArrayList<Integer> idpac=new ArrayList<>();
    Cursor cursor;
    ListView lv_pacientes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lv_pacientes=(ListView)findViewById(R.id.listahoy) ;
        con = new base_de_datos(this,"bdpacientes",null,1);
        llenarlist();

        /*Button buttonRequest = findViewById(R.id.button);
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission(0);
                }if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestStoragePermission(1);
                }
                try {

                    final String inFileName = getDatabasePath("bdpacientes").getAbsolutePath();
                    File dbFile = new File(inFileName);

                    String outFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"database_copy.db";
                    File salida=new File(outFileName);
                    InputStream in = new FileInputStream(dbFile);
                    OutputStream out = new FileOutputStream(salida);

                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                }catch (Exception p){

                    if(new File(getDatabasePath("bdpacientes").getPath()).exists())
                        Toast.makeText(MainActivity.this, getDatabasePath("bdpacientes.db").getAbsolutePath(), Toast.LENGTH_SHORT).show();
                }

            }
        });*/
     //Pedidiendo permisos de escritura y lectura
    }private void requestStoragePermission(final int i) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(i==0)
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                            else
                                ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    //comprobamos que los permisos esten habilitados
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
    //Llenamos la lista con los pacientes
    private void llenarlist() {
        SQLiteDatabase bdL=con.getReadableDatabase();
        cursor = bdL.rawQuery("SELECT * fROM paciente ",null);
        idpac=new ArrayList<>();
        if (cursor.moveToFirst() && cursor != null) {
            do {
                //con esto obtenemos los ids de la base de datos paciente
                idpac.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        CursorAdapter adapter=new CursorAdapter(this,cursor,0) {
            @Override
            //seleccionamos el estilo que tendra nuestro layout personalizado en este caso el del layout adapter
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater cursorinflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                return cursorinflater.inflate(R.layout.adapter,parent,false);
            }

            @Override
            //seteamos los datos de nuestra base de datos en nuestro adapter
            public void bindView(View view, Context context, Cursor cursor) {
                //con esto obtenomos la edad exacta
                byte[] img=cursor.getBlob(cursor.getColumnIndex("foto"));
                String fnac1=cursor.getString(cursor.getColumnIndex("fnac"));
                fecha edadtotal= null;
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
                ImageView foto=(ImageView) view.findViewById(R.id.imglista);
                TextView nomcomt=(TextView) view.findViewById(R.id.nomlista);
                TextView edadt=(TextView) view.findViewById(R.id.edadlista);
                foto.setImageBitmap(img1);
                nomcomt.setText(nomcom);
                edadt.setText("Edad:"+edad);
            }
        };bdL.close();
        lv_pacientes.setAdapter(adapter);
        lv_pacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i =new Intent(MainActivity.this,histor.class);
                i.putExtra("idpac",idpac.get(position));
                startActivity(i);
            }
        });

    }
    //borramos a un paciente de la base de datos
    public void borrar(final View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("¿Esta seguro de borrar al paciente?");
        alertDialogBuilder.setPositiveButton("si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    SQLiteDatabase bdW=con.getWritableDatabase();
                    int pos=lv_pacientes.getPositionForView(view);
                    int id=idpac.get(pos);
                    String del="DELETE FROM paciente WHERE _id="+id;
                    bdW.execSQL(del);
                    Toast.makeText(getApplicationContext(),"Paciente borrado con exito",Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Ups! error al borrar",Toast.LENGTH_LONG).show();
                }llenarlist();
            }
        });
        alertDialogBuilder.setNegativeButton("No",null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    //paso a la pantalla registro paciente
    public  void  reg(View view){
        Intent pass=new Intent(this, registro.class);
        startActivity(pass);
    }
    //Se muestran los resultados de los pacientes que coincidan con el nombre despues de precionar el boton de busqueda
    public  void  busqresult(View view){
        EditText busqe=(EditText) findViewById(R.id.editText4);
        String busq=busqe.getText().toString();
        busq.toLowerCase();
        con=new base_de_datos(this,"bdpacientes",null,1);
        SQLiteDatabase bdL=con.getReadableDatabase();
        Cursor cursor=bdL.rawQuery("SELECT * FROM paciente WHERE (lower(nombre||' '||apellido)) LIKE '%"+busq+"%'",null);
        idpac = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                idpac.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        //adaptador=new adaptador(this,cursor,0);
        CursorAdapter adapter=new CursorAdapter(this,cursor,0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                LayoutInflater cursorinflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                return cursorinflater.inflate(R.layout.adapter,parent,false);
            }
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String fnac1=cursor.getString(cursor.getColumnIndex("fnac"));
                byte[] img=cursor.getBlob(cursor.getColumnIndex("foto"));
                fecha edadtotal= null;
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
                String nomcom=cursor.getString(2)+" "+cursor.getString(3);
                Bitmap img1=BitmapFactory.decodeByteArray(img,0,img.length);
                ImageView foto=(ImageView) view.findViewById(R.id.imglista);
                TextView nomcomt=(TextView) view.findViewById(R.id.nomlista);
                TextView edadt=(TextView) view.findViewById(R.id.edadlista);
                foto.setImageBitmap(img1);
                nomcomt.setText(nomcom);
                edadt.setText("Edad:"+edad);
            }
        };
        lv_pacientes.setAdapter(adapter);
        lv_pacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(MainActivity.this, histor.class);
                i.putExtra("idpac", idpac.get(position));
                startActivity(i);
            }
        });
    }
    //sobreacrgamos el metodo onBackPressed paara que la aplicacion termine
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        moveTaskToBack(true);
        finish();

    }
}