package com.example.half_asleep;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.StringRequest;
import java.io.UnsupportedEncodingException;
import com.android.volley.VolleyLog;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Collections;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;

public class MainActivity extends AppCompatActivity {
    final String TAG = this.getClass().getSimpleName();

    private RequestQueue requestQueue;


    LinearLayout center_ly;
    BottomNavigationView bottomNavigationView;
    String pin;
    private boolean isFetchUserDiaryDataCalled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestQueue = Volley.newRequestQueue(this);

        // PIN을 SharedPreferences에서 가져온다
        SharedPreferences pref = getSharedPreferences("my_prefs", 0);
        pin = pref.getString("pin", "");

        init(); // 객체 정의
        SettingListener(); // 리스너 등록

        // 탭을 처음에 선택합니다.
        bottomNavigationView.setSelectedItemId(R.id.tab_home);

        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());

        // fetchUserDiaryData가 호출된 적이 없을 때만 호출하도록 합니다.
        if (!isFetchUserDiaryDataCalled) {
            fetchUserDiaryData();
            isFetchUserDiaryDataCalled = true;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchUserDiaryData();
    }

    private void fetchUserDiaryData() {
        String url = "http://58.126.238.66:9900/get_user_diarys";

        // JSON 데이터를 준비
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("pin", pin);

            // 기타 필요한 데이터를 추가할 수 있습니다.
            // jsonBody.put("key", value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // JSON 데이터를 요청에 추가
        String requestBody = jsonBody.toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            // JSON 응답을 처리하고 데이터를 파싱
                            ArrayList<DiaryEntry> diaryEntries = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String imageUrl = "data:image/jpeg;base64," + jsonObject.getString("image");
                                String diaryId = jsonObject.getString("diary_id");
                                String diaryDate = jsonObject.getString("diary_date");
                                DiaryEntry entry = new DiaryEntry(diaryId, diaryDate, imageUrl);
                                diaryEntries.add(entry);
                            }
                            Collections.reverse(diaryEntries); // 받아온 데이터를 역순으로 재배치

                            // 수정된 코드
                            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.change_LinearLayout);
                            if (currentFragment instanceof HomeFragment) {
                                HomeFragment homeFragment = (HomeFragment) currentFragment;
                                homeFragment.updateGridView(diaryEntries);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error
                    }
                }) {

            // 아래의 getHeaders 메서드 오버라이드를 추가하여 HTTP 헤더를 설정
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=UTF-8"); // Content-Type 수정
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8"); // 인코딩을 UTF-8로 명시
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void init() {
        center_ly = findViewById(R.id.change_LinearLayout); // 프래그먼트를 포함한 레이아웃
        bottomNavigationView = findViewById(R.id.menu_bottom_navigation);
    }

    private void SettingListener() {
        // 선택 리스너를 등록합니다.
        bottomNavigationView.setOnNavigationItemSelectedListener(new TabSelectedListener());
    }

    class TabSelectedListener implements BottomNavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            TextView selected = findViewById(R.id.select_frag); // 제목 표시줄 아래 선택 창

            switch (menuItem.getItemId()) {
                case R.id.tab_home: {
                    selected.setText("G a l l e r y");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.change_LinearLayout, new HomeFragment())
                            .commit();
                    fetchUserDiaryData(); // 홈 버튼을 누를 때 데이터를 다시 받아옴
                    return true;
                }
                case R.id.tab_commu: {
                    selected.setText("C o m m u n i t y");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.change_LinearLayout, new CommuFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_friend: {
                    selected.setText("F r i e n d s");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.change_LinearLayout, new FriendFragment())
                            .commit();
                    return true;
                }
                case R.id.tab_settings: {
                    selected.setText("s e t t i n g s");
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.change_LinearLayout, new SettingsFragment())
                            .commit();
                    return true;
                }
            }
            return false;
        }
    }
}
