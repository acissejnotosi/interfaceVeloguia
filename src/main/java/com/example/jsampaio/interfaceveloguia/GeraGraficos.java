package com.example.jsampaio.interfaceveloguia;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GeraGraficos extends AppCompatActivity {

    protected LineChart mlatLinearChart;
    protected LineChart mlngLinearChart;
    protected LineChart maltLinearChart;
    protected LineChart mspdLinearChart;
    protected LineChart mPulseLinearChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gera_graficos);

        maltLinearChart = (LineChart)findViewById(R.id.altLinearChart);
        mspdLinearChart = (LineChart)findViewById(R.id.spdLinearChart);
        mPulseLinearChart = (LineChart)findViewById(R.id.pulseLinearChart);
        Intent intent = getIntent();



        int[] pulseArray = intent.getIntArrayExtra("pulseArray");
        int pulseMax = intent.getIntExtra("pulseMax", 1);
        int pulseMin = intent.getIntExtra("pulseMin", 0);

        float[] altArray  = intent.getFloatArrayExtra("alt_dados");
        float[] spdArray  = intent.getFloatArrayExtra("velo_dados");

        float altMax = intent.getFloatExtra("alt_max", 1.0f);
        float altMin = intent.getFloatExtra("alt_min", 0.0f);
        float spdMax = intent.getFloatExtra("velo_max", 1.0f);
        float spdMin = intent.getFloatExtra("velo_min", 0.0f);

        geraGraficoYDoubleXint(altArray, maltLinearChart, altMax, altMin);
        geraGraficoYDoubleXint(spdArray, mspdLinearChart, spdMax, spdMin);

        geraGraficoInt(pulseArray,mPulseLinearChart, pulseMax, pulseMin, "Frequência Cardíaca");
    }


    protected void geraGraficoYDoubleXint(float[] floatList, LineChart mLinearChart, float latMax, float latMin)
    {

        XAxis xAxis = mLinearChart.getXAxis();
        List<String> listString = new ArrayList<>();
        Legend legend = mLinearChart.getLegend();
        List<String> labels = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        LineDataSet dataset = new LineDataSet(entries, "");
        LineData data = new LineData(labels, dataset);


        int i=0;
        float range = latMax-latMin;
        for(float mread : floatList){
            entries.add(new Entry((mread-latMin)/(range),i++));
        }



        dataset.setDrawCubic(false);
        dataset.setDrawCircles(true);
        dataset.setDrawCircleHole(false);
        dataset.setDrawValues(false);


        for(i=0;i<floatList.length;i++)
        {
            labels.add("");
        }



        data.setHighlightEnabled(false);
        //mLinearChart.setClickable(false);
        mLinearChart.setDrawGridBackground(false);
        mLinearChart.setData(data);
        mLinearChart.enableScroll();
        mLinearChart.setDescription("");
        mLinearChart.getAxisLeft().setDrawLabels(false);
        mLinearChart.getAxisRight().setDrawLabels(false);
        mLinearChart.getAxisLeft().setAxisMaxValue(1.0f);
        mLinearChart.getAxisLeft().setAxisMinValue(-0.0f);
        mLinearChart.getXAxis().setDrawGridLines(false);
        mLinearChart.getXAxis().setDrawAxisLine(true);
        mLinearChart.setHorizontalScrollBarEnabled(true);
        mLinearChart.setMinimumHeight(200);
        mLinearChart.setMinimumWidth(200);
        mLinearChart.getLineData().getDataSets().get(0).setColor(Color.BLUE);
        mLinearChart.setScrollX(50);

        /*
            Interaction with the chart
         */

        mLinearChart.setTouchEnabled(true);



        /*
            Alterando o eixo X
         */
        mLinearChart.setEnabled(true);
        xAxis.setValues(listString);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(20f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);


        /*
            Legenda
         */

        legend.setEnabled(true);
        legend.setTextColor(R.color.black);
        legend.setTextSize(10f);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
     //   legend.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[]{"Dado", "Dado2"});

        /*
            Permitir que quando tocado mostre o valor de x,y
         */
        // highlight the entry and x-position 50 in the first (0) DataSet
        Highlight highlight = new Highlight(50, 0);

        mLinearChart.highlightValue(highlight, false); // highlight this value, don't call listener

    }

    protected void geraGraficoInt(int[] floatList, LineChart mLinearChart, int latMax, int latMin, String description)
    {

        XAxis xAxis = mLinearChart.getXAxis();
        List<String> listString = new ArrayList<>();
        Legend legend = mLinearChart.getLegend();
        List<String> labels = new ArrayList<>();
        List<Entry> entries = new ArrayList<>();
        LineDataSet dataset = new LineDataSet(entries, "");
        LineData data = new LineData(labels, dataset);


        int i=0;
        float range = latMax-latMin;
        for(float mread : floatList){
            entries.add(new Entry((mread-latMin)/(range),i++));
        }



        dataset.setDrawCubic(false);
        dataset.setDrawCircles(true);
        dataset.setDrawCircleHole(false);
        dataset.setCircleColor(Color.LTGRAY);
        dataset.setDrawValues(false);
        dataset.setHighlightEnabled(true);
        dataset.setDrawHighlightIndicators(true);
        dataset.setHighLightColor(Color.BLUE);



        for(i=0;i<floatList.length;i++)
        {
            labels.add("");
        }



        data.setHighlightEnabled(true);
        data.setDrawValues(true);
        data.setValueTextSize(6.0f);
        data.setValueTextColor(Color.BLUE);


        //mLinearChart.setClickable(false);
        mLinearChart.setDrawGridBackground(false);
        mLinearChart.setData(data);
        mLinearChart.enableScroll();
        mLinearChart.getAxisLeft().setDrawLabels(false);
        mLinearChart.getAxisRight().setDrawLabels(true);
        mLinearChart.getAxisRight().setAxisMaxValue(1.0f);
        mLinearChart.getAxisRight().setAxisMinValue(-0.0f);
        mLinearChart.getXAxis().setDrawGridLines(false);
        mLinearChart.getXAxis().setDrawAxisLine(false);
        mLinearChart.getXAxis().setDrawLabels(true);

        mLinearChart.setHorizontalScrollBarEnabled(true);
        mLinearChart.setMinimumHeight(200);
        mLinearChart.setMinimumWidth(200);
        mLinearChart.getLineData().getDataSets().get(0).setColor(Color.BLUE);
        mLinearChart.setScrollX(50);
        mLinearChart.setDescription(description);
        mLinearChart.valuesToHighlight();


        /*
            Interaction with the chart
         */

        mLinearChart.setTouchEnabled(true);



        /*
            Alterando o eixo X
         */
        mLinearChart.setEnabled(true);
        xAxis.setValues(listString);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(20f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);


        /*
            Legenda
         */

        legend.setEnabled(true);
        legend.setTextColor(R.color.black);
        legend.setTextSize(10f);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
        //   legend.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[]{"Dado", "Dado2"});

        /*
            Permitir que quando tocado mostre o valor de x,y
         */
        // highlight the entry and x-position 50 in the first (0) DataSet
        //Highlight highlight = new Highlight(50, 0);



    }
    protected void geraGraficoYIntxint(Vector<Integer> integerVector )
    {

    }
}
