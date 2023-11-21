package com.example.half_asleep;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class JoinActivity extends AppCompatActivity {
    String myPin;
    String ops;
    String myId;
    private static final int REQUEST_CODE=0;
    public static String BitmapToString(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return temp;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView imageView = findViewById(R.id.prof_img);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);

                    in.close();
                    ops = BitmapToString(img);
                    imageView.setImageBitmap(img);


                } catch (Exception e) {
                }

            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진선택취소", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);
        EditText id,pwd,name,email;
        String url = "http://58.126.238.66:9900/signup";
        String img_path;
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = getSharedPreferences("pin", Activity.MODE_PRIVATE);
        editor = pref.edit();

        myPin = pref.getString("pin","0");
        myId= pref.getString("id","0");
        id = (EditText) findViewById(R.id.join_id);
        pwd = (EditText) findViewById(R.id.join_pwd);
        name = (EditText) findViewById(R.id.join_name);
        email = (EditText) findViewById(R.id.join_email);

        JSONObject jsonObject = new JSONObject();
        ImageView img = findViewById(R.id.prof_img);

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);

            }
        });
        //회원가입버튼
        Button loginButton = findViewById(R.id.join_btn);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String j_id= id.getText().toString();
                String j_pwd= pwd.getText().toString();
                String j_name= name.getText().toString();
                String j_email= email.getText().toString();
                String j_prof = ops;
                try{
                    jsonObject.put("id", j_id);
                    jsonObject.put("pwd", j_pwd);
                    jsonObject.put("name", j_name);
                    jsonObject.put("email", j_email);
                    jsonObject.put("profileImg",j_prof);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    //응답받은 JSONObject 에서 데이터 꺼내오기
                    @Override
                    public void onResponse(JSONObject response) {
                        String message = null;
                        String pin;
                        try {
                            message = response.getString("message");
                            pin = response.getString("pin");
                            editor.putString("pin",pin);
                            editor.putString("pin",j_id);
                            editor.apply();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent(JoinActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(JoinActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(jsonObjectRequest);
            }
        });

    }

}