package com.example.half_asleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class AlarmReleaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_release);

        Button releaseButton = findViewById(R.id.releaseButton);
        releaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알람 해제 로직을 여기에 추가
                cancelAlarm();
                // 알람 해제 후 다른 액티비티로 이동하는 코드 추가
                Intent intent = new Intent(AlarmReleaseActivity.this, WriteDiaryActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void cancelAlarm() {
        // 알람 해제 코드를 추가
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }
}