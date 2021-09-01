package com.example.gait_health_prediction_androidphone;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    // Normal, abnormal judgment
    private void judgement(float result1, float result2) {
        if (result1 >= result2) {
            walkingTextView.setText("정상입니다🤓 \t" + results[0]);
        }
        else{
            walkingTextView.setText("비정상입니다😂 \t" + results[1]);
        }
    }
}