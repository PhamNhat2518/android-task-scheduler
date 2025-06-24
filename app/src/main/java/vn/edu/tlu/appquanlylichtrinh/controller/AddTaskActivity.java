package vn.edu.tlu.appquanlylichtrinh.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import vn.edu.tlu.appquanlylichtrinh.R;

public class AddTaskActivity extends AppCompatActivity {

    // Khai báo các View cần tương tác
    private RelativeLayout rowDate, rowStartTime, rowEndTime;
    private TextView tvDateValue, tvStartTimeValue, tvEndTimeValue;

    // Biến để lưu trữ ngày giờ đã chọn
    private Calendar selectedDateTime = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        // Ánh xạ các View từ XML
        initViews();

        // Cập nhật giao diện với ngày giờ hiện tại
        updateDateTimeViews();

        // Thiết lập sự kiện click
        setupClickListeners();
    }

    private void initViews() {
        rowDate = findViewById(R.id.rowDate);
        rowStartTime = findViewById(R.id.rowStartTime);
        rowEndTime = findViewById(R.id.rowEndTime);
        tvDateValue = findViewById(R.id.tvDateValue);
        tvStartTimeValue = findViewById(R.id.tvStartTimeValue);
        tvEndTimeValue = findViewById(R.id.tvEndTimeValue);
    }

    private void setupClickListeners() {
        // Sự kiện click để mở Date Picker
        rowDate.setOnClickListener(v -> showDatePickerDialog());

        // Sự kiện click để mở Time Picker cho giờ bắt đầu
        rowStartTime.setOnClickListener(v -> showTimePickerDialog(true));

        // Sự kiện click để mở Time Picker cho giờ kết thúc
        rowEndTime.setOnClickListener(v -> showTimePickerDialog(false));
    }

    /**
     * Hiển thị hộp thoại chọn ngày (Calendar).
     */
    private void showDatePickerDialog() {
        // Lấy ngày, tháng, năm hiện tại từ biến Calendar
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        // Tạo một DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDayOfMonth) -> {
                    // Khi người dùng chọn xong, cập nhật biến Calendar
                    selectedDateTime.set(selectedYear, selectedMonth, selectedDayOfMonth);
                    // Cập nhật lại TextView hiển thị ngày
                    updateDateTimeViews();
                }, year, month, day);

        // Hiển thị dialog
        datePickerDialog.show();
    }

    /**
     * Hiển thị hộp thoại chọn giờ (Clock).
     * @param isStartTime true nếu chọn giờ bắt đầu, false nếu chọn giờ kết thúc.
     */
    private void showTimePickerDialog(boolean isStartTime) {
        // Lấy giờ, phút hiện tại từ biến Calendar
        int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
        int minute = selectedDateTime.get(Calendar.MINUTE);

        // Tạo một TimePickerDialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    // Khi người dùng chọn xong, cập nhật biến Calendar
                    // (Lưu ý: phần này chỉ cập nhật giờ/phút, không ảnh hưởng đến ngày)
                    // Ở đây chúng ta chỉ cập nhật TextView trực tiếp cho đơn giản
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
                    if (isStartTime) {
                        tvStartTimeValue.setText(formattedTime);
                    } else {
                        tvEndTimeValue.setText(formattedTime);
                    }
                }, hour, minute, true); // true để dùng định dạng 24 giờ

        // Hiển thị dialog
        timePickerDialog.show();
    }

    /**
     * Cập nhật các TextView hiển thị ngày và giờ dựa trên biến selectedDateTime.
     */
    private void updateDateTimeViews() {
        // Định dạng ngày thành "Ngày dd/MM/yyyy"
        SimpleDateFormat dateFormat = new SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault());
        tvDateValue.setText(dateFormat.format(selectedDateTime.getTime()));

        // Định dạng giờ thành "HH:mm"
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        tvStartTimeValue.setText(timeFormat.format(selectedDateTime.getTime()));

        // Ví dụ: Mặc định giờ kết thúc sau giờ bắt đầu 1 tiếng
        Calendar endTimeCalendar = (Calendar) selectedDateTime.clone();
        endTimeCalendar.add(Calendar.HOUR_OF_DAY, 1);
        tvEndTimeValue.setText(timeFormat.format(endTimeCalendar.getTime()));
    }
}