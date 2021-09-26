package com.example.keepwalking;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.kakao.util.helper.Utility.getKeyHash;


public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    public static Context context_main1;

    //Using the Accelometer & Gyroscoper
    private SensorManager mSensorManager;

    //Using the Gyroscope
    private SensorEventListener mGyroLis;
    //    private Sensor mGgyroSensor = null;
    private Sensor mGgyroSensor, mAccelometerSensor, mLinearAcceleration, sensor_step_detector;

    //Using the Accelometer
    private SensorEventListener mAccLis;
    private SensorEventListener mLinLis;

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

    //*************************
    private float[] results;
    private ActivityClassifier classifier;

    private static final int TIME_STAMP = 100;
    private static final String TAG = "MainActivity";
    private static List<Float> accX, accY, accZ;
    private static List<Float> gyroX, gyroY, gyroZ;
    private static List<Float> lx, ly, lz;

    public TextView walkingTextView;
    private TextView step_sensor;
    int steps = 0;
    private ImageView run;
    //*************************

    //    권한
    String[] permission_list = {
//            Manifest.permission.INTERNET,
            Manifest.permission.GET_ACCOUNTS
//            Manifest.permission.READ_PHONE_STATE
    };


    private long timeCountInMilliSeconds = 10000;

    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    private TimerStatus timerStatus = TimerStatus.STOPPED;

    private ProgressBar progressBarCircle;
    private EditText editTextMinute;
    private TextView textViewTime;
    private ImageView imageViewReset;
    private ImageView imageViewStartStop;
    private CountDownTimer countDownTimer;
    public ImageView kakaoLinkBtn;
    private ImageView frontwalking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        // Using the Gyroscope & Accelometer
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Using the Gyroscope
        mGgyroSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
//        mGyroLis = new GyroscopeListener();

        // Using the Accelometer
        mAccelometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//        mAccLis = new AccelometerListener();

        mLinearAcceleration = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        // Step count
        sensor_step_detector = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        // method call to initialize the views
        initViews();
        // method call to initialize the listeners
        initListeners();

        //********************

        accX = new ArrayList<>();
        accY = new ArrayList<>();
        accZ = new ArrayList<>();
        gyroX = new ArrayList<>();
        gyroY = new ArrayList<>();
        gyroZ = new ArrayList<>();

        lx = new ArrayList<>();
        ly = new ArrayList<>();
        lz = new ArrayList<>();

        Log.e("getKeyHash", "" + getKeyHash(this));

        classifier = new ActivityClassifier(getApplicationContext());
//        ShareKakao sh = new ShareKakao();
//        sh.click();

        kakaoLinkBtn.setOnClickListener(v -> {
            FeedTemplate params = FeedTemplate
                    .newBuilder(ContentObject.newBuilder("딥러닝을 통한 보행 건강 예측",
                            "https://res.cloudinary.com/im2015/image/upload/w_1200,h_1200,c_fill,g_center//blog/running_cover_1.jpg",
                            LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                    .setMobileWebUrl("https://developers.kakao.com").build())
                            .setDescrption("측정 결과 확인하기")
                            .build())
                    .setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
                            .setSharedCount(30).setViewCount(40).build())
                    .addButton(new ButtonObject("웹에서 보기", LinkObject.newBuilder().setWebUrl("'https://developers.kakao.com").setMobileWebUrl("'https://developers.kakao.com").build()))
                    .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                            .setWebUrl("'https://developers.kakao.com")
                            .setMobileWebUrl("'https://developers.kakao.com")
                            .setAndroidExecutionParams("key1=value1")
                            .setIosExecutionParams("key1=value1")
                            .build()))
                    .build();

            Map<String, String> serverCallbackArgs = new HashMap<String, String>();
            serverCallbackArgs.put("user_id", "${current_user_id}");
            serverCallbackArgs.put("product_id", "${shared_product_id}");

            KakaoLinkService.getInstance().sendDefault(this, params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Logger.e(errorResult.toString());
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {
                    // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                }
            });
        });
        context_main1 = this;
        Glide.with(this).load(R.drawable.frontwalking).into(frontwalking);
    }

