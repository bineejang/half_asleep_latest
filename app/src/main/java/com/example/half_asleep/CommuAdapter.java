package com.example.half_asleep;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.text.TextPaint;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CommuAdapter extends RecyclerView.Adapter<CommuAdapter.Holder> {
    public interface OnItemClickListener {
        void onItemClick(View v, int pos);
    }
    private OnItemClickListener mListener = null;

    public void setOnItemClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }
    ArrayList<CommuEntry> list;

    CommuAdapter(ArrayList<CommuEntry> list) {
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

        // 텍스트 길이를 기반으로 et_content의 높이를 동적으로 조정
        ViewGroup.LayoutParams layoutParams = holder.content.getLayoutParams();
        layoutParams.height = calculateEditTextHeight(holder, list.get(position).getcontent());
        holder.content.setLayoutParams(layoutParams);
    }

    // 텍스트 길이를 기반으로 et_content의 높이를 계산
    private int calculateEditTextHeight(Holder holder, String text) {
        // 원하는 최대 높이 설정
        int maxHeight = 100;

        // 텍스트 높이를 기반으로 계산
        TextPaint textPaint = holder.content.getPaint();
        int textWidth = holder.content.getWidth() - holder.content.getPaddingLeft() - holder.content.getPaddingRight();
        int lineCount = (int) Math.ceil((float) text.length() * textPaint.measureText("A") / textWidth);
        int lineHeight = Math.round(textPaint.getFontSpacing());
        int desiredHeight = Math.min(lineCount * lineHeight, maxHeight);

        return desiredHeight;
    }

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

    @Override
    public int getItemCount() {
        return list.size();
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
                    if (pos !=RecyclerView.NO_POSITION){
                        if (mListener != null){
                            mListener.onItemClick(v,pos);
                        }
                    }
                }
            });

        }
    }
}

