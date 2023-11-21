package com.example.half_asleep;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;


public class SendRequestAdapter extends RecyclerView.Adapter<SendRequestAdapter.SendRequestViewHolder> {

    private Context context;
    private ArrayList<SendRequest> sendRequestList;
    private String myPin;

    public SendRequestAdapter(Context context, ArrayList<SendRequest> sendRequestList, String myPin) {
        this.context = context;
        this.sendRequestList = sendRequestList;
        this.myPin = myPin;
    }

    @NonNull
    @Override
    public SendRequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_send_request, parent, false);
        return new SendRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SendRequestViewHolder holder, int position) {
        SendRequest sendRequest = sendRequestList.get(position);

        holder.friendName.setText(sendRequest.getMemberName());

        Glide.with(context)
                .load(sendRequest.getMemberPrf())
                .into(holder.friendImage);
    }

    @Override
    public int getItemCount() {
        return sendRequestList.size();
    }

    public class SendRequestViewHolder extends RecyclerView.ViewHolder {
        public ImageView friendImage;
        public TextView friendName;
        public Button cancelSendRequestButton;

        public SendRequestViewHolder(View itemView) {
            super(itemView);
            friendImage = itemView.findViewById(R.id.friend_prf);
            friendName = itemView.findViewById(R.id.friend_name);
            cancelSendRequestButton = itemView.findViewById(R.id.btn_cancel_send);

            cancelSendRequestButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 여기에서 친구 신청 취소 메서드 호출 전에 확인 다이얼로그를 띄우기
                    int itemPosition = getAdapterPosition();
                    if (itemPosition != RecyclerView.NO_POSITION) {
                        SendRequest canceledRequest = sendRequestList.get(itemPosition);
                        String friendId = canceledRequest.getMemberId();
                        showCancelConfirmationDialog(friendId, itemPosition);
                    }
                }
            });
        }
    }

    // 친구 신청 취소 메서드
    private void cancelFriendRequest(String friendId, int itemPosition) {
        if (itemPosition != RecyclerView.NO_POSITION) {
            sendRequestList.remove(itemPosition);
            notifyItemRemoved(itemPosition);
        }
    }

    private void showCancelConfirmationDialog(String friendId, int itemPosition) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("친구신청을 취소하시겠습니까?")
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Call the method to cancel friend request
                        cancelFriendRequest(friendId, itemPosition);
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
}