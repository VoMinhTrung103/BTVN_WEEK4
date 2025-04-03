package com.example.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.telephony.SmsManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class FirstActivity extends AppCompatActivity {

    private EditText etHoTen, etMSSV, etLop, etKeHoach, etPhoneNumber, etSMSContent;
    private RadioGroup rgNamHoc;
    private CheckBox cbMTHN, cbDT, cbVT;
    private Button btnGuiThongTin, btnCall, btnSendSMS, btnTakePhoto;
    private ImageView ivCapturedPhoto;

    // Request code for camera capture (no longer needed with ActivityResultLauncher)
    private static final int REQUEST_CODE_PERMISSIONS = 100;

    // ActivityResultLauncher for camera capture
    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        // Ánh xạ các thành phần giao diện
        etHoTen = findViewById(R.id.etHoTen);
        etMSSV = findViewById(R.id.etMSSV);
        etLop = findViewById(R.id.etLop);
        rgNamHoc = findViewById(R.id.rgNamHoc);
        cbMTHN = findViewById(R.id.cbMTHN);
        cbDT = findViewById(R.id.cbDT);
        cbVT = findViewById(R.id.cbVT);
        etKeHoach = findViewById(R.id.etKeHoach);
        btnGuiThongTin = findViewById(R.id.btnGuiThongTin);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etSMSContent = findViewById(R.id.etSMSContent);
        btnCall = findViewById(R.id.btnCall);
        btnSendSMS = findViewById(R.id.btnSendSMS);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        ivCapturedPhoto = findViewById(R.id.ivCapturedPhoto);

        // Initialize camera launcher
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Bundle extras = result.getData().getExtras();
                        Bitmap imageBitmap = (Bitmap) extras.get("data");
                        ivCapturedPhoto.setImageBitmap(imageBitmap);
                        ivCapturedPhoto.setVisibility(ImageView.VISIBLE);
                    } else {
                        Toast.makeText(this, "Không thể chụp ảnh", Toast.LENGTH_SHORT).show();
                    }
                });

        // Xử lý sự kiện khi nhấn nút "Gửi thông tin"
        btnGuiThongTin.setOnClickListener(v -> {
            // Lấy dữ liệu từ các trường nhập liệu
            String hoTen = etHoTen.getText().toString();
            String mssv = etMSSV.getText().toString();
            String lop = etLop.getText().toString();
            String keHoach = etKeHoach.getText().toString();

            // Lấy năm học từ RadioGroup
            String namHoc = "";
            int selectedId = rgNamHoc.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = findViewById(selectedId);
                namHoc = selectedRadioButton.getText().toString();
            }

            // Lấy chuyên ngành từ CheckBox
            StringBuilder chuyenNganh = new StringBuilder();
            if (cbMTHN.isChecked()) chuyenNganh.append("MT HTN, ");
            if (cbDT.isChecked()) chuyenNganh.append("ĐT, ");
            if (cbVT.isChecked()) chuyenNganh.append("VT, ");
            if (chuyenNganh.length() > 0) {
                chuyenNganh.setLength(chuyenNganh.length() - 2); // Xóa dấu ", " cuối cùng
            }

            // Tạo Intent để chuyển dữ liệu sang SecondActivity
            Intent intent = new Intent(FirstActivity.this, SecondActivity.class);
            intent.putExtra("hoTen", hoTen);
            intent.putExtra("mssv", mssv);
            intent.putExtra("lop", lop);
            intent.putExtra("namHoc", namHoc);
            intent.putExtra("chuyenNganh", chuyenNganh.toString());
            intent.putExtra("keHoach", keHoach);
            startActivity(intent);
        });

        // Xử lý sự kiện khi nhấn nút "Gọi điện"
        btnCall.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if (phoneNumber.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phoneNumber));
                startActivity(callIntent);
            } else {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CODE_PERMISSIONS);
            }
        });

        // Xử lý sự kiện khi nhấn nút "Gửi SMS"
        btnSendSMS.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            String smsContent = etSMSContent.getText().toString().trim();
            if (phoneNumber.isEmpty() || smsContent.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại và nội dung SMS", Toast.LENGTH_SHORT).show();
                return;
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_GRANTED) {
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNumber, null, smsContent, null, null);
                    Toast.makeText(this, "SMS đã được gửi", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Gửi SMS thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, REQUEST_CODE_PERMISSIONS);
            }
        });

        // Xử lý sự kiện khi nhấn nút "Chụp ảnh"
        btnTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    cameraLauncher.launch(cameraIntent);
                } else {
                    Toast.makeText(this, "Không tìm thấy ứng dụng máy ảnh", Toast.LENGTH_SHORT).show();
                }
            } else {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSIONS);
            }
        });
    }

    // Xử lý kết quả yêu cầu quyền
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền được cấp, có thể gọi lại hành động tương ứng
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE)
                        == PackageManager.PERMISSION_GRANTED) {
                    btnCall.performClick();
                } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                        == PackageManager.PERMISSION_GRANTED) {
                    btnSendSMS.performClick();
                } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    btnTakePhoto.performClick();
                }
            } else {
                Toast.makeText(this, "Quyền bị từ chối", Toast.LENGTH_SHORT).show();
            }
        }
    }
}