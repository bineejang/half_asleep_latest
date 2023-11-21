package com.example.half_asleep;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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


public class t2iActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    String prompt;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_t2i);
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        ImageView imageView = findViewById(R.id.iv_t2i);
        ImageView feedback = findViewById(R.id.iv_upload);
        ImageView remake = findViewById(R.id.iv_redraw_t2i);
        progressBar = findViewById(R.id.progressBar);
        JSONObject jsonObject = new JSONObject();
        Button sendBtn = findViewById(R.id.btn_finish_t2i);
        String url = "http://58.126.238.66:9900/generate_image_t2i_diary";
        EditText editText = findViewById(R.id.et_t2i_add_prompt);
        String diary = getIntent().getStringExtra("diary");

        try {
            jsonObject.put("diary_content",diary);
            progressBar.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            //응답받은 JSONObject 에서 데이터 꺼내오기
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                parseData(response,imageView);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(t2iActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);

        remake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://58.126.238.66:9900/generate_image_t2i_remake";
                progressBar.setVisibility(View.VISIBLE);
                JSONObject jsonObject= new JSONObject();
                try {
                    jsonObject.put("prompt",prompt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    //응답받은 JSONObject 에서 데이터 꺼내오기
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        parseData(response,imageView);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(t2iActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonObjectRequest);
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(t2iActivity.this, CompleteDiaryActivity.class);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("image", byteArray);
                intent.putExtra("diary", diary);
                intent.putExtra("prompt",prompt);
                startActivity(intent);
                finish();
            }
        });
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE); // 이미지 로딩 전에 ProgressBar 표시
                EditText editText = findViewById(R.id.et_t2i_add_prompt);
                String url = "http://58.126.238.66:9900/generate_image_t2i_add_feedback";
                String add= editText.getText().toString();
                try{
                    jsonObject.put("prompt", prompt);
                    jsonObject.put("feedback",add);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    //응답받은 JSONObject 에서 데이터 꺼내오기
                    @Override
                    public void onResponse(JSONObject response) {
                        progressBar.setVisibility(View.GONE);
                        parseData(response,imageView);
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(t2iActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonObjectRequest);
            }

        });//onClickListener


    }//onCreate
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    private void parseData(JSONObject object,ImageView imageView) {

        String base64String=null;

        try {
            base64String = object.getString("image");
            prompt=object.getString("prompt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalBase64String = base64String;
        Bitmap decodedBytes = StringToBitmap(finalBase64String);
        imageView.setImageBitmap(decodedBytes);
    }
}//Volleytest