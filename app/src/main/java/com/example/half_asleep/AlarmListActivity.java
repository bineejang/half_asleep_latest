package com.example.half_asleep;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.half_asleep.AlarmEntry;
import java.util.ArrayList;
import java.util.List;


public class AlarmListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private AlarmAdapter alarmAdapter;
    private List<AlarmEntry> alarmEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_setting);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // 더미 데이터로 초기화 (나중에 데이터베이스 또는 SharedPreferences에서 가져올 수 있음)
        alarmEntries = new ArrayList<>();
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
}
