package com.example.half_asleep;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
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

public class reDrawActivity extends AppCompatActivity {

    private Bitmap prof; // 클래스 멤버 변수로 선언
    private EditText editText;
    private ProgressBar progressBar;
    private Bitmap sketch;
    byte[] baos;
    String prompt;
    public void getSketch(ImageView iv){
        BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();
        sketch = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        sketch.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        baos = byteArray;
    }

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_i2i_redraw);

        String url = "http://58.126.238.66:9900/generate_image_i2i_scene";
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        ImageView imageView = findViewById(R.id.iv_i2i);
        progressBar = findViewById(R.id.progressBar);

        // 인텐트에서 이미지 파일 경로 가져오기
        String imagePath = getIntent().getStringExtra("imagePath");
        String diary = getIntent().getStringExtra("diary");
        String scene = getIntent().getStringExtra("scene");
        // 이미지 로드 및 표시
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        // 그림을 저장할 경로 정의
        String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/drawing.jpeg";
        ImageView generate = findViewById(R.id.iv_redraw_i2i);
        Button complete = findViewById(R.id.btn_finish_i2i);
        Button before = findViewById(R.id.btn_before_i2i);
        imageView.setImageBitmap(bitmap);
        getSketch(imageView);


        JSONObject jsonObject = new JSONObject();
        String j_prof = BitmapToString(bitmap);
        String j_prompt = prompt;
        try {
            jsonObject.put("sketch", j_prof);
            jsonObject.put("scene", scene);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.VISIBLE);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
            //응답받은 JSONObject 에서 데이터 꺼내오기
            @Override
            public void onResponse(JSONObject response) {
                //parseData라는 함수의 변수 response는 서버로붙어 받은 응답,imageView는 이미지를 띄울 xml파일의 imageView,reprompt는 서버로 전송한 프롬프트가 맞게 들어갔는지 서버에서 사용한 걸 그대로보내주는 프롬프트
                progressBar.setVisibility(View.GONE);
                try {
                    prompt = response.getString("prompt");
                } catch (JSONException e){
                    e.printStackTrace();
                }

                parseData(response,imageView);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(reDrawActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        //json timeout 정책 이걸로 사진뽑는 시간을 기다릴수 있음 원래는 2.5초라서 응답이 안오면 신호를 다시 보내는데 그러면 서버가 멈춤
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjectRequest);


        before.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(reDrawActivity.this,SkecthActivity.class);
                intent.putExtra("diary",diary);
                startActivity(intent);
            }
        });

        //작성 완료버튼
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(reDrawActivity.this, CompleteDiaryActivity.class);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                intent.putExtra("sketch",baos);
                intent.putExtra("image", byteArray);
                intent.putExtra("diary", diary);
                intent.putExtra("prompt", prompt);
                startActivity(intent);
                finish();
            }
        });
        //
        generate.setOnClickListener(new View.OnClickListener() {
            public void onClick (View v){
                String url = "http://58.126.238.66:9900/generate_image_i2i_remake";
                progressBar.setVisibility(View.VISIBLE); // 이미지 로딩 전에 ProgressBar 표시
                JSONObject jsonObject = new JSONObject();
                String j_prof = BitmapToString(bitmap);
                String j_prompt = prompt;
                try {
                    jsonObject.put("sketch", j_prof);
                    jsonObject.put("prompt", j_prompt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    //응답받은 JSONObject 에서 데이터 꺼내오기
                    @Override
                    public void onResponse(JSONObject response) {
                        //parseData라는 함수의 변수 response는 서버로붙어 받은 응답,imageView는 이미지를 띄울 xml파일의 imageView,reprompt는 서버로 전송한 프롬프트가 맞게 들어갔는지 서버에서 사용한 걸 그대로보내주는 프롬프트
                        progressBar.setVisibility(View.GONE);
                        parseData(response,imageView);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(reDrawActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });

                //json timeout 정책 이걸로 사진뽑는 시간을 기다릴수 있음 원래는 2.5초라서 응답이 안오면 신호를 다시 보내는데 그러면 서버가 멈춤
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        60000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonObjectRequest);
            }
        });//setOnClickListener
    }

    public static Bitmap StringToBitmap (String encodedString){
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    //서버에서 받은 json 에서 이미지 추출하는 함수
    private void parseData(JSONObject object, ImageView imageView) {

        String base64String=null;
        String pt = null;
        try {
            base64String = object.getString("image");
            pt=object.getString("prompt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Toast 함수는 알림만 띄우는 기능 삭제 해도 무관

        //베이스 64 인코딩을 디코딩하여 이미지로 나타내는 기능
        String finalBase64String = base64String;
        Bitmap decodedBytes = StringToBitmap(finalBase64String);
        imageView.setImageBitmap(decodedBytes);
    }

    //그림을 문자열로 바꿔주는 함수
    public static String BitmapToString(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return temp;
    }
}


//////////////////////////////////////////////////////////////////////////
        /*@Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_join);
            ImageView imageView = findViewById(R.id.iv_i2i);
            //클릭시 비트맵을 받아서
            Bitmap prof = BitmapFactory.decodeResource(getResources(),);
            EditText editText = findViewById(R.id.et_i2i_add_prompt);
            String url = "http://58.126.238.66:9900/generate_image_i2i";

            TextView reprompt;
            RequestQueue queue;
            queue = Volley.newRequestQueue(this);

            public void onClick(View v) {
                String j_prof = BitmapToString(prof);
                String prompt= editText.getText().toString();
                try{
                    jsonObject.put("profileImg",j_prof);
                    jsonObject.put("prompt", prompt);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                    //응답받은 JSONObject 에서 데이터 꺼내오기
                    @Override
                    public void onResponse(JSONObject response) {
                //parseData라는 함수의 변수 response는 서버로붙어 받은 응답,imageView는 이미지를 띄울 xml파일의 imageView,reprompt는 서버로 전송한 프롬프트가 맞게 들어갔는지 서버에서 사용한 걸 그대로보내주는 프롬프트
                        parseData(response,imageView,reprompt);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(reDrawActivity.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                //json timeout 정책 이걸로 사진뽑는 시간을 기다릴수 있음 원래는 2.5초라서 응답이 안오면 신호를 다시 보내는데 그러면 서버가 멈춤
                jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                        20000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                queue.add(jsonObjectRequest);
            }
        }
    //문자열을 그림으로 바꿔주는 함수
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    //서버에서 받은 json 에서 이미지 추출하는 함수
    private void parseData(JSONObject object,ImageView imageView,TextView editText) {

        String base64String=null;
        String pt = null;
        try {
            base64String = object.getString("image");
            pt=object.getString("prompt");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Toast 함수는 알림만 띄우는 기능 삭제 해도 무관
        Toast.makeText(reDrawActivity.this, "prompt: " + editText.toString(), Toast.LENGTH_SHORT).show();
        //베이스 64 인코딩을 디코딩하여 이미지로 나타내는 기능
        String finalBase64String = base64String;
        Bitmap decodedBytes = StringToBitmap(finalBase64String);
        imageView.setImageBitmap(decodedBytes);
    }
    //그림을 문자열로 바꿔주는 함수
    public static String BitmapToString(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return temp;
    }
}
*/
///////////////////////////////////////////////////////////////////////