package com.example.half_asleep;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.ArrayList;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.Volley;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context context;
    private ArrayList<Friend> friendList;
    private String myPin; // 사용자 PIN

    public Button addFriendButton;

    public FriendAdapter(Context context, ArrayList<Friend> friendList, String myPin) {
        this.context = context;
        this.friendList = friendList;
        this.myPin = myPin;
    }

    @NonNull
    @Override
    public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_friend, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FriendViewHolder holder, int position) {
        Friend friend = friendList.get(position);

        holder.friendName.setText(friend.getMemberName());

        // Glide를 사용하여 이미지 로드
        Glide.with(context)
                .load(friend.getMemberPrf())
                .into(holder.friendImage);

        // 삭제 버튼 클릭 이벤트 처리
        holder.deleteFriendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 여기에서 삭제 확인 다이얼로그를 띄우기
                int adapterPosition = holder.getAdapterPosition();
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    Friend clickedFriend = friendList.get(adapterPosition);
                    String friendId = clickedFriend.getMemberId(); // 선택한 항목의 ID를 가져옴
                    showDeleteConfirmationDialog(friendId, adapterPosition);
                }
            }
        });
    }

    private void showDeleteConfirmationDialog(String friendId, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("친구 목록에서 삭제하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteFriend(friendId, position);
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

    @Override
    public int getItemCount() {
        return friendList.size();
    }

    public class FriendViewHolder extends RecyclerView.ViewHolder {
        public ImageView friendImage;
        public TextView friendName;
        public Button deleteFriendButton;

        public FriendViewHolder(View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.friend_prf);
            friendName = itemView.findViewById(R.id.friend_name);
            deleteFriendButton = itemView.findViewById(R.id.btn_delete_friend);
        }
    }

    // 추가: 친구 삭제 메서드
    private void deleteFriend(String friendId, final int position) {
        String url = "http://58.126.238.66:9900/delete_friend";

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
                        // 친구 삭제 요청 후 서버 응답 처리
                        // 예: Toast 메시지로 결과 표시
                        Toast.makeText(context, "친구 삭제 요청 완료", Toast.LENGTH_SHORT).show();

                        // 삭제한 친구를 목록에서 제거
                        if (position != RecyclerView.NO_POSITION) {
                            friendList.remove(position);
                            notifyItemRemoved(position);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace(); // 에러 메시지 출력
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(jsonObjectRequest);
    }
}
