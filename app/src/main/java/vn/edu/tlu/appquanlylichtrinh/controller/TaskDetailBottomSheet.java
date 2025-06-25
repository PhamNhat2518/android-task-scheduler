package vn.edu.tlu.appquanlylichtrinh.controller; // Hoặc package view của bạn

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast; // Thêm import này
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog; // Thêm import này
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DatabaseReference; // Thêm import này
import com.google.firebase.database.FirebaseDatabase; // Thêm import này

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.model.Task;

public class TaskDetailBottomSheet extends BottomSheetDialogFragment {

    public static final String ARG_TASK = "ARG_TASK";

    public static TaskDetailBottomSheet newInstance(Task task) {
        TaskDetailBottomSheet fragment = new TaskDetailBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK, task);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_task_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Ánh xạ các View
        TextView tvDetailTitle = view.findViewById(R.id.tvDetailTitle);
        TextView tvDetailDate = view.findViewById(R.id.tvDetailDate);
        TextView tvDetailTime = view.findViewById(R.id.tvDetailTime);
        TextView tvDetailSubtasks = view.findViewById(R.id.tvDetailSubtasks);
        View btnEdit = view.findViewById(R.id.btnEdit);
        View btnDelete = view.findViewById(R.id.btnDelete);

        Bundle args = getArguments();
        if (args != null) {
            Task currentTask = (Task) args.getSerializable(ARG_TASK);
            if (currentTask != null) {
                // Điền dữ liệu cho tất cả các View
                tvDetailTitle.setText(currentTask.getTitle());
                tvDetailDate.setText(formatDateForDisplay(currentTask.getDate()));
                String timeRange = currentTask.getStartTime() + " - " + currentTask.getEndTime();
                tvDetailTime.setText(timeRange);

                if (currentTask.getSubtask() != null && !currentTask.getSubtask().isEmpty()) {
                    tvDetailSubtasks.setText(currentTask.getSubtask());
                    tvDetailSubtasks.setVisibility(View.VISIBLE);
                    view.findViewById(R.id.labelSubtasks).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.labelSubtasks).setVisibility(View.GONE);
                    tvDetailSubtasks.setVisibility(View.GONE);
                }

                // Thiết lập sự kiện click cho nút Sửa
                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), EditTaskActivity.class);
                    intent.putExtra(EditTaskActivity.EXTRA_TASK, currentTask);
                    startActivity(intent);
                    dismiss();
                });

                // --- PHẦN LOGIC XÓA ĐÃ ĐƯỢC THÊM VÀO ĐÂY ---
                btnDelete.setOnClickListener(v -> {
                    // Hiển thị hộp thoại xác nhận trước khi xóa
                    showDeleteConfirmationDialog(currentTask);
                });
            }
        }
    }

    /**
     * Hiển thị một hộp thoại để người dùng xác nhận việc xóa.
     * @param task Tác vụ sẽ bị xóa.
     */
    private void showDeleteConfirmationDialog(Task task) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc chắn muốn xóa tác vụ '" + task.getTitle() + "' không?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    // Nếu người dùng nhấn "Xóa", gọi hàm để xóa trên Firebase
                    deleteTaskFromFirebase(task);
                })
                .setNegativeButton("Hủy bỏ", null) // Không làm gì khi nhấn Hủy bỏ
                .setIcon(R.drawable.ic_delete_white) // Dùng icon xóa cho đẹp
                .show();
    }

    /**
     * Xóa tác vụ khỏi Firebase Realtime Database.
     * @param task Tác vụ cần xóa.
     */
    private void deleteTaskFromFirebase(Task task) {
        // Lấy tham chiếu đến đúng tác vụ cần xóa
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference()
                .child("tasks")
                .child(task.getUserId())
                .child(task.getTaskId());

        // Gọi removeValue() để xóa toàn bộ nút dữ liệu của tác vụ đó
        taskRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    // Xóa thành công
                    Toast.makeText(getContext(), "Đã xóa tác vụ thành công", Toast.LENGTH_SHORT).show();
                    // Đóng BottomSheet sau khi xóa
                    dismiss();
                })
                .addOnFailureListener(e -> {
                    // Xóa thất bại
                    Toast.makeText(getContext(), "Lỗi khi xóa: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private String formatDateForDisplay(String firebaseDate) {
        // ... (hàm này không đổi) ...
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("d 'tháng' M", new Locale("vi", "VN"));
            Date date = originalFormat.parse(firebaseDate);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return firebaseDate;
        }
    }
}