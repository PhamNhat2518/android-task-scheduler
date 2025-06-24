package vn.edu.tlu.appquanlylichtrinh.controller; // Hoặc package view của bạn

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;
import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.model.Task;

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_TASK = 1;

    private List<Object> items;

    public ScheduleAdapter(List<Object> items) {
        this.items = items;
    }

    // --- ViewHolder cho Header (ĐÃ THÊM CONSTRUCTOR) ---
    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayOfWeek, tvDate;
        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView); // Dòng này là bắt buộc
            tvDayOfWeek = itemView.findViewById(R.id.tvDayOfWeek);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    // --- ViewHolder cho Task (ĐÃ THÊM CONSTRUCTOR VÀ ÁNH XẠ) ---
    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView tvTaskTime, tvTaskTitle;
        CheckBox cbTaskCompleted, cbSubtask;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView); // Dòng này là bắt buộc
            tvTaskTime = itemView.findViewById(R.id.tvTaskTime);
            tvTaskTitle = itemView.findViewById(R.id.tvTaskTitle);
            cbSubtask = itemView.findViewById(R.id.cbSubtask);
            cbTaskCompleted = itemView.findViewById(R.id.cbTaskCompleted);
        }
    }

    // --- Các phương thức Adapter ---
    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            return VIEW_TYPE_HEADER;
        } else if (items.get(position) instanceof Task) {
            return VIEW_TYPE_TASK;
        }
        return -1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_day_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_task, parent, false);
            return new TaskViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            String dateHeader = (String) items.get(position);
            headerHolder.tvDayOfWeek.setText(dateHeader);
            headerHolder.tvDate.setText("");
        } else {
            TaskViewHolder taskHolder = (TaskViewHolder) holder;
            Task currentTask = (Task) items.get(position);

            taskHolder.tvTaskTime.setText(currentTask.getStartTime());
            taskHolder.tvTaskTitle.setText(currentTask.getTitle());
            taskHolder.cbTaskCompleted.setChecked(currentTask.isCompleted());

            if (currentTask.getSubtask() != null && !currentTask.getSubtask().isEmpty()) {
                taskHolder.cbSubtask.setText(currentTask.getSubtask());
                taskHolder.cbSubtask.setChecked(currentTask.isSubtaskCompleted());
                taskHolder.cbSubtask.setVisibility(View.VISIBLE);
            } else {
                taskHolder.cbSubtask.setVisibility(View.GONE);
            }

            taskHolder.cbTaskCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateTaskStatus(currentTask.getUserId(), currentTask.getTaskId(), "completed", isChecked);
            });

            taskHolder.cbSubtask.setOnCheckedChangeListener((buttonView, isChecked) -> {
                updateTaskStatus(currentTask.getUserId(), currentTask.getTaskId(), "subtaskCompleted", isChecked);
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private void updateTaskStatus(String userId, String taskId, String fieldToUpdate, boolean isCompleted) {
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference()
                .child("tasks")
                .child(userId)
                .child(taskId);
        taskRef.child(fieldToUpdate).setValue(isCompleted);
    }
}