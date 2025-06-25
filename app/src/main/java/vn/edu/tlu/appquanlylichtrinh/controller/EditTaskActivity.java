package vn.edu.tlu.appquanlylichtrinh.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.model.Task;

public class EditTaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK = "EXTRA_TASK";

    // Khai báo View
    private EditText etTaskTitle, etSubtask;
    private RelativeLayout rowDate, rowStartTime, rowEndTime;
    private TextView tvDateValue, tvStartTimeValue, tvEndTimeValue, tvSave, tvCancel;

    // Biến để lưu trữ dữ liệu
    private Task currentTask;
    private Calendar selectedDateTime = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task); // Tái sử dụng layout

        initViews();

        currentTask = (Task) getIntent().getSerializableExtra(EXTRA_TASK);

        if (currentTask != null) {
            populateData(); // Điền dữ liệu cũ
            setupClickListeners(); // Thiết lập sự kiện
        } else {
            Toast.makeText(this, "Lỗi: Không có dữ liệu tác vụ.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initViews() {
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etSubtask = findViewById(R.id.etSubtask);
        rowDate = findViewById(R.id.rowDate);
        rowStartTime = findViewById(R.id.rowStartTime);
        rowEndTime = findViewById(R.id.rowEndTime);
        tvDateValue = findViewById(R.id.tvDateValue);
        tvStartTimeValue = findViewById(R.id.tvStartTimeValue);
        tvEndTimeValue = findViewById(R.id.tvEndTimeValue);
        tvSave = findViewById(R.id.tvSave);
        tvCancel = findViewById(R.id.tvCancel);
    }

    /**
     * Điền dữ liệu của task hiện tại vào các View và các biến Calendar.
     */
    private void populateData() {
        // Điền vào các ô text
        etTaskTitle.setText(currentTask.getTitle());
        etSubtask.setText(currentTask.getSubtask());

        // Chuyển đổi chuỗi ngày/giờ từ Task sang các đối tượng Calendar
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Date date = dateFormat.parse(currentTask.getDate());
            selectedDateTime.setTime(date);

            Date sTime = timeFormat.parse(currentTask.getStartTime());
            startTime.setTime(sTime);

            Date eTime = timeFormat.parse(currentTask.getEndTime());
            endTime.setTime(eTime);
        } catch (ParseException e) {
            e.printStackTrace();
            // Nếu có lỗi, dùng thời gian hiện tại
        }

        // Cập nhật lại giao diện
        updateDateTimeViews();
    }

    /**
     * Cập nhật các TextView ngày giờ.
     */
    private void updateDateTimeViews() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault());
        tvDateValue.setText(dateFormat.format(selectedDateTime.getTime()));

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvStartTimeValue.setText(timeFormat.format(startTime.getTime()));
        tvEndTimeValue.setText(timeFormat.format(endTime.getTime()));
    }

    // --- PHẦN LOGIC CHỌN NGÀY/GIỜ ĐƯỢC THÊM VÀO ĐÂY ---

    private void setupClickListeners() {
        tvSave.setOnClickListener(v -> updateTaskInFirebase());
        tvCancel.setOnClickListener(v -> finish());

        rowDate.setOnClickListener(v -> showDatePickerDialog());
        rowStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        rowEndTime.setOnClickListener(v -> showTimePickerDialog(false));
    }

    private void showDatePickerDialog() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
            selectedDateTime.set(selectedYear, selectedMonth, selectedDayOfMonth);
            updateDateTimeViews();
        }, year, month, day).show();
    }

    private void showTimePickerDialog(boolean isStartTime) {
        Calendar calendarToShow = isStartTime ? this.startTime : this.endTime;
        int hour = calendarToShow.get(Calendar.HOUR_OF_DAY);
        int minute = calendarToShow.get(Calendar.MINUTE);

        new TimePickerDialog(this, (view, selectedHour, selectedMinute) -> {
            if (isStartTime) {
                this.startTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                this.startTime.set(Calendar.MINUTE, selectedMinute);
            } else {
                this.endTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                this.endTime.set(Calendar.MINUTE, selectedMinute);
            }
            updateDateTimeViews();
        }, hour, minute, true).show();
    }


    /**
     * Cập nhật lại tác vụ trên Firebase.
     */
    private void updateTaskInFirebase() {
        String newTitle = etTaskTitle.getText().toString().trim();
        String newSubtask = etSubtask.getText().toString().trim();

        if (TextUtils.isEmpty(newTitle)) {
            etTaskTitle.setError("Tiêu đề không được để trống");
            etTaskTitle.requestFocus();
            return;
        }

        // Lấy dữ liệu ngày giờ MỚI từ các TextView
        String newDate = tvDateValue.getText().toString();
        String newStartTime = tvStartTimeValue.getText().toString();
        String newEndTime = tvEndTimeValue.getText().toString();

        // Cập nhật lại các trường trong đối tượng Task
        currentTask.setTitle(newTitle);
        currentTask.setSubtask(newSubtask);
        currentTask.setDate(newDate);
        currentTask.setStartTime(newStartTime);
        currentTask.setEndTime(newEndTime);

        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference()
                .child("tasks")
                .child(currentTask.getUserId())
                .child(currentTask.getTaskId());

        taskRef.setValue(currentTask)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(EditTaskActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(EditTaskActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}