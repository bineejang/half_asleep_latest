package com.example.half_asleep;

public class AlarmEntry {
    private int id;
    private int hour;
    private int minute;

    public AlarmEntry() {
        // 기본 생성자
    }

    public AlarmEntry(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    // Getter 및 Setter 메서드
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }
}
