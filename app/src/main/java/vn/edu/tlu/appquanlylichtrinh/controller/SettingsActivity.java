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
import android.app.AlertDialog;
import android.widget.EditText;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import java.util.ArrayList;
import java.util.List;
import vn.edu.tlu.appquanlylichtrinh.model.Task;

import vn.edu.tlu.appquanlylichtrinh.R;
import android.content.Context;

public class SettingsActivity extends AppCompatActivity {

    // --- Khai báo View và Firebase ---
    private TextView tvDone;
    private MaterialButton btnAuthAction; // Đổi tên biến cho chung chung hơn
    private FirebaseAuth mAuth;
    private RelativeLayout layoutReceiveSchedule, layoutShareSchedule;

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
        layoutReceiveSchedule = findViewById(R.id.layoutReceiveSchedule);
        layoutShareSchedule = findViewById(R.id.layoutShareSchedule);

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

        layoutReceiveSchedule.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            new ReceiveScheduleDialogFragment().show(fm, "receive_schedule");
        });
        layoutShareSchedule.setOnClickListener(v -> {
            FragmentManager fm = getSupportFragmentManager();
            new ShareScheduleDialogFragment().show(fm, "share_schedule");
        });
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

    // --- FAKE DIALOG CHIA SẺ LỊCH TRÌNH ---
    public static class ShareScheduleDialogFragment extends DialogFragment {
        @Override
        public android.app.Dialog onCreateDialog(android.os.Bundle savedInstanceState) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Chọn nhiệm vụ để chia sẻ");
            // Lấy danh sách nhiệm vụ thực từ Firebase
            FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                builder.setMessage("Bạn cần đăng nhập để chia sẻ lịch trình!");
                builder.setPositiveButton("OK", null);
                return builder.create();
            }
            String userId = currentUser.getUid();
            com.google.firebase.database.DatabaseReference tasksRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("tasks").child(userId);
            android.app.AlertDialog loadingDialog = new android.app.AlertDialog.Builder(getActivity())
                    .setMessage("Đang tải danh sách nhiệm vụ...")
                    .setCancelable(false)
                    .create();
            loadingDialog.show();
            tasksRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                    loadingDialog.dismiss();
                    java.util.List<Task> taskList = new java.util.ArrayList<>();
                    for (com.google.firebase.database.DataSnapshot taskSnap : snapshot.getChildren()) {
                        Task task = taskSnap.getValue(Task.class);
                        if (task != null) taskList.add(task);
                    }
                    java.util.List<Task> validTasks = new java.util.ArrayList<>();
                    for (Task t : taskList) {
                        if (t != null && t.getTitle() != null) validTasks.add(t);
                    }
                    if (validTasks.isEmpty()) {
                        new android.app.AlertDialog.Builder(getActivity())
                                .setTitle("Không có nhiệm vụ")
                                .setMessage("Bạn chưa có nhiệm vụ nào để chia sẻ!")
                                .setPositiveButton("OK", null)
                                .show();
                        return;
                    }
                    String[] taskTitles = new String[validTasks.size()];
                    boolean[] checkedItems = new boolean[validTasks.size()];
                    for (int i = 0; i < validTasks.size(); i++) {
                        taskTitles[i] = validTasks.get(i).getTitle();
                        checkedItems[i] = false;
                    }
                    android.app.AlertDialog shareDialog = new android.app.AlertDialog.Builder(getActivity())
                            .setTitle("Chọn nhiệm vụ để chia sẻ")
                            .setMultiChoiceItems(taskTitles, checkedItems, (dialog, which, isChecked) -> {
                                checkedItems[which] = isChecked;
                            })
                            .setPositiveButton("Tạo mã chia sẻ", null)
                            .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                            .create();
                    shareDialog.setOnShowListener(d -> {
                        android.widget.Button btn = shareDialog.getButton(android.app.AlertDialog.BUTTON_POSITIVE);
                        btn.setOnClickListener(v -> {
                            java.util.List<Task> selected = new java.util.ArrayList<>();
                            for (int i = 0; i < checkedItems.length; i++) {
                                if (checkedItems[i]) selected.add(validTasks.get(i));
                            }
                            if (selected.isEmpty()) {
                                android.widget.Toast.makeText(getActivity(), "Chọn ít nhất 1 nhiệm vụ!", android.widget.Toast.LENGTH_SHORT).show();
                            } else {
                                String shareCode = "SHARE" + (int)(Math.random()*100000);
                                com.google.firebase.database.DatabaseReference sharedRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("shared_schedules").child(shareCode);
                                saveTasksRecursively(sharedRef, selected, 0, () -> {
                                    android.app.Activity activity = getActivity();
                                    if (activity != null && !activity.isFinishing()) {
                                        new android.app.AlertDialog.Builder(activity)
                                            .setTitle("Mã chia sẻ")
                                            .setMessage("Gửi mã này cho bạn bè: " + shareCode + "\n(Số nhiệm vụ: " + selected.size() + ")")
                                            .setPositiveButton("OK", (d2, w2) -> shareDialog.dismiss())
                                            .show();
                                    }
                                });
                            }
                        });
                    });
                    shareDialog.show();
                }
                @Override
                public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                    loadingDialog.dismiss();
                    new android.app.AlertDialog.Builder(getActivity())
                            .setTitle("Lỗi")
                            .setMessage("Không thể tải danh sách nhiệm vụ!")
                            .setPositiveButton("OK", null)
                            .show();
                }
            });
            // Trả về dialog loading ban đầu (sẽ bị dismiss khi có dữ liệu)
            return loadingDialog;
        }

        private void saveTasksRecursively(com.google.firebase.database.DatabaseReference ref, java.util.List<Task> tasks, int index, Runnable onComplete) {
            if (index >= tasks.size()) {
                onComplete.run();
                return;
            }
            String newId = ref.push().getKey();
            if (newId != null) {
                ref.child(newId).setValue(tasks.get(index)).addOnCompleteListener(task -> {
                    saveTasksRecursively(ref, tasks, index + 1, onComplete);
                });
            } else {
                saveTasksRecursively(ref, tasks, index + 1, onComplete);
            }
        }
    }

    // --- FAKE DIALOG NHẬN LỊCH TRÌNH ---
    public static class ReceiveScheduleDialogFragment extends DialogFragment {
        @Override
        public android.app.Dialog onCreateDialog(android.os.Bundle savedInstanceState) {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Nhập mã chia sẻ");
            final android.widget.EditText input = new android.widget.EditText(getActivity());
            builder.setView(input);
            builder.setPositiveButton("Tiếp tục", (dialog, which) -> {
                String shareCode = input.getText().toString().trim();
                if (shareCode.isEmpty()) {
                    android.widget.Toast.makeText(getActivity(), "Vui lòng nhập mã chia sẻ!", android.widget.Toast.LENGTH_SHORT).show();
                    return;
                }
                // Lấy danh sách nhiệm vụ từ shared_schedules/{shareCode}
                com.google.firebase.database.DatabaseReference sharedRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("shared_schedules").child(shareCode);
                android.app.AlertDialog loadingDialog = new android.app.AlertDialog.Builder(getActivity())
                        .setMessage("Đang tải nhiệm vụ...")
                        .setCancelable(false)
                        .create();
                loadingDialog.show();
                sharedRef.addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        loadingDialog.dismiss();
                        java.util.List<Task> taskList = new java.util.ArrayList<>();
                        for (com.google.firebase.database.DataSnapshot taskSnap : snapshot.getChildren()) {
                            Task task = taskSnap.getValue(Task.class);
                            if (task != null) taskList.add(task);
                        }
                        java.util.List<Task> validTasks = new java.util.ArrayList<>();
                        for (Task t : taskList) {
                            if (t != null && t.getTitle() != null) validTasks.add(t);
                        }
                        if (validTasks.isEmpty()) {
                            new android.app.AlertDialog.Builder(getActivity())
                                    .setTitle("Không có nhiệm vụ")
                                    .setMessage("Mã chia sẻ không hợp lệ hoặc không có nhiệm vụ nào!")
                                    .setPositiveButton("OK", null)
                                    .show();
                            return;
                        }
                        String[] taskTitles = new String[validTasks.size()];
                        boolean[] checkedItems = new boolean[validTasks.size()];
                        for (int i = 0; i < validTasks.size(); i++) {
                            taskTitles[i] = validTasks.get(i).getTitle();
                            checkedItems[i] = true;
                        }
                        new android.app.AlertDialog.Builder(getActivity())
                                .setTitle("Chọn nhiệm vụ muốn nhận")
                                .setMultiChoiceItems(taskTitles, checkedItems, (dialogInterface, whichItem, isChecked) -> {
                                    checkedItems[whichItem] = isChecked;
                                })
                                .setPositiveButton("Nhận nhiệm vụ", (d, w) -> {
                                    // Lưu các nhiệm vụ được chọn về tài khoản hiện tại
                                    com.google.firebase.auth.FirebaseUser currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser();
                                    if (currentUser == null) {
                                        android.widget.Toast.makeText(getActivity(), "Bạn cần đăng nhập để nhận nhiệm vụ!", android.widget.Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String myUid = currentUser.getUid();
                                    com.google.firebase.database.DatabaseReference tasksRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference("tasks").child(myUid);
                                    int count = 0;
                                    for (int i = 0; i < checkedItems.length; i++) {
                                        if (checkedItems[i]) {
                                            String newTaskId = tasksRef.push().getKey();
                                            if (newTaskId != null) {
                                                tasksRef.child(newTaskId).setValue(validTasks.get(i));
                                                count++;
                                            }
                                        }
                                    }
                                    android.widget.Toast.makeText(getActivity(), "Đã nhận " + count + " nhiệm vụ!", android.widget.Toast.LENGTH_SHORT).show();
                                })
                                .setNegativeButton("Hủy", null)
                                .show();
                    }
                    @Override
                    public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {
                        loadingDialog.dismiss();
                        new android.app.AlertDialog.Builder(getActivity())
                                .setTitle("Lỗi")
                                .setMessage("Không thể tải nhiệm vụ từ mã chia sẻ!")
                                .setPositiveButton("OK", null)
                                .show();
                    }
                });
            });
            builder.setNegativeButton("Hủy", null);
            return builder.create();
        }
    }
}