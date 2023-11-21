package com.example.half_asleep;

import android.graphics.Bitmap;

public class SendRequest {
    private String member_id;
    private Bitmap member_prf;
    private String member_name;

    public SendRequest(String member_id, String member_name, Bitmap member_prf) {
        this.member_id = member_id;
        this.member_prf = member_prf;
        this.member_name = member_name;
    }

    public String getMemberId() {
        return member_id;
    }

    public Bitmap getMemberPrf() {
        return member_prf;
    }

    public String getMemberName() {
        return member_name;
    }
}