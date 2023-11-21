package com.example.half_asleep;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.List;

public class MyGridViewAdapter extends BaseAdapter {
    private Context context;
    private List<String> data;

    public MyGridViewAdapter(Context context, List<String> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.gird_item_layout, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.imageView);
        String imageUrl = data.get(position);

        Glide.with(context)
                .load(imageUrl)
                .into(imageView);

        return view;
    }
}
