package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SecondActivity extends AppCompatActivity {

    private TextView tvThongTin;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Ánh xạ các thành phần giao diện
        tvThongTin = findViewById(R.id.tvThongTin);
        btnBack = findViewById(R.id.btnBack);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        String hoTen = intent.getStringExtra("hoTen");
        String mssv = intent.getStringExtra("mssv");
        String lop = intent.getStringExtra("lop");
        String namHoc = intent.getStringExtra("namHoc");
        String chuyenNganh = intent.getStringExtra("chuyenNganh");
        String keHoach = intent.getStringExtra("keHoach");

        // Hiển thị thông tin
        String thongTin = "Họ và tên: " + hoTen + "\n" +
                "MSSV: " + mssv + "\n" +
                "Lớp: " + lop + "\n" +
                "Sinh viên năm: " + namHoc + "\n" +
                "Chuyên ngành: " + chuyenNganh + "\n" +
                "Kế hoạch phát triển bản thân: " + keHoach;
        tvThongTin.setText(thongTin);

        // Lưu thông tin vào file
        saveToFile(thongTin);

        // Xử lý sự kiện khi nhấn nút "Back"
        btnBack.setOnClickListener(v -> {
            finish(); // Quay lại FirstActivity
        });
    }

    private void saveToFile(String data) {
        try {
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "thong_tin_sinh_vien.txt");
            FileWriter writer = new FileWriter(file);
            writer.write(data);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}