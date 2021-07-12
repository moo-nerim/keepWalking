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
    NotificationManager Notifi_M;
    MainActivity.ServiceThread thread;

    public GaitService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new MainActivity.ServiceThread(handler);
        thread.stopForever();
        return START_STICKY;
    }

    //서비스가 종료될 때 할 작업
    public void onDestroy() {
        myServiceHandler handler = new myServiceHandler();
        thread = new MainActivity.ServiceThread(handler);
        thread.start();
    }

    public void start() {
        myServiceHandler handler = new myServiceHandler();
        thread = new MainActivity.ServiceThread(handler);
        thread.start();
    }

    public void stop() {
        myServiceHandler handler = new myServiceHandler();
        thread = new MainActivity.ServiceThread(handler);
        thread.stopForever();
    }

    public class myServiceHandler extends Handler {
        @Override
        public void handleMessage(android.os.Message msg) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent intent = new Intent(getApplicationContext(), GaitService.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(GaitService.this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant")
                NotificationChannel notificationChannel = new NotificationChannel("my_notification", "n_channel", NotificationManager.IMPORTANCE_MAX);
                notificationChannel.setDescription("description");
                notificationChannel.setName("Channel Name");
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);
            }
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(GaitService.this).setSmallIcon(R.drawable.appicon).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.appicon)).setContentTitle("Title").setContentText("ContentText").setAutoCancel(true).setSound(soundUri).setContentIntent(pendingIntent).setDefaults(Notification.DEFAULT_ALL).setOnlyAlertOnce(true).setChannelId("my_notification").setColor(Color.parseColor("#ffffff"));
            assert notificationManager != null;
            int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
            Calendar cal = Calendar.getInstance();
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            if (hour == 18) {
                notificationManager.notify(m, notificationBuilder.build());
                thread.stopForever();
            } else if (hour == 22) {
                notificationManager.notify(m, notificationBuilder.build());
                thread.stopForever();
            }
        }
    }
}

}