package vn.edu.tlu.appquanlylichtrinh.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.edu.tlu.appquanlylichtrinh.R;

public class LoginActivity extends AppCompatActivity {

    // --- Khai báo View và Firebase ---
    private EditText etEmail, etPassword;
    private MaterialButton btnLogin, btnRegister;
    // Khai báo các TextView khác nếu bạn cần xử lý (ví dụ: tvForgotPassword)
    private TextView tvForgotPassword, tvCancel;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ các View từ layout
        initViews();

        // Thiết lập các sự kiện click
        setupClickListeners();
    }

    // Phương thức kiểm tra người dùng đã đăng nhập từ trước chưa
    /**
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Nếu đã đăng nhập, chuyển thẳng vào màn hình chính
            Toast.makeText(this, "Đã đăng nhập với: " + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            goToMainActivity();
        }
    }
*/
    /**
     * Hàm để gom việc ánh xạ View vào một chỗ cho gọn.
     */
    private void initViews() {
        etEmail = findViewById(R.id.etEmail); // Đảm bảo ID này đúng
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvCancel = findViewById(R.id.tvCancel);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
    }

    /**
     * Hàm để gom việc thiết lập sự kiện click.
     */
    private void setupClickListeners() {
        // Sự kiện click cho nút Đăng nhập
        btnLogin.setOnClickListener(v -> performLogin());

        // Sự kiện click cho nút Đăng ký
        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });


        // Bạn có thể thêm sự kiện cho các nút khác ở đây
        // findViewById(R.id.tvForgotPassword).setOnClickListener(...)
        tvCancel.setOnClickListener(v -> {
            // Đơn giản là đóng màn hình hiện tại lại
            finish();
        });
    }

    /**
     * Hàm xử lý logic chính khi nhấn nút Đăng nhập.
     */
    private void performLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // --- Kiểm tra dữ liệu đầu vào ---
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Vui lòng nhập email hợp lệ");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return;
        }

        // --- Gọi Firebase để đăng nhập ---
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng nhập thành công
                        Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                        // Chuyển sang màn hình chính
                        goToMainActivity();
                    } else {
                        // Đăng nhập thất bại, hiển thị thông báo lỗi
                        Toast.makeText(LoginActivity.this, "Đăng nhập thất bại: Email hoặc mật khẩu không đúng.",
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Hàm để chuyển sang MainActivity và xóa các màn hình trước đó.
     */
    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        // Flags này giúp người dùng không thể nhấn Back để quay lại màn hình Login
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng LoginActivity để không quay lại được
    }
}