package com.example.half_asleep;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommentEntry {
    private String comment_id;
    private String username;
    private String postDate;
    private String profileImage;
    private String pin;
    private String content;
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
    public CommentEntry(String commentid, String username,String postDate, String profileImage,String pin,String content) {
        this.comment_id = commentid;
        this.username = username;
        this.postDate = formatDate(postDate);
        this.profileImage = profileImage;
        this.pin = pin;
        this.content = content;
    }

    public String getcomment_id() {
        return comment_id;
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
    public String getpin() { return pin; }
    public String getcontent() {
        return content;
    }
}