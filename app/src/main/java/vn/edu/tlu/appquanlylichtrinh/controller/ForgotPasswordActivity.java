package vn.edu.tlu.appquanlylichtrinh.controller;


import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.button.MaterialButton;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

import vn.edu.tlu.appquanlylichtrinh.R;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText etEmail;
    private MaterialButton btnConfirm;
    private TextView tvCancel;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password); // Đảm bảo tên layout đúng

        etEmail = findViewById(R.id.etEmail);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvCancel = findViewById(R.id.tvCancel);
        mAuth = FirebaseAuth.getInstance();

        // Nút hủy bỏ: trở lại màn hình trước
        tvCancel.setOnClickListener(v -> finish());

        // Nút xác nhận: gửi email reset password
        btnConfirm.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Vui lòng nhập email");
                etEmail.requestFocus();
                return;
            }
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Đã gửi email đặt lại mật khẩu. Vui lòng kiểm tra hộp thư!",
                                    Toast.LENGTH_LONG
                            ).show();
                            finish(); // Quay lại màn hình trước
                        } else {
                            Toast.makeText(
                                    ForgotPasswordActivity.this,
                                    "Gửi email thất bại: " +
                                            (task.getException() != null ? task.getException().getMessage() : ""),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });
    }
}