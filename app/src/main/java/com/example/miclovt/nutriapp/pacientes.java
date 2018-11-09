package com.example.miclovt.nutriapp;

import java.text.ParseException;
import java.util.Calendar;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Date;
import java.util.SimpleTimeZone;
import lib.fecha;

public class pacientes extends AppCompatActivity {
    ListView lv_pacientes;
    base_de_datos con;
    Cursor cursor;
    ArrayList<Integer> idpac=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pacientes);
        //mensajeerror();
        con = new base_de_datos(this,"bdpacientes",null,1);
        SQLiteDatabase bdL=con.getReadableDatabase();
        lv_pacientes=(ListView)findViewById(R.id.lvpacientes);
        lv_pacientes.setScrollingCacheEnabled(false);
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
        con.close();
        lv_pacientes.setAdapter(adapter);
        lv_pacientes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i =new Intent(pacientes.this,histor.class);
                i.putExtra("idpac",idpac.get(position));
                startActivity(i);
            }
        });
    }public void reg(View view){
        Intent pass=new Intent(this,registro.class);
        startActivity(pass);
    }public void borrar(final View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("¿Esta seguro de borrar al paciente?");
        alertDialogBuilder.setPositiveButton("yes",new DialogInterface.OnClickListener() {
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
                }startActivity(getIntent());
            }
        });
        alertDialogBuilder.setNegativeButton("No",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }public void editar(View view){
        Toast.makeText(this, "editado", Toast.LENGTH_SHORT).show();
    }@Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(this,MainActivity.class);
        startActivity(setIntent);
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
    }public void filtrar(View view){


    }
}
