package com.example.half_asleep;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class LoginActivity extends AppCompatActivity {
    String myPin;
    String myId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = getSharedPreferences("my_prefs", 0); // 동일한 이름으로 SharedPreferences 객체 생성
        editor = pref.edit();

        myPin = pref.getString("pin", "0");

        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        EditText id = findViewById(R.id.login_id);
        EditText pwd = findViewById(R.id.login_pwd);
        JSONObject jsonObject = new JSONObject();

        String url = "http://58.126.238.66:9900/login";
        Button btnLogin = findViewById(R.id.login_btn);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String j_id = id.getText().toString();
                String j_pwd = pwd.getText().toString();
                myId = j_id;
                try {
                    jsonObject.put("id", j_id);
                    jsonObject.put("pwd", j_pwd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    //응답받은 JSONObject 에서 데이터 꺼내오기
                    @Override
                    public void onResponse(JSONObject response) {
                        String message = null;
                        String pin = null;
                        try {
                            pin = response.getString("pin");
                            message = response.getString("message");
                            editor.putString("pin",pin);
                            editor.putString("id",id.getText().toString());
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("pin", pin); // PIN 데이터를 전달
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(LoginActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(jsonObjectRequest);
            }//onClick
        });//btnlogin.setOnClickListener
        TextView gotoJoin = findViewById(R.id.goto_join);
        gotoJoin.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);    //'회원가입'에 밑줄치기
        gotoJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //회원가입 클릭시 호출
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });
    }
}