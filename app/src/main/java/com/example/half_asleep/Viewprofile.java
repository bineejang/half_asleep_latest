package com.example.half_asleep;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class Viewprofile extends AppCompatActivity{

    private static final int REQUEST_CODE=0;
    public static String BitmapToString(Bitmap bitmap){

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] bytes = baos.toByteArray();
        String temp = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return temp;
    }
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
    private void parseData(JSONObject object,ImageView imageView) {

        String base64String=null;
        String pt = null;
        try {
            base64String = object.getString("image");
            pt=object.getString("prompt");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String finalBase64String = base64String;
        Bitmap decodedBytes = StringToBitmap(finalBase64String);
        imageView.setImageBitmap(decodedBytes);
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
    String ops;
    String myPin;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profileview);
        String myId;
        String url_get = "http://58.126.238.66:9900/get_user_info/";
        RequestQueue queue;
        queue = Volley.newRequestQueue(this);
        TextView id = findViewById(R.id.edit_id);
        EditText name = findViewById(R.id.edit_name);
        EditText email = findViewById(R.id.edit_email);
        EditText pwd = findViewById(R.id.edit_pwd);
        ImageView img = findViewById(R.id.prof_img);
        Button prfset=findViewById(R.id.prfset_btn);

        SharedPreferences.Editor editor;
        SharedPreferences pref = getSharedPreferences("my_prefs", 0);
        myId = pref.getString("id", "");
        myPin = pref.getString("pin", "0");
        Toast.makeText(Viewprofile.this, "PIN: " + myPin.toString(), Toast.LENGTH_SHORT).show();
        url_get=url_get+myId;
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");

                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 0);
            }
        });

        prfset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String url = "http://58.126.238.66:9900/update_user_info/";
                url=url+myId;
                BitmapDrawable drawable = (BitmapDrawable) img.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                ops = BitmapToString(bitmap);
                JSONObject jsonObject=new JSONObject();
                try{
                    jsonObject.put("member_name",name.getText().toString());
                    jsonObject.put("member_email",email.getText().toString());
                    jsonObject.put("member_pw",pwd.getText().toString());
                    jsonObject.put("member_prf",ops);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonObject, new Response.Listener<JSONObject>() {
                    //응답받은 JSONObject 에서 데이터 꺼내오기
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(Viewprofile.this, "수정 완료" , Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Viewprofile.this, MainActivity.class);
                        startActivity(intent);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(Viewprofile.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                queue.add(jsonObjectRequest);
            }
        });
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url_get, null, new Response.Listener<JSONObject>() {
            //응답받은 JSONObject 에서 데이터 꺼내오기
            @Override
            public void onResponse(JSONObject response) {

                try {
                    parseData(response,img);
                    id.setText(response.getString("id"));
                    name.setText(response.getString("name"));
                    email.setText(response.getString("email"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Viewprofile.this, "error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest);


    }
}
