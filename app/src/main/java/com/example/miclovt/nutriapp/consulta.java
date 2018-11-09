package com.example.miclovt.nutriapp;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Calendar;

import lib.ope;
public class consulta extends AppCompatActivity implements View.OnClickListener {
    EditText longitud;
    EditText peso;
    Button calcular;
    TextView mostrar;
    ope fun=new ope();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta);
        //mensajeerror();
        longitud = (EditText) findViewById(R.id.idlong);
        peso = (EditText) findViewById(R.id.idpeso);
        calcular = (Button) findViewById(R.id.idcalcular);
        calcular.setOnClickListener(this);
        mostrar = (TextView) findViewById(R.id.idresull);
    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.idcalcular:{
                double dat_longitud = Double.parseDouble(longitud.getText()+"");
                double dat_peso = Double.parseDouble(peso.getText()+"");
                double result =fun.peso_long02(dat_longitud,dat_peso , "femenino");
                mostrar.setText(result+" ");
                //grafpunto(dat_longitud,dat_peso);
            }break;
        }
    }public void graficar(double [][] data,com.jjoe64.graphview.GraphView  graph){
        DataPoint puntos[]=new  DataPoint[data.length];
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
            }
            graph.addSeries(series);
        }
        graph.computeScroll();
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(data[(data.length)-1][7]);
        graph.getViewport().setMinY(data[0][1]);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(data[0][0]);
        graph.getViewport().setMaxX(data[(data.length)-1][0]);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
    }

    public  void grafpunto(double x,double y){
        com.jjoe64.graphview.GraphView graph = (com.jjoe64.graphview.GraphView ) findViewById(R.id.graph);
        DataPoint p=new DataPoint(x,y);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>();
        series.appendData(p,true,1);
        graph.addSeries(series);

        series.setShape(PointsGraphSeries.Shape.POINT);
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
