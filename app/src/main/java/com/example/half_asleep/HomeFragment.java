package com.example.half_asleep;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.AdapterView;
import java.util.ArrayList;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.List;

public class HomeFragment extends Fragment {
    private View view;
    private GridView gridView;
    private MyGridViewAdapter adapter;
    private List<String> data;
    private List<DiaryEntry> diaryEntries;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        data = new ArrayList<String>();
        data.add("Item 1");
        data.add("Item 2");
        data.add("Item 3");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        gridView = view.findViewById(R.id.gridViewImages);

        adapter = new MyGridViewAdapter(getActivity(), data);
        gridView.setAdapter(adapter);

        FloatingActionButton floatingActionButton = view.findViewById(R.id.btn_create);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WriteDiaryActivity.class);
                startActivity(intent);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < diaryEntries.size()) {
                    String selectedDiaryId = diaryEntries.get(position).getDiaryId();
                    openSelectDiary(selectedDiaryId);
                } else {
                    // 유효한 항목이 없을 때 처리할 코드 추가
                }
            }
        });

        return view;
    }

    // openSelectDiary 메소드 정의
    private void openSelectDiary(String diaryId) {
        Intent intent = new Intent(getActivity(), SelectDiaryActivity.class);
        intent.putExtra("diary_id", diaryId);
        startActivity(intent);
    }

    public void updateGridView(List<DiaryEntry> diaryEntries) {
        this.diaryEntries = diaryEntries;
        data.clear();
        for (DiaryEntry entry : diaryEntries) {
            String imageUrl = entry.getImageUrl();
            data.add(imageUrl);
        }
        adapter.notifyDataSetChanged();
    }
}