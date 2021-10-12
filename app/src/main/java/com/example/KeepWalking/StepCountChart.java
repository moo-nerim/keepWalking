package com.example.keepwalking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class StepCountChart extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count_chart);

        int[] Fsteps = {0, 0, 0, 0, 0, 0, 0};

        // 일주일 구하기
//        c = Calendar.getInstance().getTime();
//        df = new SimpleDateFormat("yyyy-MM-dd");
//        formattedDate = df.format(c);
//
        SimpleDateFormat dfDate = new SimpleDateFormat("yyyy-MM-dd");
        ArrayList<String> days = new ArrayList<>();
//        String TODAY = dfDate.format(new Date());

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        days.add(dfDate.format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.TUESDAY);
        days.add(dfDate.format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.WEDNESDAY);
        days.add(dfDate.format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.THURSDAY);
        days.add(dfDate.format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
        days.add(dfDate.format(c.getTime()));

        c.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
        days.add(dfDate.format(c.getTime()));

        c.add(Calendar.DATE, 1);
        days.add(dfDate.format(c.getTime()));

//        ArrayList<Integer> Fsteps = new ArrayList<>();
//        Fsteps.add(0,0);
//        Fsteps.add(1,0);
//        Fsteps.add(2,0);
//        Fsteps.add(3,0);
//        Fsteps.add(4,0);
//        Fsteps.add(5,0);
//        Fsteps.add(6,0);

        BarChart barChart = findViewById(R.id.barchart);
        ArrayList<BarEntry> barEntries = new ArrayList<>();

        for (int j = 0; j < 7; j++) {
            final int index;
            index = j;
            Log.e("배열값: ", "" + Fsteps[index]);
        }

        for (int i = 0; i < 7; i++) {
            final int index;
            index = i;
            // 걸음수 Firebase 저장

            databaseReference.child("KAKAOID").child(((GlobalApplication) getApplication()).getKakaoID()).child("STEPS").child(days.get(index)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

//                    snapshot.child(days.get(index));
                    if (snapshot.getValue() != null) {
                        Log.e("날짜:", "" + snapshot.getValue(Integer.class));
                        Fsteps[index] = (snapshot.getValue(Integer.class));
                        Log.e("인덱스", "" + Fsteps[index]);
//                        barEntries.add(new BarEntry(index, Fsteps[index]));

                        Log.e("타입", "" + snapshot.getValue(Integer.class).getClass().getName());
                    }
                    else{
//                        barEntries.add(new BarEntry(index, 0));
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // 디비를 가져오던중 에러 발생 시
                    //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                }
            });
            Log.e("" + index, "" + Fsteps[index]);
        }


        Log.e("11: ", "" + Fsteps[0]);
        Log.e("22: ", "" + Fsteps[1]);
        Log.e("33: ", "" + Fsteps[2]);
        Log.e("44: ", "" + Fsteps[3]);
        Log.e("55: ", "" + Fsteps[4]);
        Log.e("66: ", "" + Fsteps[5]);
        Log.e("77: ", "" + Fsteps[6]);


        barEntries.add(new BarEntry(0f, Fsteps[0]));
        barEntries.add(new BarEntry(1f, Fsteps[1]));
        barEntries.add(new BarEntry(2f, Fsteps[2]));
        barEntries.add(new BarEntry(3f, Fsteps[3]));
        barEntries.add(new BarEntry(4f, Fsteps[4]));
        barEntries.add(new BarEntry(5f, Fsteps[5]));
        barEntries.add(new BarEntry(6f, Fsteps[6]));
        BarDataSet barDataSet = new BarDataSet(barEntries, "Dates");
        ArrayList<String> theDates = new ArrayList<>();
        theDates.add("월");
        theDates.add("화");
        theDates.add("수");
        theDates.add("목");
        theDates.add("금");
        theDates.add("토");
        theDates.add("일");
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(theDates));
        BarData theData = new BarData(barDataSet);//----Line of error
        barChart.setData(theData);
        barChart.setTouchEnabled(true);
        barChart.setDragEnabled(true);
        barChart.setScaleEnabled(true);
    }

}