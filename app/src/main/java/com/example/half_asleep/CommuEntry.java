package com.example.half_asleep;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommuEntry {
    private String post_id;
    private String username;
    private String postDate;
    private String profileImage;
    private String postImage;
    private String content;
    public CommuEntry(String diaryId, String username,String postDate, String profileImage,String postImage,String content) {
        this.post_id = diaryId;
        this.username = username;
        this.postDate = formatDate(postDate);
        this.profileImage = profileImage;
        this.postImage = postImage;
        this.content = content;
    }
    private String formatDate(String dateString) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH);
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.ENGLISH);
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateString; // 포맷 변경에 실패한 경우, 원래 날짜 값을 그대로 반환
        }
    }
    public String getPost_id() {
        return post_id;
    }
    public String getusername() {
        return username;
    }
    public String getpostDate() {
        return postDate;
    }
    public String getprofileImage() {
        return profileImage;
    }
    public String getpostImage() {
        return postImage;
    }
    public String getcontent() {
        return content;
    }
}