package com.example.half_asleep;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import android.util.Base64;

import android.widget.EditText;
import android.widget.Button;
import com.android.volley.toolbox.Volley;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;


public class FriendFragment extends Fragment {

    private RecyclerView recyclerView;
    private FriendAdapter adapter;
    private ArrayList<Friend> friendList = new ArrayList<>();

    private ReceivedRequest receivedRequest;
    private SendRequest sendRequest;

    private RecyclerView sendRequestRecyclerView;
    private SendRequestAdapter sendRequestAdapter;
    private ArrayList<SendRequest> sendRequestList = new ArrayList<SendRequest>();

    private RecyclerView receivedRequestRecyclerView;
    private ReceivedRequestAdapter receivedRequestAdapter;
    private ArrayList<ReceivedRequest> receivedRequestList = new ArrayList<ReceivedRequest>();

    private String myId; // 사용자 아이디
    private String myPin; // 사용자 PIN
    private Button addFriendButton;
    private Button btnAcceptRequest, btnRejectionRequest, btnCancelSend;
    private EditText idSearchEditText;

    public FriendFragment() {
        // 필수 빈 생성자
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        // 보낸 친구 신청 목록
        sendRequestRecyclerView = view.findViewById(R.id.send_friend);
        sendRequestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        sendRequestAdapter = new SendRequestAdapter(getActivity(), sendRequestList, myPin);
        sendRequestRecyclerView.setAdapter(sendRequestAdapter);

        // 받은 친구 신청 목록
        receivedRequestRecyclerView = view.findViewById(R.id.received_friend);
        receivedRequestRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        receivedRequestAdapter = new ReceivedRequestAdapter(getActivity(), receivedRequestList, myPin);
        receivedRequestRecyclerView.setAdapter(receivedRequestAdapter);

        recyclerView = view.findViewById(R.id.rv_diary);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new FriendAdapter(getActivity(), friendList, myPin);
        recyclerView.setAdapter(adapter);

        SharedPreferences pref = getActivity().getSharedPreferences("my_prefs", 0);
        myId = pref.getString("id", "");
        myPin = pref.getString("pin", "0");

        // URL 생성
        String url = "http://58.126.238.66:9900/get_friend_list/" + myId;
        String sendRequestUrl = "http://58.126.238.66:9900/get_friend_send_list/" + myId;
        String receivedRequestUrl = "http://58.126.238.66:9900/get_friend_req_list/" + myId;


        // 현재 친구 목록 불러오기
        loadFriendList(url);

        // 보낸 친구 신청 목록 불러오기
        loadSendRequestList(sendRequestUrl);

        // 받은 친구 신청 목록 불러오기
        loadReceivedRequestList(receivedRequestUrl);

        // 추가: 친구 추가 UI 요소 초기화
        addFriendButton = view.findViewById(R.id.btn_add_friend);
        idSearchEditText = view.findViewById(R.id.id_search);

        // 추가: 친구 추가 버튼 클릭 이벤트 처리
        addFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String friendId = idSearchEditText.getText().toString();
                addFriend(friendId);
            }
        });

        return view;
    }


    private void loadFriendList(String url) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            friendList.clear(); // 기존 데이터 초기화
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject row = response.getJSONObject(i);
                                String member_id = row.getString("member_id");
                                String member_name = row.getString("member_name");
                                String member_prf = row.getString("member_prf");

                                Bitmap profileBitmap = StringToBitmap(member_prf);

                                Friend friend = new Friend(member_id, member_name, profileBitmap);
                                friendList.add(friend);
                            }

                            // UI 업데이트를 메인 스레드에서 수행
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace(); // 에러 메시지 출력
                    }
                }
        );

        // 요청을 큐에 추가
        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }


    private void loadSendRequestList(String sendRequestUrl) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, sendRequestUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            sendRequestList.clear(); // 기존 데이터 초기화
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject row = response.getJSONObject(i);
                                String member_id = row.getString("member_id");
                                String member_name = row.getString("member_name");
                                String member_prf = row.getString("member_prf");

                                Bitmap profileBitmap = StringToBitmap(member_prf);

                                SendRequest sendRequest = new SendRequest(member_id, member_name, profileBitmap);
                                sendRequestList.add(sendRequest);
                            }

                            // UI 업데이트를 메인 스레드에서 수행
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    sendRequestAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace(); // 에러 메시지 출력
                    }
                }
        );

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void loadReceivedRequestList(String receivedRequestUrl) {
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, receivedRequestUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            receivedRequestList.clear(); // 기존 데이터 초기화
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject row = response.getJSONObject(i);
                                String member_id = row.getString("member_id");
                                String member_name = row.getString("member_name");
                                String member_prf = row.getString("member_prf");

                                Bitmap profileBitmap = StringToBitmap(member_prf);

                                ReceivedRequest receivedRequest = new ReceivedRequest(member_id, member_name, profileBitmap);
                                receivedRequestList.add(receivedRequest);
                            }

                            // UI 업데이트를 메인 스레드에서 수행
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    receivedRequestAdapter.notifyDataSetChanged();
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace(); // 에러 메시지 출력
                    }
                }
        );

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(request);
    }

    private void addFriend(String friendId) {
        String url = "http://58.126.238.66:9900/send_friend_req";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("userPIN", myPin);
            jsonObject.put("friendID", friendId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // 친구 추가 요청 후 서버 응답 처리
                        // 예: Toast 메시지로 결과 표시
                        Toast.makeText(getActivity(), "친구 추가 요청 완료", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace(); // 에러 메시지 출력
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(jsonObjectRequest);
    }

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}