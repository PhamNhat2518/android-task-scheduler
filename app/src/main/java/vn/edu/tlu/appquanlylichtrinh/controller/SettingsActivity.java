package vn.edu.tlu.appquanlylichtrinh.controller; // <-- Thay bằng package của bạn

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton; // QUAN TRỌNG: Thêm import này

import vn.edu.tlu.appquanlylichtrinh.R;

public class SettingsActivity extends AppCompatActivity {
    private TextView tvDone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // --- BẮT ĐẦU PHẦN CODE CẦN THÊM ---

        // 1. Ánh xạ (tìm) nút Đăng nhập từ file XML qua ID của nó
        MaterialButton btnLogin = findViewById(R.id.btnLogin);
        tvDone = findViewById(R.id.tvDone);
        // 2. Đặt sự kiện lắng nghe khi người dùng nhấp vào nút Đăng nhập
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nút được nhấn, gọi hàm để chuyển sang màn hình Đăng nhập
                goToLoginActivity();
            }
        });
        tvDone.setOnClickListener(v -> {
            // Đơn giản là đóng màn hình hiện tại lại
            finish();
        });

        // --- KẾT THÚC PHẦN CODE CẦN THÊM ---
    }

    // Hàm riêng để xử lý việc chuyển màn hình, giúp code gọn gàng hơn
    private void goToLoginActivity() {
        // Tạo một "ý định" (Intent) để mở LoginActivity
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        // Bắt đầu Activity mới
        startActivity(intent);
    }
}