package com.example.miclovt.nutriapp;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

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
        longitud = (EditText) findViewById(R.id.idlong);
        peso = (EditText) findViewById(R.id.idpeso);
        calcular = (Button) findViewById(R.id.idcalcular);
        calcular.setOnClickListener(this);
        mostrar = (TextView) findViewById(R.id.idresull);
        com.jjoe64.graphview.GraphView graf0=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph);
        com.jjoe64.graphview.GraphView graf1=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph1);
        com.jjoe64.graphview.GraphView graf2=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph2);
        com.jjoe64.graphview.GraphView graf3=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph3);
        com.jjoe64.graphview.GraphView graf4=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph4);
        com.jjoe64.graphview.GraphView graf5=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph5);
        com.jjoe64.graphview.GraphView graf6=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph6);
        com.jjoe64.graphview.GraphView graf7=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph7);
        com.jjoe64.graphview.GraphView graf8=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph8);
        com.jjoe64.graphview.GraphView graf9=(com.jjoe64.graphview.GraphView) findViewById(R.id.graph9);
        graf0.setTitle("longitud edad 0 a 2 niñas");
        graf1.setTitle("longitud edad 0 a 2 niños");
        graf2.setTitle("peso edad 0 a 2 niñas");
        graf3.setTitle("peso edad 0 a 2 niños");
        graf4.setTitle("peso longitud 0 a 2 niñas");
        graf5.setTitle("peso longitud 0 a 2 niños");
        graf6.setTitle("peso talla 2 a 5 niñas");
        graf7.setTitle("peso talla 2 a 5 niños");
        graf8.setTitle("talla edad 2 a 5 niñas");
        graf9.setTitle("talla edad 2 a 5 niños");
        graficar(ope.getLE0_2nina(),graf0);
        graficar(ope.getLE0_2nino(),graf1);
        graficar(ope.getPE0_2nina(),graf2);
        graficar(ope.getPE0_2nino(),graf3);
        graficar(ope.getPL0_2nina(),graf4);
        graficar(ope.getPL0_2nino(),graf5);
        graficar(ope.getPT2_5nina(),graf6);
        graficar(ope.getPT2_5nino(),graf7);
        graficar(ope.getTE2_5nina(),graf8);
        graficar(ope.getTE2_5nino(),graf9);

    }

    public void onClick(View v){
        switch (v.getId()){
            case R.id.idcalcular:{
                double dat_longitud = Double.parseDouble(longitud.getText()+"");
                double dat_peso = Double.parseDouble(peso.getText()+"");
                double result =fun.peso_long02(dat_longitud,dat_peso , "femenino");
                mostrar.setText(result+" ");
                grafpunto(dat_longitud,dat_peso);
            }break;
        }
    }public void graficar(double [][] data,com.jjoe64.graphview.GraphView  graph){
        DataPoint puntos[]=new  DataPoint[data.length];
        LineGraphSeries<DataPoint> series;
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMaxY(data[(data.length)-1][7]);
        graph.getViewport().setMinY(data[0][1]);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(data[0][0]);
        graph.getViewport().setMaxX(data[(data.length)-1][0]);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
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
    }
    public  void grafpunto(double x,double y){
        com.jjoe64.graphview.GraphView graph = (com.jjoe64.graphview.GraphView ) findViewById(R.id.graph);
        DataPoint p=new DataPoint(x,y);
        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>();
        series.appendData(p,true,1);
        graph.addSeries(series);

        series.setShape(PointsGraphSeries.Shape.POINT);
    }
}
