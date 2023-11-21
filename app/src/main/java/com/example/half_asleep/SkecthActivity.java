package com.example.half_asleep;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import android.widget.FrameLayout;
import android.graphics.Color;
import android.widget.SeekBar;

import android.os.Environment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import yuku.ambilwarna.AmbilWarnaDialog;

public class SkecthActivity extends AppCompatActivity {

    private MyPaintView view;
    int tColor, n = 0;
    private Bitmap canvasBitmap;
    private Button SaveBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i2i);
        view = new MyPaintView(this);
        EditText editText = findViewById(R.id.et_i2i_add_prompt);

        FrameLayout container = findViewById(R.id.container);
        Resources res = getResources();
        String diary = getIntent().getStringExtra("diary");

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        container.addView(view, params);

        Button btn = findViewById(R.id.colorPickerButton);
        Button btn2 = findViewById(R.id.thickPickerButton);
        Button btn3 = findViewById(R.id.eraserButton);
        Button btn4 = findViewById(R.id.btn_finish_sketch_i2i);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPicker();
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toggle the eraser mode
                n = 1 - n;
                if (n == 1) {
                    Toast.makeText(getApplicationContext(), "Eraser mode activated", Toast.LENGTH_LONG).show();
                    // If in eraser mode, set the color to white directly
                    view.setColor(Color.WHITE);
                } else {
                    Toast.makeText(getApplicationContext(), "Eraser mode deactivated", Toast.LENGTH_LONG).show();
                    // If not in eraser mode, open the color picker dialog
                    openColorPicker();
                }
            }
        });
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                // 그림을 저장할 경로 정의
                String filePath = getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/drawing.jpeg";


                // 그림 저장
                view.saveDrawing(filePath);

                // Start the next activity and pass the file path
                Intent intent = new Intent(SkecthActivity.this, reDrawActivity.class);
                intent.putExtra("imagePath", filePath);

                String scene = editText.getText().toString();
                intent.putExtra("scene", scene);
                intent.putExtra("diary", diary);
                startActivity(intent);
            }
        });
    }


    private void bitmap(String filePath) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            canvasBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void show() {
        final SeekBar seekBar = new SeekBar(this);
        seekBar.setMax(100); // 굵기의 최대값 설정, 예를 들면 100

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(seekBar);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("굵기 선택");
        builder.setView(layout);
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int strokeWidth = seekBar.getProgress();
                view.setStrokeWidth(strokeWidth);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // 사용자가 취소를 선택한 경우의 동작 추가
            }
        });
        builder.show();
    }

    private void openColorPicker() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, tColor, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                Toast.makeText(getApplicationContext(), "" + tColor, Toast.LENGTH_LONG).show();
                view.setColor(color);
            }
        });
        colorPicker.show();
    }



}