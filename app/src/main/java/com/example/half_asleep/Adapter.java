package com.example.half_asleep;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }
    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    ArrayList<CommuEntry> list;

    Adapter(ArrayList<CommuEntry> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_commu, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        holder.content.setText(list.get(position).getcontent());
        holder.name.setText(list.get(position).getusername());
        holder.Date.setText(list.get(position).getpostDate());
        holder.prf.setImageBitmap(StringToBitmap(list.get(position).getprofileImage()));
        holder.thumb.setImageBitmap(StringToBitmap(list.get(position).getpostImage()));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static Bitmap StringToBitmap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.NO_WRAP);
            if (encodeByte != null && encodeByte.length > 0) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                if (bitmap != null) {
                    return bitmap;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    class Holder extends RecyclerView.ViewHolder {
        TextView content, name, Date;
        ImageView prf, thumb;

        public Holder(@NonNull View itemView) {
            super(itemView);
            content = itemView.findViewById(R.id.tv_content);
            name = itemView.findViewById(R.id.name);
            prf = itemView.findViewById(R.id.prf);
            thumb = itemView.findViewById(R.id.iv_pic);
            Date = itemView.findViewById(R.id.Date);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION){
                        if (mListener != null){
                            mListener.onItemClick(v, pos);
                        }
                    }
                }
            });
        }
    }
}