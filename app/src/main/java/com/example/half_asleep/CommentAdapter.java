package com.example.half_asleep;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.renderscript.ScriptGroup;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.Holder> {

    ArrayList<CommentEntry> list;

    CommentAdapter(ArrayList<CommentEntry> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_comment, parent, false));


    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.content.setText(list.get(position).getcontent());
        holder.name.setText(list.get(position).getusername());
        holder.Date.setText(list.get(position).getpostDate());
        holder.prf.setImageBitmap(StringToBitmap(list.get(position).getprofileImage()));

    }


    @Override
    public int getItemCount() {
        if(list !=null){
            return list.size();}
        return 0;
    }
    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
    class Holder extends RecyclerView.ViewHolder {
        TextView content, name, Date;
        ImageView prf;
        ImageView del;


        public Holder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.comment);
            name = itemView.findViewById(R.id.nickname);
            prf = itemView.findViewById(R.id.iv_pic);
            Date = itemView.findViewById(R.id.Date);
            del = itemView.findViewById(R.id.commentDelBtn);

        }
    }
}

