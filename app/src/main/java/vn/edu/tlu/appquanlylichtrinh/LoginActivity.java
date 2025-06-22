package vn.edu.tlu.appquanlylichtrinh;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {
    EditText etPhone, etPassword;
    Button btnLogin, btnRegister;
    TextView tvForgotPassword, tvCancel, tvWelcome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etPhone = findViewById(R.id.etPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvCancel = findViewById(R.id.tvCancel);
        tvWelcome = findViewById(R.id.tvWelcome);

        // Nhận tên người dùng từ Intent
        String userName = getIntent().getStringExtra("USER_NAME");
        if (userName != null && !userName.isEmpty()) {
            tvWelcome.setVisibility(View.VISIBLE);
            tvWelcome.setText("Xin chào, " + userName + "!");
        } else {
            tvWelcome.setVisibility(View.GONE);
        }

        btnLogin.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if(phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            } else if (!phone.matches("^0\\d{9}$")) {
                Toast.makeText(this, "Số điện thoại phải đủ 10 số và bắt đầu bằng 0", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu tối thiểu 6 ký tự", Toast.LENGTH_SHORT).show();
            } else {
                // Xử lý đăng nhập ở đây
                Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        tvForgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        tvCancel.setOnClickListener(v -> {
            // Đóng màn hình đăng nhập, quay lại màn trước
            finish();
        });
    }
}