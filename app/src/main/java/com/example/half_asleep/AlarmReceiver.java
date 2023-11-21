package com.example.half_asleep;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.app.PendingIntent;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.app.NotificationManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;


public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("AlarmReceiver", "알람이 울립니다!");

        playAlarmSound(context);
    }

    private void playAlarmSound(Context context) {
        // 알람 소리를 재생하는 코드를 추가
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
        mediaPlayer.start();

        // 알람 소리를 재생한 후 알람 알림을 표시
        showAlarmNotification(context);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showAlarmNotification(Context context) {
        // 채널 생성 및 설정
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "my_channel_id";
            String channelName = "My Channel Name";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Intent notificationIntent = new Intent(context, AlarmReleaseActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "my_channel_id")
                .setContentTitle("알람")
                .setContentText("알람이 울립니다!")
                .setSmallIcon(R.drawable.ic_notification) // 알림 아이콘 리소스 설정
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

}