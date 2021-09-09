package com.example.gait_health_prediction_androidphone;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {

    public static Context context_main2;
    private LineChart chart;

    // Graph 그리기
    private LineGraphSeries<DataPoint> mSeriesAccelX, mSeriesAccelY, mSeriesAccelZ;
    private GraphView mGraphAccel;
    private double graphLastAccelXValue = 10d;
    private GraphView line_graph;
    TextView xValue, yValue, zValue;
    private TextView walkingTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        walkingTextView = findViewById(R.id.tv_output);

        context_main2 = this;

        // 데이터 수신
        Intent intent = getIntent();
//        ArrayList<Float> data = (ArrayList<Float>) intent.getSerializableExtra("data");

        ArrayList<Float> accX = (ArrayList<Float>) intent.getSerializableExtra("accX");
        ArrayList<Float> accY = (ArrayList<Float>) intent.getSerializableExtra("accY");
        ArrayList<Float> accZ = (ArrayList<Float>) intent.getSerializableExtra("accZ");

        ArrayList<Float> gyroX = (ArrayList<Float>) intent.getSerializableExtra("gyroX");
        ArrayList<Float> gyroY = (ArrayList<Float>) intent.getSerializableExtra("gyroY");
        ArrayList<Float> gyroZ = (ArrayList<Float>) intent.getSerializableExtra("gyroZ");

        ArrayList<Float> lx = (ArrayList<Float>) intent.getSerializableExtra("lx");
        ArrayList<Float> ly = (ArrayList<Float>) intent.getSerializableExtra("ly");
        ArrayList<Float> lz = (ArrayList<Float>) intent.getSerializableExtra("lz");

        String result = intent.getStringExtra("result");
        walkingTextView.setText(result);

//        Log.e("LOG", accX.size() + "," + accY.size() + "," + accZ.size());

        // here

        chart = findViewById(R.id.chart);
        ArrayList<Entry> entry1 = new ArrayList<>();
        ArrayList<Entry> entry2 = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            float val = (float) (Math.random() * 10);
            float x, y;
            x = accX.get(i);
            y = accY.get(i);
            entry1.add(new Entry(i, val));
            entry2.add(new Entry(i, val + 10));
        }

        LineDataSet set1,set2;
        set1 = new LineDataSet(entry1, "사용자");
        set2 = new LineDataSet(entry2, "정상인");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        dataSets.add(set2);

        LineData dat = new LineData(dataSets);

        // ******그래프 디자인*********
        // 사용자 측정 Graph
        set1.setColor(Color.rgb(153, 204, 255));
        set1.setCircleColor(Color.rgb(153, 204, 255));
        set1.setCircleRadius(3f);
        set1.setLineWidth(2);
        set1.setDrawFilled(true); // 차트 아래 fill(채우기) 설정
        set1.setFillColor(Color.rgb(212, 248, 253));
        set1.setValueTextSize(10f);

        // 정상 Graph
        set2.setColor(Color.rgb(255, 51, 153));
        set2.setCircleColor(Color.rgb(255, 51, 153));
        set2.setCircleRadius(3f);
        set2.setLineWidth(2);
        set2.setValueTextSize(10f);

        // y축 오른쪽 Label remove
        YAxis yAxisRight = chart.getAxisRight(); //Y축의 오른쪽면 설정
        yAxisRight.setDrawLabels(false);
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        //*************************

        chart.getDescription().setEnabled(false); // 하단 regend remove
        chart.setData(dat);


//        Log.e("Log", String.valueOf(data));
    }

//    // Normal, abnormal judgment
//    public void judgement(float result1, float result2) {
//        TextView walkingTextView = findViewById(R.id.tv_output);
//        if (result1 >= result2) {
//            walkingTextView.setText("정상입니다🤓 \t" + result1);
//        } else {
//            walkingTextView.setText("비정상입니다😂 \t" + result2);
//        }
//    }
}