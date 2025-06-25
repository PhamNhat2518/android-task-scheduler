package vn.edu.tlu.appquanlylichtrinh.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import androidx.appcompat.widget.PopupMenu;
import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.model.Task;
import vn.edu.tlu.appquanlylichtrinh.controller.ScheduleAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<Object> displayList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    // Định dạng ngày mà bạn lưu trên Firebase
    private final SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault());
    // Định dạng để hiển thị Thứ trong tuần
    private final SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
    // Định dạng để hiển thị ngày/tháng
    private final SimpleDateFormat dateMonthFormat = new SimpleDateFormat("d 'tháng' M", new Locale("vi", "VN"));


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        displayList = new ArrayList<>();
        scheduleAdapter = new ScheduleAdapter(displayList, getSupportFragmentManager());
        recyclerView.setAdapter(scheduleAdapter);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("tasks").child(userId);
            fetchAndDisplayWeekSchedule(); // Gọi hàm mới
        }
    }

    /**
     * Hàm chính để lấy và hiển thị lịch trình cho 7 ngày tới.
     */
    private void fetchAndDisplayWeekSchedule() {
        // Lấy danh sách 7 ngày tới
        List<Date> nextSevenDays = getNextSevenDays();

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Nhóm tất cả các task lấy từ Firebase theo ngày
                Map<String, List<Task>> allTasksByDate = groupAllTasks(dataSnapshot);

                // Xây dựng danh sách hiển thị cuối cùng
                buildDisplayListForWeek(nextSevenDays, allTasksByDate);

                scheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Nhóm tất cả các task từ Firebase vào một Map với key là ngày (dd/MM/yyyy).
     */
    private Map<String, List<Task>> groupAllTasks(DataSnapshot dataSnapshot) {
        Map<String, List<Task>> groupedTasks = new LinkedHashMap<>();
        SimpleDateFormat keyFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            Task task = snapshot.getValue(Task.class);
            if (task != null && task.getDate() != null) {
                try {
                    // Chuyển đổi chuỗi ngày từ Firebase sang đối tượng Date
                    Date taskDate = firebaseDateFormat.parse(task.getDate());
                    // Tạo một key chuẩn (dd/MM/yyyy) để nhóm
                    String dateKey = keyFormat.format(taskDate);

                    if (!groupedTasks.containsKey(dateKey)) {
                        groupedTasks.put(dateKey, new ArrayList<>());
                    }
                    groupedTasks.get(dateKey).add(task);
                } catch (ParseException e) {
                    e.printStackTrace(); // Lỗi nếu định dạng ngày trên Firebase bị sai
                }
            }
        }
        return groupedTasks;
    }

    /**
     * Xây dựng danh sách hiển thị cuối cùng, bao gồm cả những ngày không có task.
     */
    private void buildDisplayListForWeek(List<Date> weekDays, Map<String, List<Task>> tasksByDate) {
        displayList.clear();
        SimpleDateFormat keyFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Date day : weekDays) {
            // Tạo chuỗi header cho ngày
            String dayHeader = dayOfWeekFormat.format(day) + ", " + dateMonthFormat.format(day);
            displayList.add(dayHeader); // Thêm header vào danh sách hiển thị

            // Tìm các task tương ứng với ngày này
            String dateKey = keyFormat.format(day);
            List<Task> tasksForDay = tasksByDate.get(dateKey);

            if (tasksForDay != null && !tasksForDay.isEmpty()) {
                // Nếu có task, thêm chúng vào danh sách hiển thị
                displayList.addAll(tasksForDay);
            }
            // Nếu không có task, không cần làm gì thêm, chỉ có header ngày được hiển thị.
        }
    }


    /**
     * Tạo ra một danh sách 7 đối tượng Date, bắt đầu từ hôm nay.
     */
    private List<Date> getNextSevenDays() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }
    // --- PHẦN BỊ THIẾU ĐÃ ĐƯỢC THÊM LẠI Ở ĐÂY ---

    /**
     * Phương thức này được gọi để tạo các icon bên phải trên Toolbar.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    /**
     * Phương thức này xử lý sự kiện click trên tất cả các icon của Toolbar.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Xử lý click cho icon menu bên trái (mở Settings)
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.action_history) {
            // Khi nhấn nút lịch sử, gọi hàm hiển thị popup menu
            showFilterPopupMenu();
            return true;

        } else if (id == R.id.action_add) {
            // Xử lý click cho icon thêm bên phải (mở AddTask)
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);


    }
    /**
     * Hàm để hiển thị PopupMenu khi nhấn vào icon lịch sử.
     */
    private void showFilterPopupMenu() {
        // Tìm View của icon lịch sử để PopupMenu hiện ra đúng vị trí
        View menuItemView = findViewById(R.id.action_history);
        if (menuItemView == null) {
            // Dự phòng nếu không tìm thấy view (ví dụ trên các thiết bị cũ)
            // Lấy view của toolbar làm vị trí neo
            menuItemView = findViewById(R.id.toolbar);
        }

        PopupMenu popup = new PopupMenu(this, menuItemView);
        // "Thổi phồng" file menu của chúng ta vào popup
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());

        // --- Logic để hiển thị dấu tick ---
        // Lấy mục menu "Danh sách"
        MenuItem listMenuItem = popup.getMenu().findItem(R.id.filter_as_list);
        // Đặt icon dấu tick nếu nó đang được chọn
        if (listMenuItem.isChecked()) {
            listMenuItem.setIcon(R.drawable.ic_check_24);
        } else {
            listMenuItem.setIcon(null);
        }

        // Đặt sự kiện lắng nghe khi một mục trong popup được chọn
        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();

            if (itemId == R.id.filter_overdue || itemId == R.id.filter_incomplete || itemId == R.id.filter_complete) {
                // Xử lý cho nhóm radio button
                menuItem.setChecked(true); // Đánh dấu mục được chọn
                Toast.makeText(this, "Đã chọn: " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                // TODO: Thêm logic lọc danh sách ở đây
                return true;
            } else if (itemId == R.id.filter_as_list) {
                // Xử lý cho mục "Danh sách"
                menuItem.setChecked(!menuItem.isChecked()); // Đảo trạng thái tick
                if (menuItem.isChecked()) {
                    menuItem.setIcon(R.drawable.ic_check_24);
                } else {
                    menuItem.setIcon(null);
                }
                // Giữ cho popup không bị đóng lại ngay lập tức để người dùng thấy sự thay đổi
                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
                menuItem.setActionView(new View(getApplicationContext()));
                menuItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return false;
                    }
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        return false;
                    }
                });
                return false; // Trả về false để menu không đóng lại
            }
            return false;
        });

        // Hiển thị popup menu
        popup.show();
    }
}