//      HashKey 얻기
//    public static String getKeyHash(final Context context) {
//        PackageManager pm = context.getPackageManager();
//        try {
//            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
//            if (packageInfo == null)
//                return null;
//
//            for (Signature signature : packageInfo.signatures) {
//                try {
//                    MessageDigest md = MessageDigest.getInstance("SHA");
//                    md.update(signature.toByteArray());
//                    return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
//                } catch (NoSuchAlgorithmException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    // 계정 권한 허용
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

    /**
     * method to initialize the views
     */
    private void initViews() {
        progressBarCircle = findViewById(R.id.progressBarCircle);
        textViewTime = findViewById(R.id.textViewTime);
        textViewTime.setTypeface(null, Typeface.BOLD);
        imageViewReset = findViewById(R.id.imageViewReset);
        imageViewStartStop = findViewById(R.id.imageViewStartStop);
        kakaoLinkBtn = findViewById(R.id.imageViewShare);
        walkingTextView = findViewById(R.id.tv_output);
//        run = findViewById(R.id.imageView3);
        frontwalking = findViewById(R.id.imageView3);
        step_sensor = findViewById(R.id.step_sensor);
    }

    /**
     * method to initialize the click listeners
     */
    private void initListeners() {
        imageViewReset.setOnClickListener(this);
        imageViewStartStop.setOnClickListener(this);
    }

    /**
     * implemented method to listen clicks
     *
     * @param view
     */

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageViewReset:
                reset();
                break;
            case R.id.imageViewStartStop:
                startStop();
                break;
        }
    }

    /**
     * method to reset count down timer
     */
    private void reset() {
        stopCountDownTimer();
        startCountDownTimer();
    }

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

    int a = 0;

    /**
     * method to start and stop count down timer
     */
    private void startStop() {
//        ImageView iv = findViewById(R.id.imageView1);
//        final AnimationDrawable drawable = (AnimationDrawable) iv.getBackground();
        if (timerStatus == TimerStatus.STOPPED) {
//            walkingTextView.setText(null);
            // call to initialize the progress bar values
            setProgressBarValues();
            // showing the reset icon
            imageViewReset.setVisibility(View.VISIBLE);
            // changing play icon to stop icon
            imageViewStartStop.setImageResource(R.drawable.icon_stop);
            // changing the timer status to started
            timerStatus = TimerStatus.STARTED;
            // call to start the count down timer
            startCountDownTimer();

            mSensorManager.registerListener(this, mGgyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, mAccelometerSensor, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, mLinearAcceleration, SensorManager.SENSOR_DELAY_FASTEST);
            mSensorManager.registerListener(this, sensor_step_detector, SensorManager.SENSOR_DELAY_NORMAL);

//            drawable.start();

//            Glide.with(this).load(R.drawable.frontwalking).into(frontwalking);
            // gif stop
            ((GifDrawable) frontwalking.getDrawable()).start();

//            int[] location = new int[2];
//
//            run.getLocationOnScreen(location);

//            ArcAnimator.createArcAnimator(run,location[0], location[1], 360, Side.LEFT)
//                    .setDuration(timeCountInMilliSeconds)
//                    .start();

//            Path path = new Path();
//            run.setX(200);
//            run.setY(200);
//            path.addCircle(run.getX(), run.getY(), 200, Path.Direction.CW);
//
//            ViewPathAnimator.animate(run, path, 1000/ 30, 1);

        } else {
            a = 0;

            // gif stop
            ((GifDrawable) frontwalking.getDrawable()).stop();

            // hiding the reset icon
            imageViewReset.setVisibility(View.GONE);
            // changing stop icon to start icon
            imageViewStartStop.setImageResource(R.drawable.icon_start);
            // changing the timer status to stopped
            timerStatus = TimerStatus.STOPPED;
            stopCountDownTimer();

            mSensorManager.unregisterListener(this);

//            drawable.stop();
        }
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {
        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                textViewTime.setText(hmsTimeFormatter(millisUntilFinished));
                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                textViewTime.setText(hmsTimeFormatter(timeCountInMilliSeconds));
                // call to initialize the progress bar values
                setProgressBarValues();
                // hiding the reset icon
                imageViewReset.setVisibility(View.GONE);
                // changing stop icon to start icon
                imageViewStartStop.setImageResource(R.drawable.icon_start);
                // changing the timer status to stopped
                timerStatus = TimerStatus.STOPPED;

                predictActivity(); // 모델 학습
                mSensorManager.unregisterListener(MainActivity.this);
            }

        }.start();
        countDownTimer.start();
    }

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        countDownTimer.cancel();
    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {
        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);
    }


    /**
     * method to convert millisecond to time format
     *
     * @param milliSeconds
     * @return HH:mm:ss time formatted string
     */
    private String hmsTimeFormatter(long milliSeconds) {

        String hms = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(milliSeconds),
                TimeUnit.MILLISECONDS.toMinutes(milliSeconds) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(milliSeconds)),
                TimeUnit.MILLISECONDS.toSeconds(milliSeconds) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliSeconds)));

        return hms;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accX.add(event.values[0]);
            accY.add(event.values[1]);
            accZ.add(event.values[2]);

            Log.e("LOG", "ACCELOMETER           [X]:" + String.format("%.4f", event.values[0])
                    + "           [Y]:" + String.format("%.4f", event.values[1])
                    + "           [Z]:" + String.format("%.4f", event.values[2]));

        } else if (sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX.add(event.values[0]);
            gyroY.add(event.values[1]);
            gyroZ.add(event.values[2]);

            Log.e("LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
                    + "           [Y]:" + String.format("%.4f", event.values[1])
                    + "           [Z]:" + String.format("%.4f", event.values[2])
                    + "           [Pitch]: " + String.format("%.1f", pitch * RAD2DGR)
                    + "           [Roll]: " + String.format("%.1f", roll * RAD2DGR)
                    + "           [Yaw]: " + String.format("%.1f", yaw * RAD2DGR)
                    + "           [dt]: " + String.format("%.4f", dt));
            
        } else if (sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            lx.add(event.values[0]);
            ly.add(event.values[1]);
            lz.add(event.values[2]);
            
        } else { // 걸음수 측정
            switch (event.sensor.getType()) {
                case Sensor.TYPE_STEP_DETECTOR:
                    step_sensor.setText("" + (++steps));
                    break;
            }
        }
