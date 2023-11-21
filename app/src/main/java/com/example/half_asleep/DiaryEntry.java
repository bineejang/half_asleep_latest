package com.example.half_asleep;

public class DiaryEntry {
    private String diary_id;
    private String diaryDate;
    private String imageUrl;

    public DiaryEntry(String diaryId, String diaryDate, String imageUrl) {
        this.diary_id = diaryId;
        this.diaryDate = diaryDate;
        this.imageUrl = imageUrl;
    }

    public String getDiaryId() {
        return diary_id;
    }

    public String getDiaryDate() {
        return diaryDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
