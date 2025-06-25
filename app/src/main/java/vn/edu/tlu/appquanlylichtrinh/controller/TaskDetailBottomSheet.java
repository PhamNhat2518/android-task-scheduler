package vn.edu.tlu.appquanlylichtrinh.controller; // Hoặc package view của bạn

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

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
                // --- PHẦN SỬA LỖI NẰM Ở ĐÂY ---

                // 1. Điền dữ liệu cho tất cả các View
                tvDetailTitle.setText(currentTask.getTitle());

                // Xử lý và hiển thị ngày tháng
                tvDetailDate.setText(formatDateForDisplay(currentTask.getDate()));

                // Hiển thị giờ
                String timeRange = currentTask.getStartTime() + " - " + currentTask.getEndTime();
                tvDetailTime.setText(timeRange);

                // Hiển thị công việc phụ
                if (currentTask.getSubtask() != null && !currentTask.getSubtask().isEmpty()) {
                    tvDetailSubtasks.setText(currentTask.getSubtask());
                    tvDetailSubtasks.setVisibility(View.VISIBLE);
                } else {
                    // Ẩn đi nếu không có công việc phụ
                    view.findViewById(R.id.labelSubtasks).setVisibility(View.GONE); // Cần thêm ID "labelSubtasks" vào TextView "Nhiệm vụ phụ" trong XML
                    tvDetailSubtasks.setVisibility(View.GONE);
                }

                // 2. Thiết lập sự kiện click
                btnEdit.setOnClickListener(v -> {
                    Intent intent = new Intent(getContext(), EditTaskActivity.class);
                    intent.putExtra(EditTaskActivity.EXTRA_TASK, currentTask);
                    startActivity(intent);
                    dismiss();
                });

                btnDelete.setOnClickListener(v -> {
                    // TODO: Thêm logic xóa task
                });
            }
        }
    }

    /**
     * Hàm tiện ích để chuyển đổi định dạng ngày từ "Ngày dd/MM/yyyy" sang "dd tháng MM".
     * @param firebaseDate Chuỗi ngày từ Firebase.
     * @return Chuỗi ngày đã được định dạng lại.
     */
    private String formatDateForDisplay(String firebaseDate) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("d 'tháng' M", new Locale("vi", "VN"));
            Date date = originalFormat.parse(firebaseDate);
            return targetFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Nếu có lỗi, trả về chuỗi gốc
            return firebaseDate;
        }
    }
}