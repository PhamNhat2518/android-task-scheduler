package vn.edu.tlu.appquanlylichtrinh.controller;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout; // Thêm import này
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView; // Thêm import này
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import vn.edu.tlu.appquanlylichtrinh.R;

public class SettingsActivity extends AppCompatActivity {

    // --- Khai báo View và Firebase ---
    private TextView tvDone;
    private MaterialButton btnAuthAction; // Đổi tên biến cho chung chung hơn
    private FirebaseAuth mAuth;

    // Thêm biến cho chức năng chia sẻ lịch trình
    private RelativeLayout rowShareSchedule;
    private MaterialCardView shareCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Khởi tạo Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Ánh xạ View
        btnAuthAction = findViewById(R.id.btnLogin); // Vẫn dùng ID cũ là btnLogin
        tvDone = findViewById(R.id.tvDone);

        // Ánh xạ cho chức năng chia sẻ lịch trình
        rowShareSchedule = findViewById(R.id.rowShareSchedule);
        shareCard = findViewById(R.id.shareCard);

        // Xử lý hiện/ẩn khung chia sẻ khi bấm "Chia sẻ lịch trình với bạn bè"
        if (rowShareSchedule != null && shareCard != null) {
            rowShareSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (shareCard.getVisibility() == View.GONE) {
                        shareCard.setVisibility(View.VISIBLE);
                    } else {
                        shareCard.setVisibility(View.GONE);
                    }
                }
            });
        }

        // Thiết lập sự kiện click cho nút "Xong"
        tvDone.setOnClickListener(v -> finish());
    }

    // --- PHƯƠNG THỨC QUAN TRỌNG: onStart() ---
    // onStart() được gọi mỗi khi người dùng quay lại màn hình này,
    // đảm bảo giao diện luôn được cập nhật đúng trạng thái.
    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra trạng thái đăng nhập và cập nhật giao diện
        updateUI();
    }

    /**
     * Kiểm tra trạng thái đăng nhập của người dùng và cập nhật giao diện (nút bấm).
     */
    private void updateUI() {
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // --- TRƯỜNG HỢP: NGƯỜI DÙNG ĐÃ ĐĂNG NHẬP ---
            // Đổi chữ trên nút thành "Đăng xuất"
            btnAuthAction.setText("Đăng xuất");
            // Đổi màu nút thành màu đỏ
            btnAuthAction.setBackgroundColor(Color.parseColor("#F44336"));

            // Thiết lập sự kiện click để thực hiện ĐĂNG XUẤT
            btnAuthAction.setOnClickListener(v -> showLogoutConfirmationDialog());

        } else {
            // --- TRƯỜNG HỢP: NGƯỜI DÙNG CHƯA ĐĂNG NHẬP ---
            // Đổi chữ trên nút thành "Đăng nhập"
            btnAuthAction.setText("Đăng nhập");
            // Đổi màu nút về màu xanh/tím ban đầu
            btnAuthAction.setBackgroundColor(Color.parseColor("#4F00FF"));

            // Thiết lập sự kiện click để thực hiện ĐĂNG NHẬP
            btnAuthAction.setOnClickListener(v -> goToLoginActivity());
        }
    }

    /**
     * Hiển thị hộp thoại xác nhận trước khi đăng xuất.
     */
    private void showLogoutConfirmationDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Xác nhận đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Nếu người dùng chọn "Đăng xuất", thực hiện đăng xuất
                    performLogout();
                })
                .setNegativeButton("Hủy bỏ", null)
                .show();
    }

    /**
     * Thực hiện đăng xuất khỏi Firebase và chuyển về màn hình Login.
     */
    private void performLogout() {
        mAuth.signOut(); // Dòng code chính để đăng xuất
        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();

        // Sau khi đăng xuất, quay về màn hình Login và xóa hết các màn hình cũ
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng cả SettingsActivity
    }

    /**
     * Chuyển đến màn hình Login.
     */
    private void goToLoginActivity() {
        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}