package com.example.half_asleep;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RestAPITaskLogin extends AsyncTask<Integer, Void, String> {
    protected String mURL;
    protected String id, pwd;

    public RestAPITaskLogin(String url, String id, String pwd) {
        this.mURL = url;
        this.id = id;
        this.pwd = pwd;
    }

    @Override
    protected String doInBackground(Integer... params) {
        try {
            URL url = new URL(mURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDefaultUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");


            StringBuffer buffer = new StringBuffer();
            buffer.append("id").append("=").append(id).append("&");
            buffer.append("pwd").append("=").append(pwd);

            OutputStreamWriter outputStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            PrintWriter writer = new PrintWriter(outputStream);
            writer.write(buffer.toString());
            System.out.println(buffer.toString());
            writer.flush();
            writer.close();

            InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            String result = builder.toString();
            JSONObject jsonObject = new JSONObject(result);
            JSONObject postObject = jsonObject.getJSONObject("code");
            String code = postObject.getString("code");

            return code;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }



}
