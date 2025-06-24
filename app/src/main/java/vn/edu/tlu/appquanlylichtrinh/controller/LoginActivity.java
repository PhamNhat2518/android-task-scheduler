package vn.edu.tlu.appquanlylichtrinh.controller; // <-- Thay bằng package của bạn

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton; // QUAN TRỌNG: Thêm import này

import vn.edu.tlu.appquanlylichtrinh.R;

public class LoginActivity extends AppCompatActivity {

    // Khai báo các View mà chúng ta cần tương tác
    private MaterialButton btnRegister;
    // Khai báo các view khác nếu bạn cần xử lý (btnLogin, etPhoneNumber,...)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // --- BẮT ĐẦU PHẦN CODE CẦN THÊM ---

        // 1. Ánh xạ (tìm) nút Đăng ký từ file XML qua ID của nó
        btnRegister = findViewById(R.id.btnRegister);

        // 2. Đặt sự kiện lắng nghe khi người dùng nhấp vào nút Đăng ký
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nút được nhấn, gọi hàm để chuyển sang màn hình Đăng ký
                goToRegisterActivity();
            }
        });

        // Bạn có thể thêm các sự kiện click khác ở đây
        // ví dụ cho btnLogin, tvCancel, tvForgotPassword...

        // --- KẾT THÚC PHẦN CODE CẦN THÊM ---
    }

    // Hàm riêng để xử lý việc chuyển màn hình, giúp code gọn gàng hơn
    private void goToRegisterActivity() {
        // Tạo một "ý định" (Intent) để mở RegisterActivity
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        // Bắt đầu Activity mới
        startActivity(intent);
    }
}
