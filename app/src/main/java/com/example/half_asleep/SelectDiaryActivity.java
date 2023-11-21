package com.example.half_asleep;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import android.widget.Button;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import java.io.ByteArrayOutputStream;

public class SelectDiaryActivity extends AppCompatActivity {

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
    private String updateUrl = "http://58.126.238.66:9900/edit_diary";


    // 수정 상태 여부를 나타내는 플래그
    private boolean isEditing = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_diary);

        String diaryId = getIntent().getStringExtra("diary_id");

        // EditText 및 저장 버튼 참조
        EditText contentEditText = findViewById(R.id.et_content);
        ImageView imageView = findViewById(R.id.iv_foodpic);
        TextView DateTextView = findViewById(R.id.diary_date);// TextView 참조
        ImageView ivEdit = findViewById(R.id.iv_edit);
        ImageView ivBack = findViewById(R.id.iv_back);
        ImageView ivCheck = findViewById(R.id.iv_check);
        ImageView ivCommuShare = findViewById(R.id.iv_commu_share);
        ImageView ivTrash = findViewById(R.id.iv_trash);

        contentEditText.setEnabled(false); // 초기에는 수정 불가능 상태

        ivEdit.setOnClickListener(v -> {
            isEditing = true;
            contentEditText.setEnabled(true);
            ivEdit.setVisibility(View.GONE);
            ivBack.setVisibility(View.VISIBLE);
            ivCheck.setVisibility(View.VISIBLE);

            // iv_commu_share와 iv_trash를 숨김
            ivCommuShare.setVisibility(View.GONE);
            ivTrash.setVisibility(View.GONE);
        });

        ivBack.setOnClickListener(v -> {
            isEditing = false;
            contentEditText.setEnabled(false);
            ivEdit.setVisibility(View.VISIBLE);
            ivBack.setVisibility(View.GONE);
            ivCheck.setVisibility(View.GONE);

            // iv_commu_share와 iv_trash를 다시 표시
            ivCommuShare.setVisibility(View.VISIBLE);
            ivTrash.setVisibility(View.VISIBLE);
            // 기존 내용으로 되돌리는 로직 필요
        });

        ivCheck.setOnClickListener(v -> {
            if (isEditing) {
                String updatedContent = contentEditText.getText().toString();
                updateDiaryContent(diaryId, updatedContent);
                isEditing = false;
                contentEditText.setEnabled(false);
                ivEdit.setVisibility(View.VISIBLE);
                ivBack.setVisibility(View.GONE);
                ivCheck.setVisibility(View.GONE);
            }
        });

        ivTrash.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(SelectDiaryActivity.this);
            builder.setTitle("정말로 삭제 하시겠습니까?")        // 제목 설정
                    .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                    .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                        // 확인 버튼 클릭시 설정, 오른쪽 버튼입니다.
                        public void onClick(DialogInterface dialog, int whichButton){
                            deleteDiary(diaryId);
                            finish(); // 현재 화면을 닫음
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        // 취소 버튼 클릭시 설정, 왼쪽 버튼입니다.
                        public void onClick(DialogInterface dialog, int whichButton){
                            //원하는 클릭 이벤트를 넣으시면 됩니다.
                            finish();
                        }
                    });

            AlertDialog dialog = builder.create();    // 알림창 객체 생성
            dialog.show();    // 알림창 띄우기
        });


        ivCommuShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // "게시글 작성" 버튼을 클릭했을 때 PostDiaryActivity로 이동
                Intent intent = new Intent(SelectDiaryActivity.this, PostDiaryActivity.class);

                // ImageView에서 이미지를 추출하여 바이트 배열로 변환
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                // 바이트 배열을 다음 화면으로 전달
                intent.putExtra("image", byteArray);

                // diaryId를 다음 화면으로 전달
                intent.putExtra("diaryId", diaryId);

                // 이후 다음 화면(PostDiaryActivity)로 이동
                startActivity(intent);
                finish();
            }
        });


        // 서버로부터 데이터 요청 (Volley 사용)
        String url = "http://58.126.238.66:9900/get_diary_info";

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("diary_id", diaryId); // 선택한 일기의 ID를 요청에 포함
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String[] imageUrl = {""}; // imageUrl 변수를 미리 선언
        String requestBody = jsonBody.toString(); // requestBody 변수를 선언하고 초기화

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // JSON 데이터 파싱
                            JSONObject jsonObject = new JSONObject(response);
                            String diaryDate = jsonObject.getString("diary_date");
                            String diaryContent = jsonObject.getString("diary_content");

                            // 데이터를 UI에 설정
                            contentEditText.setText(diaryContent);

                            JSONArray imagesArray = jsonObject.getJSONArray("images");
                            if (imagesArray.length() > 0) {
                                JSONObject firstImageObject = imagesArray.getJSONObject(0);
                                String firstImageData = firstImageObject.getString("image_data");
                                imageUrl[0] = "data:image/jpeg;base64," + firstImageData;
                            }

                            Glide.with(SelectDiaryActivity.this).load(imageUrl[0]).into(imageView);

                            // 날짜 포맷 변경
                            String formattedDate = formatDate(diaryDate);
                            DateTextView.setText(formattedDate); // 형식화된 날짜 표시
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // 오류 처리
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void updateDiaryContent(String diaryId, String updatedContent) {
        String url = updateUrl;
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("diary_id", diaryId);
            jsonBody.put("diary_content", updatedContent);
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void deleteDiary(String diaryId) {
        String deleteUrl = "http://58.126.238.66:9900/delete_diary"; // 삭제 요청을 보낼 URL

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("diary_id", diaryId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final String requestBody = jsonBody.toString();
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // 서버 응답 처리
                        // 삭제가 성공하면 해당 화면을 닫을 수 있도록 코드를 추가하세요 (예: finish())
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        // 오류 처리
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

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
