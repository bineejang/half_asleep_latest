package com.example.half_asleep;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import android.widget.Button;
import com.example.half_asleep.AlarmEntry;

interface OnAlarmClickListener {
    void onDeleteClick(int position);
}

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {
    private List<AlarmEntry> alarmEntries;
    private OnAlarmClickListener onAlarmClickListener;

    public AlarmAdapter(List<AlarmEntry> alarmEntries, OnAlarmClickListener listener) {
        this.alarmEntries = alarmEntries;
        this.onAlarmClickListener = listener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView timeTextView;
        public Button deleteButton;

        public ViewHolder(View view) {
            super(view);
            timeTextView = view.findViewById(R.id.alarm_time);
            deleteButton = view.findViewById(R.id.btn_delete_alarm);

            // 삭제 버튼 클릭 시 이벤트 처리
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onAlarmClickListener.onDeleteClick(position);
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AlarmEntry alarmEntry = alarmEntries.get(position);
        holder.timeTextView.setText(" " + alarmEntry.getHour() + ":" + alarmEntry.getMinute());
    }

    @Override
    public int getItemCount() {
        return alarmEntries.size();
    }
}
