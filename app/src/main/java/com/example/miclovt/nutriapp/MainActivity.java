package com.example.miclovt.nutriapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    base_de_datos con;
    ArrayList<Integer> idpac;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mensajeerror();
    }public  void  allpac(View view){
        Intent pass=new Intent(this, pacientes.class);
        startActivity(pass);
    }public  void  busqresult(View view){
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                DateTimeFormatter fmt=DateTimeFormatter.ofPattern("dd-MM-yyyy");
                String fnac1=cursor.getString(cursor.getColumnIndex("fnac"));
                LocalDate fnac=LocalDate.parse(fnac1,fmt);
                LocalDate ahora=LocalDate.now();
                Period periodo=Period.between(fnac,ahora);
                String edad="";
                byte[] img=cursor.getBlob(cursor.getColumnIndex("foto"));
                if(periodo.getYears()==0){
                    if(periodo.getMonths()==0){
                        edad=periodo.getDays()+" Dia(s)";
                    }else {
                        edad=periodo.getMonths()+" Mes(es)";
                    }
                }else{
                    edad=periodo.getYears()+" Año(s)";
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
        ListView lv_pacientes=(ListView)findViewById(R.id.listahoy) ;
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
    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        moveTaskToBack(true);
        finish();

    }public void bor(View view){
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + this.getPackageName()));
        startActivity(intent);
    }public void mensajeerror(){
        long installed=100000000;
        long millisStart = Calendar.getInstance().getTimeInMillis();
        try {
            installed = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).firstInstallTime;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (millisStart-installed>= 172800000){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("APLICACION CADUCADA");
            alertDialogBuilder.setCancelable(false);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }
}