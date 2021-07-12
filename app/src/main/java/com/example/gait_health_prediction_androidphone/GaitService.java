package com.example.gait_health_prediction_androidphone;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import static android.content.ContentValues.TAG;

public class GaitService extends Service {

    public GaitService() {
    }


    @Override
    public void onCreate(){
        super.onCreate();
        // 서비스는 한번 실행되면 계속 실행된 상태로 있는다.
        // 따라서 서비스 특성상 intent를 받아서 처리하기에 적합하지않다.
        // intent에 대한 처리는 onStartCommand()에서 처리해준다.
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        Log.d(TAG, "onDestroy() called");
    }

    @Override
    public IBinder onBind(Intent intent){
        throw new UnsupportedOperationException("Not yet Implemented"); //자동으로 작성되는 코드
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d(TAG, "onStartCommand() called");

        if (intent == null) {
            return Service.START_STICKY; //서비스가 종료되어도 자동으로 다시 실행시켜줘!
        } else {
            String command = intent.getStringExtra("command");
            String name = intent.getStringExtra("name");

            //Log.d(TAG, "전달받은 데이터: " + command+ ", " +name);

            try{
                Thread.sleep(5000); //5초동안 정지
            } catch(Exception e) {}

            Intent showIntent = new Intent(getApplicationContext(), MainActivity.class);

            /**
             화면이 띄워진 상황에서 다른 액티비티를 호출하는 것은 문제가없으나,
             지금은 따로 띄워진 화면이 없는 상태에서 백그라운드에서 실행중인 서비스가 액티비티를 호출하는 상황이다.
             이러한 경우에는 FLAG_ACTIVITY_NEW_TASK 옵션(플래그)을 사용해줘야 한다.
             **/
            showIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            showIntent.putExtra("command", "show");
            showIntent.putExtra("name", name + " from service.");

            // *** 이제 완성된 인텐트와 startActivity()메소드를 사용하여 MainActivity 액티비티를 호출한다. ***
            startActivity(showIntent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

}