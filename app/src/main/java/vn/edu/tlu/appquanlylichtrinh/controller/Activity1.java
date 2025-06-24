package vn.edu.tlu.appquanlylichtrinh.controller; // <-- Thay bằng package của bạn

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import vn.edu.tlu.appquanlylichtrinh.R;

public class Activity1 extends AppCompatActivity {

    // --- Model ---
    // Hiện tại chưa có dữ liệu cần xử lý ở màn hình này.

    // --- View ---
    private MaterialButton btnContinue; // Biến đại diện cho nút "Tiếp tục" trên giao diện

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // --- Controller: Kết nối View ---
        // Dòng này liên kết file Java này với file layout activity1.xml
        setContentView(R.layout.activity1);

        // --- Controller: Ánh xạ các thành phần View ---
        // Tìm nút "Tiếp tục" trong layout bằng ID của nó
        btnContinue = findViewById(R.id.btnContinue);

        // --- Controller: Lắng nghe sự kiện người dùng ---
        // Đặt một sự kiện lắng nghe khi người dùng nhấp vào nút
        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // --- Controller: Xử lý hành động ---
                // Khi nút được nhấn, gọi hàm để chuyển sang màn hình tiếp theo
                goToNextActivity();
            }
        });
    }

    private void goToNextActivity() {
        // Tạo một "ý định" (Intent) để mở Activity2
        Intent intent = new Intent(Activity1.this, Activity2.class);
        // Bắt đầu Activity mới
        startActivity(intent);
    }
}