package vn.edu.tlu.appquanlylichtrinh.controller;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.model.User;

public class RegisterActivity extends AppCompatActivity {

    // --- Khai báo View và Firebase ---
    private EditText etEmail, etPassword, etConfirmPassword;
    private MaterialButton btnRegister;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Ánh xạ View
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);

        // Thiết lập sự kiện click
        btnRegister.setOnClickListener(v -> performSignUp());
    }

    /**
     * Hàm xử lý logic chính khi nhấn nút Đăng ký.
     */
    private void performSignUp() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // --- Kiểm tra dữ liệu đầu vào ---
        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Vui lòng nhập email hợp lệ");
            etEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu nhập lại không khớp");
            etConfirmPassword.requestFocus();
            return;
        }

        // --- Gọi Firebase để tạo tài khoản ---
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Đăng ký thành công, lấy thông tin người dùng
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            // Tạo đối tượng User để lưu vào Realtime Database
                            User user = new User(email);

                            // Lưu thông tin user vào Realtime Database với key là UID
                            mDatabase.child("users").child(firebaseUser.getUid()).setValue(user)
                                    .addOnCompleteListener(dbTask -> {
                                        if (dbTask.isSuccessful()) {
                                            // Cả 2 bước đều thành công, hiển thị thông báo
                                            showSuccessDialog();
                                        } else {
                                            // Lỗi khi lưu vào database
                                            Toast.makeText(RegisterActivity.this, "Lỗi khi lưu dữ liệu người dùng.", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        // Đăng ký thất bại, hiển thị lỗi
                        Toast.makeText(RegisterActivity.this, "Đăng ký thất bại: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    /**
     * Hiển thị hộp thoại thông báo thành công và chuyển về màn hình Login.
     */
    private void showSuccessDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng ký thành công")
                .setMessage("Tài khoản của bạn đã được tạo. Vui lòng đăng nhập.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Chuyển về màn hình Login
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    // Xóa các màn hình trước đó để không bị quay lại
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                })
                .setCancelable(false) // Không cho phép đóng dialog bằng nút Back
                .show();
    }
}