//        predictActivity();


        Log.e("Log", "acc크기: " + accX.size());
        Log.e("Log", "gyro크기: " + gyroX.size());
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    private class GyroscopeListener implements SensorEventListener {
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//
//            /* 각 축의 각속도 성분을 받는다. */
//            float gyroXX = event.values[0];
//            float gyroYY = event.values[1];
//            float gyroZZ = event.values[2];
//
//            gyroX.add(gyroXX);
//            gyroY.add(gyroYY);
//            gyroZ.add(gyroZZ);
//
//
//            /* 각속도를 적분하여 회전각을 추출하기 위해 적분 간격(dt)을 구한다.
//             * dt : 센서가 현재 상태를 감지하는 시간 간격
//             * NS2S : nano second -> second */
//            dt = (event.timestamp - timestamp) * NS2S;
//            timestamp = event.timestamp;
//
//            /* 맨 센서 인식을 활성화 하여 처음 timestamp가 0일때는 dt값이 올바르지 않으므로 넘어간다. */
//            if (dt - timestamp * NS2S != 0) {
//
//                /* 각속도 성분을 적분 -> 회전각(pitch, roll)으로 변환.
//                 * 여기까지의 pitch, roll의 단위는 '라디안'이다.
//                 * SO 아래 로그 출력부분에서 멤버변수 'RAD2DGR'를 곱해주어 degree로 변환해줌.  */
//                pitch = pitch + gyroYY * dt;
//                roll = roll + gyroXX * dt;
//                yaw = yaw + gyroZZ * dt;
//
//                a += 1;
//                Log.e("CNT", String.valueOf(a));
//
//                Log.e("LOG", "GYROSCOPE           [X]:" + String.format("%.4f", event.values[0])
//                        + "           [Y]:" + String.format("%.4f", event.values[1])
//                        + "           [Z]:" + String.format("%.4f", event.values[2])
//                        + "           [Pitch]: " + String.format("%.1f", pitch * RAD2DGR)
//                        + "           [Roll]: " + String.format("%.1f", roll * RAD2DGR)
//                        + "           [Yaw]: " + String.format("%.1f", yaw * RAD2DGR)
//                        + "           [dt]: " + String.format("%.4f", dt));
//                predictActivity();
//            }
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    }
//
//    private class AccelometerListener implements SensorEventListener {
//
//        @Override
//        public void onSensorChanged(SensorEvent event) {
//
//            double accXX = event.values[0];
//            double accYY = event.values[1];
//            double accZZ = event.values[2];
//
//            accX.add(event.values[0]);
//            accY.add(event.values[1]);
//            accZ.add(event.values[2]);
//
//            double angleXZ = Math.atan2(accXX, accZZ) * 180 / Math.PI;
//            double angleYZ = Math.atan2(accYY, accZZ) * 180 / Math.PI;
//
//            Log.e("LOG", "ACCELOMETER           [X]:" + String.format("%.4f", event.values[0])
//                    + "           [Y]:" + String.format("%.4f", event.values[1])
//                    + "           [Z]:" + String.format("%.4f", event.values[2])
//                    + "           [angleXZ]: " + String.format("%.4f", angleXZ)
//                    + "           [angleYZ]: " + String.format("%.4f", angleYZ));
//            predictActivity();
//            Log.e("Log", "Acc 크기: " + accX.size());
//            Log.e("Log", "Gyro 크기: " + gyroX.size());
//        }
//
//        @Override
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//
//        }
//    }

    private void predictActivity() {
        List<Float> data = new ArrayList<>();
        if (accX.size() >= TIME_STAMP && accY.size() >= TIME_STAMP && accZ.size() >= TIME_STAMP
                && gyroX.size() >= TIME_STAMP && gyroY.size() >= TIME_STAMP && gyroZ.size() >= TIME_STAMP
                && lx.size() >= TIME_STAMP && ly.size() >= TIME_STAMP && lz.size() >= TIME_STAMP
        ) {

            data.addAll(accX.subList(0, TIME_STAMP));
            data.addAll(accY.subList(0, TIME_STAMP));
            data.addAll(accZ.subList(0, TIME_STAMP));

            data.addAll(gyroX.subList(0, TIME_STAMP));
            data.addAll(gyroY.subList(0, TIME_STAMP));
            data.addAll(gyroZ.subList(0, TIME_STAMP));

            data.addAll(lx.subList(0, TIME_STAMP));
            data.addAll(ly.subList(0, TIME_STAMP));
            data.addAll(lz.subList(0, TIME_STAMP));

            results = classifier.predictProbabilities(toFloatArray(data));
            Log.e("Log", "predictActivity: " + Arrays.toString(results));

            // MainActivity2로 전환
            Intent intent = new Intent(MainActivity.this, MainActivity2.class);

//            intent.putExtra("data", (Serializable) data);

            intent.putExtra("accX", (Serializable) accX);
            intent.putExtra("accY", (Serializable) accY);
            intent.putExtra("accZ", (Serializable) accZ);

            intent.putExtra("gyroX", (Serializable) gyroX);
            intent.putExtra("gyroY", (Serializable) gyroY);
            intent.putExtra("gyroZ", (Serializable) gyroZ);

            intent.putExtra("lx", (Serializable) lx);
            intent.putExtra("ly", (Serializable) ly);
            intent.putExtra("lz", (Serializable) lz);

            String result = judgement(results[0]);
            intent.putExtra("result", result);

            startActivity(intent);
            // MainActivity2 judgement() 호출

            data.clear();
            accX.clear();
            accY.clear();
            accZ.clear();

            gyroX.clear();
            gyroY.clear();
            gyroZ.clear();

            lx.clear();
            ly.clear();
            lz.clear();
        }

    }

    // Normal, abnormal judgment
//    private String judgement(float result1, float result2) {
//        if (result1 >= result2) {
//            return "정상입니다🤓 \t" + results[0];
//        } else {
//            return "비정상입니다😂 \t" + results[1];
//        }
//    }

    private String judgement(float result1) {
        if (result1 > 0.5) {
            return "정상입니다🤓 \t" + results[0];
        } else {
            return "비정상입니다😂 \t" + results[0];
        }
    }

    private float[] toFloatArray(List<Float> data) {
        int i = 0;
        float[] array = new float[data.size()];
        for (Float f : data) {
            array[i++] = (f != null ? f : Float.NaN);
        }
        return array;
    }
}