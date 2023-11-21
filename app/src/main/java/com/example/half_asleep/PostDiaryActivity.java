package com.example.half_asleep;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class PostDiaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_diary);

        // SharedPreferences에서 사용자 Pin 값을 가져오기
        SharedPreferences pref = getSharedPreferences("my_prefs", Context.MODE_PRIVATE);
        String userPin = pref.getString("pin", "default_pin_value");

        Intent intent = getIntent();
        String diaryId = intent.getStringExtra("diaryId");


        // PostDiaryActivity에서 이미지를 받아 디코딩
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        // ImageView에 설정
        ImageView ivFoodPic = findViewById(R.id.iv_foodpic);
        ivFoodPic.setImageBitmap(bitmap);

        // EditText 참조
        EditText contentEditText = findViewById(R.id.et_content);

        // 작성 완료 버튼 참조
        Button finishButton = findViewById(R.id.btn_post);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // EditText에서 작성한 내용 가져오기
                String content = contentEditText.getText().toString();

                if (diaryId != null) {
                    // 게시글 작성 요청을 서버로 보내기
                    postDiary(userPin, diaryId, content);
                }
            }
        });

        // 취소 버튼 참조
        Button cancelButton = findViewById(R.id.btn_cancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 이전 화면으로 돌아가기 (예: finish())
                finish();
            }
        });
    }

    private void postDiary(String userPin, String diaryId, String content) {
        String url = "http://58.126.238.66:9900/post_write";

        // JSON 객체를 생성하여 요청 데이터 설정
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("userPin", userPin);
            jsonBody.put("diaryId", diaryId);
            jsonBody.put("content", content);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBody.toString();

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 서버 응답 처리

                        // MainActivity로 돌아가기
                        Intent mainIntent = new Intent(PostDiaryActivity.this, MainActivity.class);
                        startActivity(mainIntent);
                        finish(); // 현재 액티비티 종료
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        queue.add(stringRequest);
    }
}