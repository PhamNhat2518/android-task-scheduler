package vn.edu.tlu.appquanlylichtrinh.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // << THÊM DÒNG NÀY
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
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
import java.util.stream.Collectors;

import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.model.Task;
import vn.edu.tlu.appquanlylichtrinh.controller.ScheduleAdapter;

public class MainActivity extends AppCompatActivity {

    private enum FilterMode {
        DEFAULT_WEEK,
        OVERDUE,
        INCOMPLETE,
        COMPLETE
    }

    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<Object> displayList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FilterMode currentFilter = FilterMode.DEFAULT_WEEK;
    private List<Task> allTasksCache = new ArrayList<>();

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
            fetchInitialTasks();
        }
    }

    private void fetchInitialTasks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                allTasksCache.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null) {
                        allTasksCache.add(task);
                    }
                }
                applyCurrentFilter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void applyCurrentFilter() {
        displayList.clear();
        List<Task> filteredTasks;

        switch (currentFilter) {
            case OVERDUE:
                filteredTasks = filterOverdueTasks(allTasksCache);
                buildDisplayListForFilteredTasks(filteredTasks, "Công việc quá hạn");
                break;
            case INCOMPLETE:
                filteredTasks = filterIncompleteTasks(allTasksCache);
                buildDisplayListForFilteredTasks(filteredTasks, "Công việc chưa hoàn tất");
                break;
            case COMPLETE:
                filteredTasks = filterCompletedTasks(allTasksCache);
                buildDisplayListForFilteredTasks(filteredTasks, "Công việc đã hoàn tất");
                break;
            case DEFAULT_WEEK:
            default:
                List<Date> nextSevenDays = getNextSevenDays();
                List<Task> weekTasks = filterTasksForNext7Days(allTasksCache, nextSevenDays);
                buildDisplayListForWeek(nextSevenDays, groupTasksByDate(weekTasks));
                break;
        }
        scheduleAdapter.notifyDataSetChanged();
    }

    // --- CÁC HÀM LỌC ĐÃ SỬA LỖI VÀ TỐI ƯU ---
    private List<Task> filterOverdueTasks(List<Task> tasks) {
        Date now = new Date();
        return tasks.stream()
                .filter(task -> {
                    if (task.isCompleted()) return false;
                    Date taskEndDate = getTaskEndDateTime(task);
                    // Chỉ xem xét công việc quá hạn nếu có ngày hợp lệ (khác với ngày 0)
                    return taskEndDate.getTime() != 0 && taskEndDate.before(now);
                })
                .collect(Collectors.toList());
    }

    private List<Task> filterIncompleteTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> !task.isCompleted())
                .collect(Collectors.toList());
    }

    private List<Task> filterCompletedTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(Task::isCompleted)
                .collect(Collectors.toList());
    }

    // *** PHƯƠNG THỨC ĐÃ SỬA LỖI NullPointerException ***
    private List<Task> filterTasksForNext7Days(List<Task> allTasks, List<Date> weekDays) {
        SimpleDateFormat keyFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        List<String> weekDateKeys = weekDays.stream().map(keyFormat::format).collect(Collectors.toList());

        return allTasks.stream()
                .filter(task -> {
                    // Lấy chuỗi ngày ra một biến riêng để kiểm tra
                    String dateString = task.getDate();

                    // KIỂM TRA NULL HOẶC RỖNG TRƯỚC KHI PARSE
                    if (dateString == null || dateString.isEmpty()) {
                        return false; // Bỏ qua công việc này nếu không có ngày
                    }

                    // Nếu chuỗi ngày hợp lệ, mới tiến hành parse
                    try {
                        Date taskDate = firebaseDateFormat.parse(dateString);
                        String taskDateKey = keyFormat.format(taskDate);
                        return weekDateKeys.contains(taskDateKey);
                    } catch (ParseException e) {
                        Log.e("MainActivity", "Lỗi định dạng ngày không hợp lệ: " + dateString, e);
                        return false; // Bỏ qua nếu định dạng ngày sai
                    }
                })
                .collect(Collectors.toList());
    }


    // --- CÁC HÀM TIỆN ÍCH ĐÃ SỬA LỖI VÀ TỐI ƯU ---

    // *** PHƯƠNG THỨC ĐƯỢC LÀM CHO AN TOÀN HƠN VỚI NULL ***
    private Date getTaskEndDateTime(Task task) {
        String dateStr = task.getDate();
        String timeStr = task.getEndTime();

        // Kiểm tra null hoặc rỗng cho cả ngày và giờ
        if (dateStr == null || dateStr.isEmpty() || timeStr == null || timeStr.isEmpty()) {
            // Trả về một ngày không hợp lệ (ngày 0) để dễ dàng kiểm tra ở nơi gọi
            return new Date(0);
        }

        try {
            String dateTimeString = dateStr.replace("Ngày ", "") + " " + timeStr;
            SimpleDateFormat combinedFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return combinedFormat.parse(dateTimeString);
        } catch (ParseException e) {
            Log.e("MainActivity", "Lỗi parse ngày giờ: " + dateStr + " " + timeStr, e);
            return new Date(0); // Trả về giá trị mặc định nếu có lỗi
        }
    }

    private Map<String, List<Task>> groupTasksByDate(List<Task> tasks) {
        Map<String, List<Task>> groupedTasks = new LinkedHashMap<>();
        for (Task task : tasks) {
            String dateString = task.getDate();
            if (dateString != null && !dateString.isEmpty()) { // Kiểm tra null ở đây
                try {
                    Date taskDate = firebaseDateFormat.parse(dateString);
                    String dateKey = firebaseDateFormat.format(taskDate); // Dùng lại định dạng chuẩn
                    if (!groupedTasks.containsKey(dateKey)) {
                        groupedTasks.put(dateKey, new ArrayList<>());
                    }
                    groupedTasks.get(dateKey).add(task);
                } catch (ParseException e) {
                    Log.e("MainActivity", "Lỗi parse ngày khi nhóm công việc: " + dateString, e);
                }
            }
        }
        return groupedTasks;
    }

    private void buildDisplayListForFilteredTasks(List<Task> filteredTasks, String header) {
        displayList.clear();
        if (!filteredTasks.isEmpty()) {
            displayList.add(header);
            displayList.addAll(filteredTasks);
        } else {
            displayList.add("Không có công việc nào cho mục '" + header + "'");
        }
    }

    private void buildDisplayListForWeek(List<Date> weekDays, Map<String, List<Task>> tasksByDate) {
        displayList.clear();
        for (Date day : weekDays) {
            // Tạo chuỗi header cho ngày
            String dayHeader = dayOfWeekFormat.format(day) + ", " + dateMonthFormat.format(day);
            displayList.add(dayHeader);

            String dateKey = firebaseDateFormat.format(day); // Dùng định dạng chuẩn để lấy key
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

    private void showFilterPopupMenu() {
        View menuItemView = findViewById(R.id.action_history);
        if (menuItemView == null) menuItemView = findViewById(R.id.toolbar);
        if (menuItemView == null) return;

        PopupMenu popup = new PopupMenu(this, menuItemView);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());

        switch (currentFilter) {
            case OVERDUE:
                popup.getMenu().findItem(R.id.filter_overdue).setChecked(true);
                break;
            case INCOMPLETE:
                popup.getMenu().findItem(R.id.filter_incomplete).setChecked(true);
                break;
            case COMPLETE:
                popup.getMenu().findItem(R.id.filter_complete).setChecked(true);
                break;
            default:
                popup.getMenu().findItem(R.id.filter_as_list).setChecked(true);
                break;
        }

        popup.setOnMenuItemClickListener(menuItem -> {
            menuItem.setChecked(true);
            int itemId = menuItem.getItemId();
            if (itemId == R.id.filter_overdue) {
                currentFilter = FilterMode.OVERDUE;
            } else if (itemId == R.id.filter_incomplete) {
                currentFilter = FilterMode.INCOMPLETE;
            } else if (itemId == R.id.filter_complete) {
                currentFilter = FilterMode.COMPLETE;
            } else if (itemId == R.id.filter_as_list) {
                currentFilter = FilterMode.DEFAULT_WEEK;
            }
            applyCurrentFilter();
            return true;
        });

        popup.show();
    }
}