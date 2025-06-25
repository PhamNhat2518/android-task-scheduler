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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.model.Task;

public class AddTaskActivity extends AppCompatActivity {

    // --- Khai báo View và Firebase ---
    private EditText etTaskTitle, etSubtask;
    private RelativeLayout rowDate, rowStartTime, rowEndTime;
    private TextView tvDateValue, tvStartTimeValue, tvEndTimeValue, tvAdd, tvCancel;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    // Biến để lưu trữ ngày giờ đã chọn
    private Calendar selectedDateTime = Calendar.getInstance();
    private Calendar startTime = Calendar.getInstance();
    private Calendar endTime = Calendar.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Khởi tạo Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Ánh xạ View
        initViews();

        // Cập nhật giao diện với ngày giờ hiện tại
        updateDateTimeViews();

        // Thiết lập sự kiện click
        setupClickListeners();
    }

    private void initViews() {
        // Ánh xạ các thành phần nhập liệu
        etTaskTitle = findViewById(R.id.etTaskTitle);
        etSubtask = findViewById(R.id.etSubtask);

        // Ánh xạ các hàng và giá trị ngày/giờ
        rowDate = findViewById(R.id.rowDate);
        rowStartTime = findViewById(R.id.rowStartTime);
        rowEndTime = findViewById(R.id.rowEndTime);
        tvDateValue = findViewById(R.id.tvDateValue);
        tvStartTimeValue = findViewById(R.id.tvStartTimeValue);
        tvEndTimeValue = findViewById(R.id.tvEndTimeValue);

        // Ánh xạ nút "Thêm" và "Hủy bỏ"
        tvAdd = findViewById(R.id.tvSave);
        tvCancel = findViewById(R.id.tvCancel);
    }

    private void setupClickListeners() {
        // Sự kiện click cho các hàng chọn ngày/giờ
        rowDate.setOnClickListener(v -> showDatePickerDialog());
        rowStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        rowEndTime.setOnClickListener(v -> showTimePickerDialog(false));

        // Sự kiện click cho nút "Thêm" và "Hủy bỏ"
        tvAdd.setOnClickListener(v -> saveTaskToFirebase());
        tvCancel.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog() {
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    selectedDateTime.set(Calendar.YEAR, selectedYear);
                    selectedDateTime.set(Calendar.MONTH, selectedMonth);
                    selectedDateTime.set(Calendar.DAY_OF_MONTH, selectedDayOfMonth);
                    updateDateTimeViews();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePickerDialog(boolean isStartTime) {
        // Dùng calendar của startTime hoặc endTime để lấy giờ hiện tại của chúng
        Calendar calendarToShow = isStartTime ? startTime : endTime;
        int hour = calendarToShow.get(Calendar.HOUR_OF_DAY);
        int minute = calendarToShow.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    if (isStartTime) {
                        startTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        startTime.set(Calendar.MINUTE, selectedMinute);
                    } else {
                        endTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                        endTime.set(Calendar.MINUTE, selectedMinute);
                    }
                    updateDateTimeViews();
                }, hour, minute, true);
        timePickerDialog.show();
    }

    private void updateDateTimeViews() {
        // Định dạng và hiển thị ngày
        SimpleDateFormat dateFormat = new SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault());
        tvDateValue.setText(dateFormat.format(selectedDateTime.getTime()));

        // Định dạng và hiển thị giờ
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvStartTimeValue.setText(timeFormat.format(startTime.getTime()));
        tvEndTimeValue.setText(timeFormat.format(endTime.getTime()));
    }

    /**
     * Hàm xử lý logic chính khi nhấn nút "Thêm".
     */
    private void saveTaskToFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Bạn cần đăng nhập để thêm tác vụ!", Toast.LENGTH_SHORT).show();
            return;
        }
        String userId = currentUser.getUid();

        String title = etTaskTitle.getText().toString().trim();
        String subtask = etSubtask.getText().toString().trim();

        // Lấy dữ liệu ngày giờ từ các TextView đã được cập nhật
        String date = tvDateValue.getText().toString();
        String startTimeStr = tvStartTimeValue.getText().toString();
        String endTimeStr = tvEndTimeValue.getText().toString();

        if (TextUtils.isEmpty(title)) {
            etTaskTitle.setError("Tiêu đề không được để trống");
            etTaskTitle.requestFocus();
            return;
        }

        String taskId = mDatabase.child("tasks").child(userId).push().getKey();
        if (taskId == null) {
            Toast.makeText(this, "Không thể tạo tác vụ, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            return;
        }

        Task newTask = new Task(taskId, title, subtask, date, startTimeStr, endTimeStr, userId);

        mDatabase.child("tasks").child(userId).child(taskId).setValue(newTask)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(AddTaskActivity.this, "Thêm tác vụ thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(AddTaskActivity.this, "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}