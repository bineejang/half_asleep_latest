package com.example.half_asleep;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import android.widget.Toast;

import android.app.AlertDialog;
import android.content.DialogInterface;


public class ReceivedRequestAdapter extends RecyclerView.Adapter<ReceivedRequestAdapter.ReceivedRequestViewHolder> {

    private Context context;
    private ArrayList<ReceivedRequest> receivedRequestList;
    private String myPin;

    public ReceivedRequestAdapter(Context context, ArrayList<ReceivedRequest> receivedRequestList, String myPin) {
        this.context = context;
        this.receivedRequestList = receivedRequestList;
        this.myPin = myPin;
    }

    @NonNull
    @Override
    public ReceivedRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_received_request, parent, false);
        return new ReceivedRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivedRequestViewHolder holder, int position) {
        ReceivedRequest receivedRequest = receivedRequestList.get(position);

        holder.friendName.setText(receivedRequest.getMemberName());

        Glide.with(context)
                .load(receivedRequest.getMemberPrf())
                .into(holder.friendImage);
    }

    @Override
    public int getItemCount() {
        return receivedRequestList.size();
    }

    public class ReceivedRequestViewHolder extends RecyclerView.ViewHolder {
        public ImageView friendImage;
        public TextView friendName;
        public Button acceptRequestButton;
        public Button rejectRequestButton;

        public ReceivedRequestViewHolder(View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.friend_prf);
            friendName = itemView.findViewById(R.id.friend_name);
            acceptRequestButton = itemView.findViewById(R.id.btn_accept_request);
            rejectRequestButton = itemView.findViewById(R.id.btn_rejection_request);

            acceptRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getAdapterPosition();
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        ReceivedRequest acceptedRequest = receivedRequestList.get(itemPosition);
                        String friendId = acceptedRequest.getMemberId();
                        acceptFriendRequest(friendId, itemPosition);
                    }
                }
            });

            rejectRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int itemPosition = getAdapterPosition();
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        ReceivedRequest rejectedRequest = receivedRequestList.get(itemPosition);
                        String friendId = rejectedRequest.getMemberId();
                        rejectFriendRequest(friendId, itemPosition);
                    }
                }
            });
        }
    }

    private void acceptFriendRequest(String friendId, int itemPosition) {
        // Show an alert dialog to confirm friend request acceptance
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("친구신청을 수락하시겠습니까?") // 제목 설정
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String acceptUrl = "http://58.126.238.66:9900/accept_friend_req";

                        if (itemPosition != RecyclerView.NO_POSITION) {
                            receivedRequestList.remove(itemPosition);
                            notifyItemRemoved(itemPosition);
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private void rejectFriendRequest(String friendId, int itemPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("친구신청을 거절하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String rejectUrl = "http://58.126.238.66:9900/reject_friend_req";

                        if (itemPosition != RecyclerView.NO_POSITION) {
                            receivedRequestList.remove(itemPosition);
                            notifyItemRemoved(itemPosition);
                        }
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }
}