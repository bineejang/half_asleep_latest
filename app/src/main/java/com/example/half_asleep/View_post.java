package com.example.half_asleep;

import static com.example.half_asleep.Viewprofile.StringToBitmap;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class View_post extends AppCompatActivity {
    String myPin;
    ArrayList<CommentEntry> list = new ArrayList<CommentEntry>();
    CommentEntry Entry;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_commu_detail);
        String url = "http://58.126.238.66:9900/posts/";
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = getSharedPreferences("my_prefs", 0);
        myPin = pref.getString("pin","");
        String postID = getIntent().getStringExtra("postID");
        url = url + postID;
        ImageView postimg = findViewById(R.id.iv_foodpic);
        TextView Date = findViewById(R.id.tv_date);
        EditText content = findViewById(R.id.et_content);
        ImageView Edit_post = findViewById(R.id.iv_edit);
        ImageView Del_post = findViewById(R.id.iv_trash);

        //댓글작성 관련 수정한 부분
        EditText comment = findViewById(R.id.comment_text);
        Button commentwrite= findViewById(R.id.comment_send);
        ImageView post_prf=findViewById(R.id.post_prf);
        TextView name = findViewById(R.id.post_id);

        commentwrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://58.126.238.66:9900/comment_write";
                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("postID", postID);
                    jsonObject.put("pin", myPin);
                    jsonObject.put("content", comment.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String requestBody = jsonObject.toString();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Intent intent = new Intent(View_post.this, View_post.class);
                                intent.putExtra("postID",postID);
                                startActivity(intent);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error
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
                            VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                            return null;
                        }
                    }
                };
                queue.add(stringRequest);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.comment);

        Edit_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(View_post.this, Edit_post.class);
                BitmapDrawable drawable = (BitmapDrawable) postimg.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("image", byteArray);
                intent.putExtra("content",content.getText().toString());
                intent.putExtra("postID",postID);
                startActivity(intent);
            }
        });

        Del_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteConfirmationDialog();
            }
        });

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String pin;
                    postimg.setImageBitmap(StringToBitmap(response.getString("image_data")));
                    Date.setText(formatDate(response.getString("post_date")));
                    content.setText(response.getString("post_content"));
                    pin=response.getString("pin");

                    post_prf.setImageBitmap(StringToBitmap(response.getString("member_prf")));
                    name.setText(response.getString("member_name"));

                    if(!pin.equals(myPin)){
                        Edit_post.setVisibility(View.GONE);
                        Del_post.setVisibility(View.GONE);
                    }
                    CommentAdapter commentAdapter = new CommentAdapter(list);
                    String url_c = "http://58.126.238.66:9900/comments/";
                    url_c = url_c+postID;
                    JsonArrayRequest jsonObjectRequest_c = new JsonArrayRequest(Request.Method.GET, url_c, null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject idobj = response.getJSONObject(i);
                                    Entry = new CommentEntry(
                                            idobj.getString("comment_id"),
                                            idobj.getString("member_name"),
                                            idobj.getString("comment_date"),
                                            idobj.getString("member_prf"),
                                            idobj.getString("pin"),
                                            idobj.getString("comment_content")
                                    );
                                    list.add(Entry);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            recyclerView.setAdapter(commentAdapter);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            content.setText(error.toString());
                        }
                    });
                    queue.add(jsonObjectRequest_c);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(View_post.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);

        updateEditTextHeight();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("게시물을 삭제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deletePost();
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing or handle cancel
                    }
                })
                .show();
    }

    private int calculateEditTextHeight(Holder holder, String text) {
        // 원하는 최대 높이 설정
        int maxHeight = 1000;

        // 높이 계산을 위해 StaticLayout 사용
        TextPaint textPaint = holder.content.getPaint();
        int textWidth = holder.content.getWidth() - holder.content.getPaddingLeft() - holder.content.getPaddingRight();

        // 다중 줄 텍스트에 대해 StaticLayout 사용
        StaticLayout staticLayout = null;
        try {
            staticLayout = new StaticLayout(text, textPaint, textWidth, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } catch (IllegalArgumentException e) {
            // 레이아웃 높이 음수 예외 처리
            e.printStackTrace();
            return 10; // 높이가 음수이면 10으로 반환
        }

        // 텍스트가 너무 길어서 maxHeight를 초과하는 경우 예외 처리
        int lineCount = Math.min(staticLayout.getLineCount(), 1000); // 예: 최대 1000줄까지만 허용

        // 최대 높이로 높이 설정
        int desiredHeight = Math.min(lineCount * Math.round(textPaint.getFontSpacing()), maxHeight);

        return desiredHeight;
    }

    private void updateEditTextHeight() {
        Holder holder = new Holder();
        holder.content = findViewById(R.id.et_content);

        // 서버에서 내용 텍스트 가져오기
        String serverText = "서버에서 받은 텍스트";  // 실제로 받은 텍스트로 교체

        // EditText에 텍스트 설정
        holder.content.setText(serverText);

        // 최대 라인 수를 설정
        int maxLines = 1000;  // 예: 최대 1000줄까지만 허용
        holder.content.setMaxLines(maxLines);
    }

    private void deletePost() {
        String url = "http://58.126.238.66:9900/posts/";
        url = url + getIntent().getStringExtra("postID");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Toast.makeText(View_post.this, "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(View_post.this, "삭제 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(jsonObjectRequest);
    }

    class Holder {
        EditText content;  // 다른 뷰가 필요한 경우 추가
    }
}