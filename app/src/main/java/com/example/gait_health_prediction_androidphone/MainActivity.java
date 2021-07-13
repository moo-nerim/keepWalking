package com.example.gait_health_prediction_androidphone;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class MainActivity extends AppCompatActivity {

    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager = null;

    //Using the Gyroscope
    private SensorEventListener mGyroLis;
    private Sensor mGgyroSensor = null;

    //Using the Accelometer
    private SensorEventListener mAccLis;
    private Sensor mAccelometerSensor = null;

    //Roll and Pitch
    private double pitch;
    private double roll;
    private double yaw;

    //timestamp and dt
    private double timestamp;
    private double dt;

    // for radian -> dgree
    private double RAD2DGR = 180 / Math.PI;
    private static final float NS2S = 1.0f / 1000000000.0f;

    Chronometer chronometer;
    Button startBtn, pauseBtn, resetBtn;
    long stopTime = 0;

    //    권한
    String[] permission_list = {
//            Manifest.permission.INTERNET,
            Manifest.permission.GET_ACCOUNTS
//            Manifest.permission.READ_PHONE_STATE
    };

//    MyAsyncTask task = new MyAsyncTask();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // AccountPermission class 호출
//        Intent intent = new Intent(getApplicationContext(),AccountPermission.class);
//        startActivity(intent);

        checkPermission();

        // 계정확인

        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.GET_ACCOUNTS}, 1);

        AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        System.out.println(accounts.length);
        for (Account ac : accounts) {
            String acname = ac.name;
            String actype = ac.type;
            // Take your time to look at all available accounts
            System.out.println("Accounts : " + acname + ", " + actype);
            Log.e("Tag", "계정:" + acname);
        }

        chronometer = (Chronometer) findViewById(R.id.chronometer);
        startBtn = (Button) findViewById(R.id.startBtn);
        pauseBtn = (Button) findViewById(R.id.pauseBtn);
        resetBtn = (Button) findViewById(R.id.resetBtn);

        // Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Using the Gyroscope
        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        mGyroLis = new GyroscopeListener();

        // Using the Accelometer
        mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccLis = new AccelometerListener();

        startBtn.setOnClickListener(v -> {
            chronometer.setBase(SystemClock.elapsedRealtime() + stopTime);
            chronometer.start();
            startBtn.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
//            task.execute();
            mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI);
            mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI);
        });

        pauseBtn.setOnClickListener(v -> {
            stopTime = chronometer.getBase() - SystemClock.elapsedRealtime();
            chronometer.stop();
            startBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);
            // Unusing the Gyroscope & Accelometer
            mSensorManager.unregisterListener(mGyroLis);
            mSensorManager.unregisterListener(mAccLis);
        });

        resetBtn.setOnClickListener(v -> {
            chronometer.setBase(SystemClock.elapsedRealtime());
            stopTime = 0;
            chronometer.stop();
            startBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);
            // Unusing the Gyroscope & Accelometer
            mSensorManager.unregisterListener(mGyroLis);
            mSensorManager.unregisterListener(mAccLis);
        });
    }

    public void checkPermission() {
        //현재 안드로이드 버전이 6.0미만이면 메서드를 종료한다.
        //안드로이드6.0 (마시멜로) 이후 버전부터 유저 권한설정 필요
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M)
            return;

        for (String permission : permission_list) {
            //권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);

            if (chk == PackageManager.PERMISSION_DENIED) {
                //권한 허용을여부를 확인하는 창을 띄운다
                requestPermissions(permission_list, 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            for (int i = 0; i < grantResults.length; i++) {
                //허용됬다면
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    //권한을 하나라도 허용하지 않는다면 앱 종료
                    Toast.makeText(getApplicationContext(), "앱권한설정하세요", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }
    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        processCommand(intent);
//
//        super.onNewIntent(intent);
//    }

// 이와 같이 모든 경우에 서비스로부터 받은 인텐트가 처리 될 수 있도록한다.
// 이제 processCommand() 메서드 정의.

//    private void processCommand(Intent intent) {
//        if (intent != null) {
//            String command = intent.getStringExtra("command");
//            String name = intent.getStringExtra("name");
//
////            Toast.makeText(this, "서비스로부터 전달받은 데이터: "+ command + ", " + name).show();
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("LOG", "onPause()");
        mSensorManager.unregisterListener(mGyroLis);
        mSensorManager.unregisterListener(mAccLis);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("LOG", "onDestroy()");
        mSensorManager.unregisterListener(mGyroLis);
        mSensorManager.unregisterListener(mAccLis);
    }

    private class GyroscopeListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            /* 각 축의 각속도 성분을 받는다. */
            double gyroX = event.values[0];
            double gyroY = event.values[1];
            double gyroZ = event.values[2];

            /* 각속도를 적분하여 회전각을 추출하기 위해 적분 간격(dt)을 구한다.
             * dt : 센서가 현재 상태를 감지하는 시간 간격
             * NS2S : nano second -> second */
            dt = (event.timestamp - timestamp) * NS2S;
            timestamp = event.timestamp;

            /* 맨 센서 인식을 활성화 하여 처음 timestamp가 0일때는 dt값이 올바르지 않으므로 넘어간다. */
            if (dt - timestamp * NS2S != 0) {

                /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
                 * 여기까지의 pitch, roll의 단위는 '라디안'이다.
                 * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */
                pitch = pitch + gyroY * dt;
                roll = roll + gyroX * dt;
                yaw = yaw + gyroZ * dt;

                Log.e("LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
                        + "           [Y]:" + String.format("%.4f", event.values[1])
                        + "           [Z]:" + String.format("%.4f", event.values[2])
                        + "           [Pitch]: " + String.format("%.1f", pitch * RAD2DGR)
                        + "           [Roll]: " + String.format("%.1f", roll * RAD2DGR)
                        + "           [Yaw]: " + String.format("%.1f", yaw * RAD2DGR)
                        + "           [dt]: " + String.format("%.4f", dt));
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private class AccelometerListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent event) {

            double accX = event.values[0];
            double accY = event.values[1];
            double accZ = event.values[2];

            double angleXZ = Math.atan2(accX, accZ) * 180 / Math.PI;
            double angleYZ = Math.atan2(accY, accZ) * 180 / Math.PI;

            Log.e("LOG", "ACCELOMETER           [X]:" + String.format("%.4f", event.values[0])
                    + "           [Y]:" + String.format("%.4f", event.values[1])
                    + "           [Z]:" + String.format("%.4f", event.values[2])
                    + "           [angleXZ]: " + String.format("%.4f", angleXZ)
                    + "           [angleYZ]: " + String.format("%.4f", angleYZ));

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }



//    public class MyAsyncTask extends AsyncTask<Void, Integer, String> {
//
//        @Override
//
//        protected String doInBackground(Void... params) {
//            Intent intent = new Intent(getApplicationContext(), GaitService.class); // 실행시키고픈 서비스클래스 이름
//            startService(intent); // 서비스 실행!
//
//            Intent passedIntent = getIntent();
//            processCommand(passedIntent);
//
//            // Using the Gyroscope & Accelometer
//            mSensorManager.registerListener(mGyroLis, mGgyroSensor, SensorManager.SENSOR_DELAY_UI);
//            mSensorManager.registerListener(mAccLis, mAccelometerSensor, SensorManager.SENSOR_DELAY_UI);
//            return "Finish";
//        }
//
//        @Override
//
//        protected void onProgressUpdate(Integer... values) {
//
//        }
//
//        @Override
//
//        protected void onPostExecute(String values) {
//
//        }
//    }


//    public static class ServiceThread extends Thread {
//        Handler handler;
//        boolean isRun = true;
//
//        public ServiceThread(Handler handler) {
//            this.handler = handler;
//        }
//
//        public void stopForever() {
//            synchronized (this) {
//                this.isRun = false;
//            }
//        }
//
//        public void run() { //반복적으로 수행할 작업을 한다.
//            while (isRun) {
//                handler.sendEmptyMessage(0);//쓰레드에 있는 핸들러에게 메세지를 보냄
//                try {
//                    Thread.sleep(1000); //10초씩 쉰다.
//                } catch (Exception e) {
//                }
//            }
//        }
//    }
}