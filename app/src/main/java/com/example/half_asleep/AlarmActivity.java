package com.example.half_asleep;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.TextView;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.widget.EditText;
import android.view.inputmethod.EditorInfo;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.half_asleep.AlarmEntry;
import com.example.half_asleep.AlarmAdapter;

import java.util.ArrayList;
import java.util.List;

public class AlarmActivity extends AppCompatActivity {

    private TimePicker timePicker;
    private Button startAlarmButton;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private AlarmEntry alarmEntry;

    private TextView alarmDetailsTextView;  // TextView 참조 추가

    // 알람 목록을 저장할 리스트
    private List<AlarmEntry> alarmEntries = new ArrayList<>();

    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // 여기에서 알람 액션을 처리
            Toast.makeText(context, "알람이 울립니다!", Toast.LENGTH_SHORT).show();
            // 알람이 울릴 때 알람 액티비티를 시작
            startAlarmActivity();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        timePicker = findViewById(R.id.timePicker);
        startAlarmButton = findViewById(R.id.start_AlarmButton);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        startAlarmButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                setAlarm();
            }
        });

        // RecyclerView 초기화 및 어댑터 설정
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 기존의 알람 목록 초기화
        alarmEntries = new ArrayList<>();

        // 더미 데이터로 초기화 (나중에 데이터베이스 또는 SharedPreferences에서 가져올 수 있음)
        alarmEntries.add(new AlarmEntry(8, 0));
        alarmEntries.add(new AlarmEntry(9, 30));
        alarmEntries.add(new AlarmEntry(12, 15));

        alarmAdapter = new AlarmAdapter(alarmEntries, new OnAlarmClickListener() {
            @Override
            public void onDeleteClick(int position) {
                // 삭제 버튼 클릭 시 실행되는 코드
                alarmEntries.remove(position);
                alarmAdapter.notifyItemRemoved(position);
                alarmAdapter.notifyItemRangeChanged(position, alarmEntries.size());
            }
        });
        recyclerView.setAdapter(alarmAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAlarm() {
        int hour = timePicker.getHour();
        int minute = timePicker.getMinute();

        alarmEntry = new AlarmEntry();
        alarmEntry.setHour(hour);
        alarmEntry.setMinute(minute);

        // 새로운 알람을 추가
        alarmEntries.add(alarmEntry);

        // 알람을 저장한 후 RecyclerView에 업데이트
        alarmAdapter.notifyItemInserted(alarmEntries.size() - 1);

        // 여기에서 AlarmManager를 사용하여 알람을 설정
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);
        alarmManager.set(AlarmManager.RTC_WAKEUP, getTimeInMillis(hour, minute), pendingIntent);
        Toast.makeText(this, hour + "시 " + minute + "분에 알람 설정됨", Toast.LENGTH_SHORT).show();
    }


    private long getTimeInMillis(int hour, int minute) {
        long currentTime = System.currentTimeMillis();
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        calendar.set(java.util.Calendar.HOUR_OF_DAY, hour);
        calendar.set(java.util.Calendar.MINUTE, minute);
        calendar.set(java.util.Calendar.SECOND, 0);
        if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
            calendar.add(java.util.Calendar.DAY_OF_YEAR, 1);
        }
        return calendar.getTimeInMillis();
    }

    private void startAlarmActivity() {
        // 알람 액티비티를 시작하여 알람 내역을 보여줌
        Intent intent = new Intent(this, AlarmActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(alarmReceiver, new IntentFilter("ALARM_ACTION"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(alarmReceiver);
    }
}
