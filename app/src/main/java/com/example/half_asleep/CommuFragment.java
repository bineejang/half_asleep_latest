package com.example.half_asleep;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CommuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommuFragment extends Fragment {
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
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public CommuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CommuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CommuFragment newInstance(String param1, String param2) {
        CommuFragment fragment = new CommuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    ArrayList<CommuEntry> list = new ArrayList<CommuEntry>();

    String myId;
    String myPin;
    CommuEntry Entry;
    protected int offset;
    boolean position_flag=true;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_commu, container, false);
        SharedPreferences pref = getActivity().getSharedPreferences("my_prefs", 0);
        myId = pref.getString("id", "");
        myPin = pref.getString("pin", "0");

        CommuAdapter commuAdapter = new CommuAdapter(list);
        RecyclerView recyclerView = view.findViewById(R.id.rv_diary);

        ///get_posts_friends/<pin>
        commuAdapter.setOnItemClickListener(new CommuAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                String id = list.get(pos).getPost_id();
                Intent intent = new Intent(getContext(), View_post.class);
                intent.putExtra("postID",id);
                startActivity(intent);
            }
        });


        ScrollView scrollView = view.findViewById(R.id.scroll);
        request(recyclerView, commuAdapter, 0);


        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int offset=0;
                offset+=10;
                if (position_flag) {

                    if ((!v.canScrollVertically(1))) {
                        request(recyclerView, commuAdapter, offset);

                    } else if ((!v.canScrollVertically(-1))) {
                        offset+=10;
                    }
                    position_flag = false;
                } else position_flag = true;
            }


        });


        return view;
    }

    public void request(RecyclerView recyclerView, CommuAdapter commuAdapter, int offset){
        RequestQueue queue;
        queue = Volley.newRequestQueue(getContext());
        String url = "http://58.126.238.66:9900/get_posts_friends/";
        url=url+myPin+"?offset="+offset;
        JsonObjectRequest jsonObjectRequest_i = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            //응답받은 JSONObject 에서 데이터 꺼내오기
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray JAry = response.getJSONArray("posts");
                    for (int i = 0; i < JAry.length(); i++) {
                        JSONObject idobj = JAry.getJSONObject(i);
                        Entry = new CommuEntry(
                                idobj.getString("post_id"),
                                idobj.getString("username"),
                                formatDate(idobj.getString("date")),
                                idobj.getString("profileImage"),
                                idobj.getString("postImage"),
                                idobj.getString("content")
                        );
                        list.add(Entry);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                recyclerView.setAdapter(commuAdapter);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getContext(), "error: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonObjectRequest_i);
    }

    /**
     * @author with_soju
     * 스크롤 하단 터치 인식 클래스
     */

}