package com.example.miclovt.nutriapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import lib.*;

public class histor extends AppCompatActivity {
    base_de_datos con;
    ArrayList <Integer> idh=new ArrayList<>();
    ArrayList <double[][]>tablas=new ArrayList<>();
    ArrayList <String> nomtablas=new ArrayList<>();
    com.jjoe64.graphview.GraphView plot;
    Double x=0.0,y=0.0;
    fecha fnaci=null;
    int pos =0,id=0;
    int not=0;
    int meses=0;
    String genero="";
    String times="";
    ListView listahist;
    double peso=0,tallalongi=0;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_histor);
        //mensajeerror();
        con=new base_de_datos(this,"bdpacientes",null,1);
        SQLiteDatabase bdL=con.getReadableDatabase();
        Intent intent=getIntent();
        id=intent.getIntExtra("idpac",0);
        llenar(id,bdL);
        llenarlista(id,bdL);
        bdL.close();
        listahist=findViewById(R.id.listahist);
        listahist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                final View graflayout= View.inflate(v.getContext(), R.layout.graficas, null);
                final AlertDialog.Builder construirdialogo= new AlertDialog.Builder(v.getContext());
                plot=graflayout.findViewById(R.id.graph);
                construirdialogo.setView(R.layout.graficas);
                final TextView titulo=graflayout.findViewById(R.id.titulografica);
                final TextView descri=graflayout.findViewById(R.id.prescri);
                final TextView desvi=graflayout.findViewById(R.id.desviacion);
                desvi.setTextColor(Color.rgb(2,119,189));
                descri.setTextColor(Color.rgb(2,119,189));
                SQLiteDatabase bdL=con.getReadableDatabase();
                Cursor cursor = bdL.rawQuery("SELECT p.fnac,c.fecha,c.peso,c.tallalong fROM consulta c,paciente p WHERE p._id="+getIntent().getIntExtra("idpac",0)+" and c._id="+idh.get(position),null);
                fecha nac=null;
                if(cursor.moveToFirst()){
                    try {
                        nac=new fecha(cursor.getString(0));
                        SimpleDateFormat formato=new SimpleDateFormat("dd-MM-yyyy");
                        Date time=formato.parse(cursor.getString(1));
                        Calendar c=Calendar.getInstance();
                        c.setTime(time);
                        times=c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.YEAR);
                        meses=nac.getmonthslive(times);
                    } catch (ParseException e) {
                        Toast.makeText(histor.this, "error parse", Toast.LENGTH_SHORT).show();
                    }peso=cursor.getDouble(2);
                    tallalongi=cursor.getDouble(3);
                    if(meses>=24 && meses<=60){
                        llenar(getIntent().getIntExtra("idpac",0),con.getReadableDatabase());
                        llenargraficas(titulo,desvi,descri,meses,tallalongi,peso);
                    }if(meses>=0 && meses<=23){
                        nomtablas=new ArrayList<>();
                        tablas=new ArrayList<>();
                        nomtablas.add("longitud para la edad");
                        nomtablas.add("peso para la edad");
                        nomtablas.add("peso para la longitud");
                        if(genero.equals("masculino")){
                            tablas.add(ope.getLE0_2nino());
                            tablas.add(ope.getPE0_2nino());
                            tablas.add(ope.getPL0_2nino());
                        }else{
                            tablas.add(ope.getLE0_2nina());
                            tablas.add(ope.getPE0_2nina());
                            tablas.add(ope.getPL0_2nina());
                        }llenargraficas(titulo,desvi,descri,meses,tallalongi,peso);
                    }
                }bdL.close();
                construirdialogo.setPositiveButton("siguiente",null);
                construirdialogo.setNegativeButton("Anterior",null);
                final AlertDialog alertDialog = construirdialogo.create();
                alertDialog.setView(graflayout);
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                        button.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                if(pos+1<tablas.size()){
                                    pos++;
                                }else{
                                    pos=0;
                                }plot.removeAllSeries();
                                llenargraficas(titulo,desvi,descri,meses,tallalongi,peso);

                            }
                        });
                        Button button1= ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                        button1.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                // recorremos las tablas hacia atras
                                if(pos-1>=0){
                                    pos--;
                                }else{
                                    pos=tablas.size()-1;
                                }plot.removeAllSeries();
                                llenargraficas(titulo,desvi,descri,meses,tallalongi,peso);
                            }
                        });
                    }
                });
                alertDialog.show();
            }
        });
    }
    private void llenarlista(int id, SQLiteDatabase bdL) {
        Cursor cursor = bdL.rawQuery("SELECT * fROM consulta WHERE idpac="+id+" ORDER BY fecha",null);
        if(cursor.moveToFirst()){
            listahist=findViewById(R.id.listahist);
            CursorAdapter adapter=new CursorAdapter(this,cursor,0) {
                @Override
                //seleccionamos el estilo que tendra nuestro layout personalizado en este caso el del layout adapter
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    LayoutInflater cursorinflater = (LayoutInflater) context.getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE);
                    return cursorinflater.inflate(R.layout.adapterhist,parent,false);
                }
                @Override
                //seteamos los datos de nuestra base de datos en nuestro adapter
                public void bindView(View view, Context context, Cursor cursor) {
                    try {
                        SimpleDateFormat formato=new SimpleDateFormat("dd-MM-yyyy");
                        idh.add(cursor.getInt(0));
                        String fecha=cursor.getString(2);
                        double peso=cursor.getDouble(3);
                        double tallalongi=cursor.getDouble(4);
                        TextView fechat=view.findViewById(R.id.fechacon);
                        Date fechad=formato.parse(fecha);
                        Calendar c=Calendar.getInstance();
                        c.setTime(fechad);
                        String fec=c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.YEAR);
                        fechat.setText("FECHA: "+fec);
                        TextView pesot=view.findViewById(R.id.pesocon);
                        pesot.setText("PESO(Kg): "+peso);
                        TextView lott=view.findViewById(R.id.lotcon);
                        if(fnaci.getmonthslive(fec)>=0 && fnaci.getmonthslive(fec)<=23){
                            lott.setText("LONGITUD(cm): "+tallalongi);
                        }else{
                            lott.setText("TALLA(cm): "+tallalongi);
                        }
                    }catch (Exception e){
                        Toast.makeText(context, "no se pudieron obtener los datos", Toast.LENGTH_SHORT).show();
                    }
                }
            };bdL.close();
            listahist.setAdapter(adapter);
        }
    }

    private void llenar(int id, SQLiteDatabase bdL) {
        nomtablas=new ArrayList<>();
        tablas=new ArrayList<>();
        Cursor cursor = bdL.rawQuery("SELECT * fROM paciente WHERE _id="+id,null);
        TextView nom=(TextView)findViewById(R.id.nomH);
        TextView sexo=(TextView)findViewById(R.id.sexH);
        TextView edaaad=(TextView)findViewById(R.id.edadH);
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
                edad=edadtotal.getyearslive()+" Año(s) "+edadtotal.getmonthslive()%12+" Mes(es)";
            }
            Bitmap img1=BitmapFactory.decodeByteArray(img,0,img.length);
            String nomcom=cursor.getString(2)+" "+cursor.getString(3);
            genero=cursor.getString(cursor.getColumnIndex("genero"));
            nom.setText(nomcom);
            sexo.setText("Sexo:"+cursor.getString(cursor.getColumnIndex("genero")));
            edaaad.setText("Edad:"+edad);
            i.setImageBitmap(img1);
            fnaci=edadtotal;
            if(edadtotal.getmonthslive()>=0 && edadtotal.getmonthslive()<=23){
                nomtablas.add("longitud para la edad");
                nomtablas.add("peso para la edad");
                nomtablas.add("peso para la longitud");
                if(cursor.getString(cursor.getColumnIndex("genero")).equals("masculino")){
                    tablas.add(ope.getLE0_2nino());
                    tablas.add(ope.getPE0_2nino());
                    tablas.add(ope.getPL0_2nino());
                }else{
                    tablas.add(ope.getLE0_2nina());
                    tablas.add(ope.getPE0_2nina());
                    tablas.add(ope.getPL0_2nina());
                }
            }else{
                nomtablas.add("peso para la talla");
                nomtablas.add("talla para la edad");
                if(cursor.getString(cursor.getColumnIndex("genero")).equals("masculino")){
                    tablas.add(ope.getPT2_5nino());
                    tablas.add(ope.getTE2_5nino());
                }else{
                    tablas.add(ope.getPT2_5nina());
                    tablas.add(ope.getTE2_5nina());
                }
            }
        }
    }

    public void  nuevahist(View cont) throws ParseException {
        final View popupExerciseStatisticView = View.inflate(cont.getContext(), R.layout.alertconsulta, null);
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(cont.getContext());
        alertDialogBuilder.setView(R.layout.alertconsulta);
        Button guardar=popupExerciseStatisticView.findViewById(R.id.guardar);
        final Button graficarx=popupExerciseStatisticView.findViewById(R.id.graficas);
        Button calcular=popupExerciseStatisticView.findViewById(R.id.idcalcular);
        TextView pot=popupExerciseStatisticView.findViewById(R.id.tallaolong),peso=popupExerciseStatisticView.findViewById(R.id.textopeso);
        final EditText diac=popupExerciseStatisticView.findViewById(R.id.diac);
        final EditText mesc=popupExerciseStatisticView.findViewById(R.id.mesc);
        final EditText anioc=popupExerciseStatisticView.findViewById(R.id.anioc);
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(fahora());
        diac.setText(calendar.get(Calendar.DAY_OF_MONTH)+"");
        mesc.setText((calendar.get(Calendar.MONTH)+1)+"");
        anioc.setText(calendar.get(Calendar.YEAR)+"");
        peso.setText("Introduzca el peso (Kg):");
        if(fnaci.getmonthslive()>=0 && fnaci.getmonthslive()<=23){
            pot.setText("Introduzca la longitud (cm):");
        }else{
            pot.setText("Introduzca la talla (cm):");
        }
        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    EditText xe=popupExerciseStatisticView.findViewById(R.id.edtitallaolong);
                    EditText ye=popupExerciseStatisticView.findViewById(R.id.editpeso);
                    x=Double.parseDouble(xe.getText().toString());
                    y=Double.parseDouble(ye.getText().toString());
                    AlertDialog.Builder selecnot=new AlertDialog.Builder(v.getContext());
                    selecnot.setMessage("Que notacion usara?");
                    selecnot.setPositiveButton("NOTACION OMS", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            not=0;
                        }
                    });
                    selecnot.setNegativeButton("NOTACIOM MSD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            not=1;
                        }
                    });
                    AlertDialog alertDialog = selecnot.create();
                    alertDialog.show();
                    graficarx.setTextColor(Color.rgb(0,130,0));
                    graficarx.startAnimation(AnimationUtils.loadAnimation(v.getContext(),R.anim.milkshake));
                }catch (Exception e){
                    Toast.makeText(histor.this, "datos erroneos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    try {
                        EditText xe=popupExerciseStatisticView.findViewById(R.id.edtitallaolong);
                        EditText ye=popupExerciseStatisticView.findViewById(R.id.editpeso);
                        x=Double.parseDouble(xe.getText().toString());
                        y=Double.parseDouble(ye.getText().toString());
                        SimpleDateFormat  format=new SimpleDateFormat("dd-MM-yyyy");
                        Date fechacon=format.parse(diac.getText().toString()+"-"+mesc.getText().toString()+"-"+anioc.getText().toString());
                        SQLiteDatabase bdW=con.getWritableDatabase();
                        String sql="INSERT INTO  consulta (idpac,fecha,peso,tallalong) VALUES (?,?,?,?)";
                        SQLiteStatement insert = bdW.compileStatement(sql);
                        insert.clearBindings();
                        insert.bindDouble(1,id);
                        insert.bindString(2,format.format(fechacon));
                        insert.bindDouble(3,y);
                        insert.bindDouble(4,x);
                        insert.executeInsert();
                        Toast.makeText(histor.this, "GUARDADO", Toast.LENGTH_SHORT).show();
                        bdW.close();
                    }catch (ParseException e){
                        Toast.makeText(histor.this, "Datos invalidos", Toast.LENGTH_SHORT).show();
                    }catch (SQLException e){
                        Toast.makeText(histor.this, "Ups! error al guardar", Toast.LENGTH_SHORT).show();
                    }
                    Intent intent=new Intent(getApplication(),histor.class);
                    intent.putExtra("idpac",id);
                    startActivity(intent);
                }
        });
        graficarx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(x!=0 && y!=0){
                    final View popupExerciseStatisticView = View.inflate(v.getContext(), R.layout.graficas, null);
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                    plot=popupExerciseStatisticView.findViewById(R.id.graph);
                    alertDialogBuilder.setView(R.layout.graficas);
                    final TextView titulo=popupExerciseStatisticView.findViewById(R.id.titulografica);
                    final TextView descri=popupExerciseStatisticView.findViewById(R.id.prescri);
                    final TextView desvi=popupExerciseStatisticView.findViewById(R.id.desviacion);
                    desvi.setTextColor(Color.rgb(2,119,189));
                    descri.setTextColor(Color.rgb(2,119,189));
                    SQLiteDatabase bdL=con.getReadableDatabase();
                    final Cursor cursor = bdL.rawQuery("SELECT * fROM paciente WHERE _id="+id,null);
                    if(cursor.moveToFirst()){
                        try {
                            fecha edad=new fecha(cursor.getString(cursor.getColumnIndex("fnac")));
                            int edadenmesx= edad.getmonthslive(diac.getText().toString()+"-"+mesc.getText().toString()+"-"+anioc.getText().toString());
                            llenargraficas(titulo,desvi,descri,edadenmesx,x,y);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }alertDialogBuilder.setPositiveButton("siguiente",null);
                    alertDialogBuilder.setNegativeButton("Anterior",null);
                    final AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.setView(popupExerciseStatisticView);
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                        @Override
                        public void onShow(DialogInterface dialog) {
                            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(cursor.moveToFirst()){
                                        try {
                                            fecha edad=new fecha(cursor.getString(cursor.getColumnIndex("fnac")));
                                            int edadenmesx= edad.getmonthslive(diac.getText().toString()+"-"+mesc.getText().toString()+"-"+anioc.getText().toString());
                                            if(pos+1<tablas.size()){
                                                pos++;
                                            }else{
                                                pos=0;
                                            }plot.removeAllSeries();
                                            llenargraficas(titulo,desvi,descri,edadenmesx,x,y);
                                        } catch (ParseException e) {
                                            Toast.makeText(histor.this, "error al cargar las graficas", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                            Button button1= ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
                            button1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    // recorremos las tablas hacia atras
                                    if(cursor.moveToFirst()){
                                        try {
                                            fecha edad=new fecha(cursor.getString(cursor.getColumnIndex("fnac")));
                                            int edadenmesx= edad.getmonthslive(diac.getText().toString()+"-"+mesc.getText().toString()+"-"+anioc.getText().toString());
                                            if(pos-1>=0){
                                                pos--;
                                            }else{
                                                pos=tablas.size()-1;
                                            }plot.removeAllSeries();
                                            llenargraficas(titulo,desvi,descri,edadenmesx,x,y);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }else {
                    Toast.makeText(histor.this, "sin datos", Toast.LENGTH_SHORT).show();
                }
            }
        });
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setView(popupExerciseStatisticView);
        alertDialog.show();
    }
    public void graficar(double [][] data,com.jjoe64.graphview.GraphView  graph,double x,double y,String ex,String ey){
        DataPoint puntos[]=new  DataPoint[data.length];
        graph.getGridLabelRenderer().setHorizontalAxisTitle(ex);
        graph.getGridLabelRenderer().setVerticalAxisTitle(ey);
        graph.setTitleTextSize(30);
        LineGraphSeries<DataPoint> series;
        for(int j=1;j<=7;j++){
            for (int i=0;i<data.length;i++) {
                puntos[i] = new DataPoint(data[i][0], data[i][j]);
            }series= new LineGraphSeries<>(puntos);
            if(j==1 || j==7){
                series.setColor(Color.RED);
            }if(j==2 || j==6){
                series.setColor(Color.YELLOW);
            }if(j==3 || j==5){
                series.setColor(Color.GREEN);
            }graph.addSeries(series);
        }graph.computeScroll();
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(data[(data.length)-1][7]);
        graph.getViewport().setMinY(data[0][1]);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(data[0][0]);
        graph.getViewport().setMaxX(data[(data.length)-1][0]);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        DataPoint p=new DataPoint(x,y);
        PointsGraphSeries<DataPoint> seriesx = new PointsGraphSeries<>();
        seriesx.appendData(p,true,1);
        graph.addSeries(seriesx);
        seriesx.setShape(PointsGraphSeries.Shape.POINT);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        Intent setIntent = new Intent(this,MainActivity.class);
        finish();
        startActivity(setIntent);

    }public void borrarc(final View view){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("¿Esta seguro de borrar esta consulta?");
        alertDialogBuilder.setPositiveButton("si",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                try {
                    SQLiteDatabase bdW=con.getWritableDatabase();
                    String del="DELETE FROM consulta WHERE _id="+(idh.get(listahist.getPositionForView(view)))+" AND idpac="+id;
                    bdW.execSQL(del);
                    Toast.makeText(getApplicationContext(),"Borrado con exito",Toast.LENGTH_LONG).show();
                    bdW.close();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(),"Ups! error al borrar",Toast.LENGTH_LONG).show();
                }Intent intent=new Intent(getApplicationContext(),histor.class);
                intent.putExtra("idpac",id);
                startActivity(intent);
            }
        });
        alertDialogBuilder.setNegativeButton("No",null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

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
            alertDialogBuilder.setCancelable(false);
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }public void llenargraficas(TextView titulo, TextView desvi, TextView descri, int edadenmesx,double x,double y){
        try{
            if(edadenmesx>=0 && edadenmesx<=23){
                switch (pos){
                    case 0: graficar(tablas.get(pos),plot,edadenmesx,x,"meses","longitud");titulo.setText(nomtablas.get(pos));desvi.setText("Desviacion:"+ope.long_edad02(edadenmesx,x,genero));descri.setText(prescripcion(0,ope.long_edad02(edadenmesx,x,genero))); break;
                    case 1: graficar(tablas.get(pos),plot,edadenmesx,y,"meses","peso");titulo.setText(nomtablas.get(pos));desvi.setText("Desviacion:"+ope.peso_edad02(edadenmesx,y,genero));descri.setText(prescripcion(1,ope.peso_edad02(edadenmesx,y,genero))); break;
                    case 2: graficar(tablas.get(pos),plot,x,y,"longitud","peso");titulo.setText(nomtablas.get(pos));desvi.setText("Desviacion:"+ope.peso_long02(x,y,genero));descri.setText(prescripcion(1,ope.peso_long02(x,y,genero))); break;
                    default: break;
                }
            }if(edadenmesx>=24 && edadenmesx<=60){
                switch (pos){
                    case 0: graficar(tablas.get(pos),plot,x,y,"talla","peso");titulo.setText(nomtablas.get(pos));desvi.setText("Desviacion:"+ope.peso_talla25(x,y,genero));descri.setText(prescripcion(1,ope.peso_talla25(x,y,genero))); break;
                    case 1: graficar(tablas.get(pos),plot,edadenmesx,x,"meses","talla");titulo.setText(nomtablas.get(pos));desvi.setText("Desviacion:"+ope.talla_edad25(edadenmesx,x,genero));descri.setText(prescripcion(0,ope.talla_edad25(edadenmesx,x,genero))); break;
                    default: break;
                }
            }if(edadenmesx>60){
                Toast.makeText(this, "LA APLICACION SOLO FUNCIONA PARA NIÑOS DE 0 A 5 AÑOS", Toast.LENGTH_LONG).show();
            }
        }catch (Exception e){
            Toast.makeText(this, "los datos son invalidos debido al cambio de fecha de nacimiento,borre los datos", Toast.LENGTH_SHORT).show();
        }

    }public String prescripcion(int sw,double desv){
        DecimalFormat num=new DecimalFormat("#,##");
        desv=Double.parseDouble(num.format(desv));
        if(not==0){
            switch (sw){
                case 0:{
                    if(desv<=-1 && desv>-2){
                        return "RETRASO DE CRECIMIENTO LEVE";
                    }if(desv<=-2 && desv>-3){
                        return "RETRASO DE CRECIMIENTO MODERADO";
                    }if(desv<=-3){
                        return "RETRASO DE CRECIMIENTO SEVERO";
                    }if(desv>1){
                        return "TALLA ALTA";
                    }if(desv>=-2 && desv<=2){
                        return "TALLA NORMAL";
                    }
                };
                case 1:{
                    if(desv>2){
                        return "OBESIDAD";
                    }if(desv<=2 && desv>1){
                        return "SOBREPESO";
                    }if(desv>-1 && desv<=1){
                        return "PESO NORMAL";
                    }if(desv>-2 && desv<=-1){
                        return "DESNUTRICION LEVE";
                    }if(desv>-3 && desv<=-2){
                        return "DESNUTRICION MODERADA";
                    }
                    if(desv<=-3){
                        return "DESNUTRICION SEVERA";
                    }
                };
                default: break;
            }
        }else{
            switch (sw){
                case 0:{
                    if(desv<-2){
                        return "TALLA BAJA";
                    }if(desv>2){
                        return "TALLA ALTA";
                    }if(desv>=-2 && desv<=2){
                        return "TALLA NORMAL";
                    }
                };
                case 1:{
                    if(desv>3){
                        return "OBESIDAD";
                    }if(desv<=3 && desv>2){
                        return "SOBREPESO";
                    }if(desv>=-2 && desv<=2){
                        return "PESO NORMAL";
                    }if(desv<-2 && desv>=-3){
                        return "DESNUTRICION AGUDA MODERADA";
                    }if(desv<-3){
                        return "DESNUTRICION AGUDA GRAVE Y/O ANEMIA GRAVE";
                    }
                };
                default: break;
            }
        }

        return "";
    }public Date fahora() throws ParseException {
        SimpleDateFormat formato=new SimpleDateFormat("dd-MM-yyyy");
        Date ahora=new Date();
        Calendar c=Calendar.getInstance();
        c.setTime(ahora);
        String fec=c.get(Calendar.DAY_OF_MONTH)+"-"+(c.get(Calendar.MONTH)+1)+"-"+c.get(Calendar.YEAR);
        return formato.parse(fec);
    }
    public void graficarhist(View view) {
        Toast.makeText(this, "FUNCION NO DISPONIBLE POR EL MOMENTO", Toast.LENGTH_LONG).show();
    }
    public void editar(final View view){
        Intent intent=new Intent(this,registro.class);
        intent.putExtra("idpac",getIntent().getIntExtra("idpac",0));
        startActivity(intent);
    }
}
