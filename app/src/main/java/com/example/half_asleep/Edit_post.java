package com.example.half_asleep;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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

public class Edit_post extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_commu);
        ImageView postimg = findViewById(R.id.iv_foodpic);
        EditText content = findViewById(R.id.et_content);
        String url = "http://58.126.238.66:9900/post_update";
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        content.setText(getIntent().getStringExtra("content"));
        String postID = getIntent().getStringExtra("postID");
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        postimg.setImageBitmap(bitmap);

        Button finish = findViewById(R.id.btn_finish_i2i);
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("postId",postID);
                    jsonObject.put("content",content.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    //응답받은 JSONObject 에서 데이터 꺼내오기
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Edit_post.this, "수정 완료" , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Edit_post.this,MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Edit_post.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(jsonObjectRequest);
            }
        });
    }
}