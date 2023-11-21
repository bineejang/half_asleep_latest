package com.example.half_asleep;

import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import com.bumptech.glide.Glide;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;

public class CompleteDiaryActivity extends AppCompatActivity {
    String myPin;
    String myId;

    int diary_id=0;
    SimpleDateFormat mFormat = new SimpleDateFormat("yyyy.MM.dd");
    private Bitmap sketch=null;
    byte[] baos;
    String prompt;
    long mNow;
    Date mDate;
    private String getTime(){
        mNow = System.currentTimeMillis();
        mDate = new Date(mNow);
        return mFormat.format(mDate);
    }
    public static String BitmapToString(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return temp;
    }
    private Integer parseData(JSONObject object) {
        int pt=0;
        try {
            pt=object.getInt("diary_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return pt;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_diary);
        String url_d = "http://58.126.238.66:9900/save_diary";
        String url_i = "http://58.126.238.66:9900/save_image";
        EditText content = findViewById(R.id.et_content);
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        ImageView img = findViewById(R.id.iv_foodpic);

        SharedPreferences pref = getSharedPreferences("my_prefs", 0);
        myId = pref.getString("id", "");
        myPin = pref.getString("pin", "0");
        Toast.makeText(CompleteDiaryActivity.this, "PIN: " + myPin.toString(), Toast.LENGTH_SHORT).show();

        Button complete = findViewById(R.id.btn_finish_i2i);
        prompt = getIntent().getStringExtra("prompt");
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        img.setImageBitmap(bitmap);
        baos = getIntent().getByteArrayExtra("sketch");
        sketch = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        String diary = getIntent().getStringExtra("diary");
        String prompt = getIntent().getStringExtra("prompt");
        content.setText(diary);

        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                JSONObject jsonObject_d = new JSONObject();
                String diary = content.getText().toString();
                String date=getTime();
                try {
                    jsonObject_d.put("content", diary);
                    jsonObject_d.put("pin",myPin);
                    jsonObject_d.put("date",date);
                    jsonObject_d.put("prompt",prompt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest_d = new JsonObjectRequest(Request.Method.POST, url_d, jsonObject_d, new Response.Listener<JSONObject>() {
                    //응답받은 JSONObject 에서 데이터 꺼내오기
                    @Override
                    public void onResponse(JSONObject response) {
                        diary_id=parseData(response);
                        Toast.makeText(CompleteDiaryActivity.this, "response: " + response.toString(), Toast.LENGTH_SHORT).show();
                        JSONObject jsonObject_i = new JSONObject();
                        String image = BitmapToString(bitmap);
                        String image2 = BitmapToString(sketch);
                        try {
                            jsonObject_i.put("diary_id",diary_id);
                            jsonObject_i.put("image", image);
                            jsonObject_i.put("sketch", image2);
                            jsonObject_i.put("prompt", prompt);
                            if(sketch ==null){
                                jsonObject_i.put("method", 1);
                            }else{
                                jsonObject_i.put("method", 2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        JsonObjectRequest jsonObjectRequest_i = new JsonObjectRequest(Request.Method.POST, url_i, jsonObject_i, new Response.Listener<JSONObject>() {
                            //응답받은 JSONObject 에서 데이터 꺼내오기
                            @Override
                            public void onResponse(JSONObject response) {
                                Intent intent = new Intent(CompleteDiaryActivity.this,MainActivity.class);
                                startActivity(intent);
                                //parseData라는 함수의 변수 response는 서버로붙어 받은 응답,imageView는 이미지를 띄울 xml파일의 imageView,reprompt는 서버로 전송한 프롬프트가 맞게 들어갔는지 서버에서 사용한 걸 그대로보내주는 프롬프트
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(CompleteDiaryActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        queue.add(jsonObjectRequest_i);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CompleteDiaryActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });jsonObjectRequest_d.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonObjectRequest_d);
            }
        });
        // Today's date TextView
        TextView todayDateTextView = findViewById(R.id.tv_today_date_complete);
        String currentDate = getTime();
        todayDateTextView.setText(currentDate);
    }

}