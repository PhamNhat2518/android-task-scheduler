// Tạo file mới: adapter/ScheduleAdapter.java
package vn.edu.tlu.appquanlylichtrinh.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.model.DaySchedule;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.DayViewHolder> {

    private List<DaySchedule> dayScheduleList;
    private Context context;

    public ScheduleAdapter(List<DaySchedule> dayScheduleList, Context context) {
        this.dayScheduleList = dayScheduleList;
        this.context = context;
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_schedule_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        DaySchedule currentDay = dayScheduleList.get(position);

        // Định dạng và hiển thị tiêu đề ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, 'ngày' dd 'tháng' MM", new Locale("vi", "VN"));
        holder.dayHeaderText.setText(dateFormat.format(currentDay.getDate()));

        // --- Chìa khóa ở đây: Thiết lập cho RecyclerView lồng bên trong ---
        // 1. Tạo một TaskAdapter mới cho danh sách công việc của ngày hôm đó
        TaskAdapter innerTaskAdapter = new TaskAdapter(currentDay.getTasks(), context);

        // 2. Thiết lập LayoutManager và Adapter cho inner RecyclerView
        holder.innerTasksRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        holder.innerTasksRecyclerView.setAdapter(innerTaskAdapter);
        holder.innerTasksRecyclerView.setHasFixedSize(true); // Tối ưu hóa
    }

    @Override
    public int getItemCount() {
        return dayScheduleList.size();
    }

    public static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView dayHeaderText;
        RecyclerView innerTasksRecyclerView;

        public DayViewHolder(@NonNull View itemView) {
            super(itemView);
            dayHeaderText = itemView.findViewById(R.id.day_header_text);
            innerTasksRecyclerView = itemView.findViewById(R.id.inner_tasks_recycler_view);
        }
    }
}