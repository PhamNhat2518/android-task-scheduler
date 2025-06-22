package vn.edu.tlu.appquanlylichtrinh;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ForgotPasswordActivity extends AppCompatActivity {
    EditText etPhone;
    Button btnConfirm;
    TextView tvCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        etPhone = findViewById(R.id.etPhone);
        btnConfirm = findViewById(R.id.btnConfirm);
        tvCancel = findViewById(R.id.tvCancel);

        tvCancel.setOnClickListener(v -> finish());

        btnConfirm.setOnClickListener(v -> {
            String phone = etPhone.getText().toString().trim();
            if (phone.isEmpty() || !phone.matches("^0\\d{9}$")) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại hợp lệ!", Toast.LENGTH_SHORT).show();
            } else {
                // Giả lập gửi mã xác nhận, thực tế sẽ gọi API gửi mã
                // Chuyển sang màn hình đặt lại mật khẩu
                Intent intent = new Intent(ForgotPasswordActivity.this, ResetPasswordActivity.class);
                intent.putExtra("PHONE", phone);
                startActivity(intent);
            }
        });
    }
}