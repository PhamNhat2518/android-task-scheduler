package vn.edu.tlu.appquanlylichtrinh.controller;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends AppCompatActivity {

    // Enum để quản lý các chế độ lọc
    private enum FilterMode {
        DEFAULT_WEEK, // Chế độ mặc định, hiển thị 7 ngày tới
        OVERDUE,      // Quá hạn
        INCOMPLETE,   // Chưa hoàn thành
        COMPLETE      // Đã hoàn thành
    }

    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<Object> displayList;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    // Biến lưu trữ chế độ lọc hiện tại, mặc định là xem theo tuần
    private FilterMode currentFilter = FilterMode.DEFAULT_WEEK;
    // Cache để lưu tất cả công việc, tránh phải truy vấn Firebase liên tục
    private List<Task> allTasksCache = new ArrayList<>();

    // Các định dạng ngày tháng được sử dụng trong ứng dụng
    private final SimpleDateFormat firebaseDateFormat = new SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault());
    private final SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("EEEE", new Locale("vi", "VN"));
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
            // Bắt đầu lắng nghe sự thay đổi dữ liệu từ Firebase
            fetchAndListenForTasks();
        } else {
            // Xử lý trường hợp người dùng chưa đăng nhập
            // Ví dụ: chuyển đến màn hình đăng nhập
            // startActivity(new Intent(MainActivity.this, LoginActivity.class));
            // finish();
        }
    }

    /**
     * Lắng nghe dữ liệu từ Firebase.
     * Mỗi khi dữ liệu trên Firebase thay đổi, onDataChange sẽ được gọi.
     * Dữ liệu sẽ được lưu vào `allTasksCache` và sau đó bộ lọc hiện tại sẽ được áp dụng.
     */
    private void fetchAndListenForTasks() {
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
                // Sau khi có dữ liệu mới, áp dụng lại bộ lọc hiện tại để cập nhật UI
                applyCurrentFilter();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Lỗi tải dữ liệu", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "Firebase Database Error: ", databaseError.toException());
            }
        });
    }

    /**
     * Trung tâm điều phối việc lọc và hiển thị dữ liệu.
     * Dựa vào `currentFilter`, hàm này sẽ gọi các hàm lọc và hàm xây dựng danh sách hiển thị tương ứng.
     */
    private void applyCurrentFilter() {
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


    // --- CÁC HÀM LỌC DỮ LIỆU TỪ CACHE ---

    private List<Task> filterOverdueTasks(List<Task> tasks) {
        Date now = new Date();
        return tasks.stream()
                .filter(task -> {
                    if (task.isCompleted()) return false;
                    Date taskEndDate = getTaskEndDateTime(task);
                    // Chỉ coi là quá hạn nếu có ngày/giờ hợp lệ và thời gian đó đã qua
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

    private List<Task> filterTasksForNext7Days(List<Task> allTasks, List<Date> weekDays) {
        // Dùng định dạng chỉ có ngày/tháng/năm để so sánh
        SimpleDateFormat comparisonFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        List<String> weekDateKeys = weekDays.stream().map(comparisonFormat::format).collect(Collectors.toList());

        return allTasks.stream()
                .filter(task -> {
                    String dateString = task.getDate();
                    // KIỂM TRA NULL HOẶC RỖNG TRƯỚC KHI PARSE để tránh crash
                    if (dateString == null || dateString.isEmpty()) {
                        return false;
                    }
                    try {
                        // Parse ngày của công việc từ định dạng trên Firebase
                        Date taskDate = firebaseDateFormat.parse(dateString);
                        // Chuyển về định dạng so sánh
                        String taskDateKey = comparisonFormat.format(taskDate);
                        // Kiểm tra xem ngày của công việc có nằm trong 7 ngày tới không
                        return weekDateKeys.contains(taskDateKey);
                    } catch (ParseException e) {
                        Log.e("MainActivity", "Lỗi định dạng ngày không hợp lệ: " + dateString, e);
                        return false;
                    }
                })
                .collect(Collectors.toList());
    }


    // --- CÁC HÀM TIỆN ÍCH VÀ XÂY DỰNG DANH SÁCH HIỂN THỊ ---

    /**
     * Chuyển đổi ngày và giờ kết thúc của Task thành đối tượng Date.
     * An toàn với giá trị null hoặc rỗng.
     * @return Đối tượng Date, hoặc new Date(0) nếu ngày/giờ không hợp lệ.
     */
    private Date getTaskEndDateTime(Task task) {
        String dateStr = task.getDate();
        String timeStr = task.getEndTime();

        // Kiểm tra null hoặc rỗng để tránh crash
        if (dateStr == null || dateStr.isEmpty() || timeStr == null || timeStr.isEmpty()) {
            return new Date(0); // Trả về một ngày không hợp lệ để dễ kiểm tra
        }
        try {
            // Ghép chuỗi ngày và giờ lại để parse
            String dateTimeString = dateStr.replace("Ngày ", "") + " " + timeStr;
            SimpleDateFormat combinedFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            return combinedFormat.parse(dateTimeString);
        } catch (ParseException e) {
            Log.e("MainActivity", "Lỗi parse ngày giờ: " + dateStr + " " + timeStr, e);
            return new Date(0); // Trả về giá trị mặc định nếu có lỗi
        }
    }

    private Map<String, List<Task>> groupTasksByDate(List<Task> tasks) {
        // Dùng LinkedHashMap để giữ nguyên thứ tự các ngày
        Map<String, List<Task>> groupedTasks = new LinkedHashMap<>();
        for (Task task : tasks) {
            String dateKey = task.getDate();
            // Kiểm tra null/rỗng trước khi sử dụng
            if (dateKey != null && !dateKey.isEmpty()) {
                if (!groupedTasks.containsKey(dateKey)) {
                    groupedTasks.put(dateKey, new ArrayList<>());
                }
                groupedTasks.get(dateKey).add(task);
            }
        }
        return groupedTasks;
    }

    /**
     * Xây dựng danh sách hiển thị cho các chế độ lọc (Quá hạn, Chưa xong, Đã xong).
     * Bao gồm một tiêu đề và danh sách các công việc.
     */
    private void buildDisplayListForFilteredTasks(List<Task> filteredTasks, String header) {
        displayList.clear();
        if (!filteredTasks.isEmpty()) {
            displayList.add(header); // Thêm tiêu đề
            displayList.addAll(filteredTasks);
        } else {
            displayList.add("Không có công việc nào trong mục '" + header + "'");
        }
    }

    /**
     * Xây dựng danh sách hiển thị cho chế độ xem theo tuần (mặc định).
     * Hiển thị lần lượt các ngày, theo sau là công việc của ngày đó.
     */
    private void buildDisplayListForWeek(List<Date> weekDays, Map<String, List<Task>> tasksByDate) {
        displayList.clear();
        for (Date day : weekDays) {
            // Tạo tiêu đề cho ngày (VD: "Thứ Hai, 15 tháng 7")
            String dayHeader = dayOfWeekFormat.format(day) + ", " + dateMonthFormat.format(day);
            displayList.add(dayHeader);

            // Lấy key theo đúng định dạng lưu trên Firebase để tìm trong Map
            String dateKey = firebaseDateFormat.format(day);
            List<Task> tasksForDay = tasksByDate.get(dateKey);

            if (tasksForDay != null && !tasksForDay.isEmpty()) {
                displayList.addAll(tasksForDay);
            }
        }
    }

    private List<Date> getNextSevenDays() {
        List<Date> dates = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        return dates;
    }


    // --- XỬ LÝ MENU TRÊN TOOLBAR ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Xử lý icon menu bên trái (mở Settings)
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_history) {
            // Hiển thị menu Lọc
            showFilterPopupMenu();
            return true;
        } else if (id == R.id.action_add) {
            // Mở màn hình thêm công việc
            startActivity(new Intent(MainActivity.this, AddTaskActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterPopupMenu() {
        View menuItemView = findViewById(R.id.action_history);
        if (menuItemView == null) return; // Đảm bảo view tồn tại

        PopupMenu popup = new PopupMenu(this, menuItemView);
        popup.getMenuInflater().inflate(R.menu.filter_menu, popup.getMenu());

        // Đánh dấu checked cho mục đang được chọn
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
            case DEFAULT_WEEK:
                popup.getMenu().findItem(R.id.filter_as_list).setChecked(true);
                break;
        }

        // Xử lý sự kiện khi một mục trong menu được chọn
        popup.setOnMenuItemClickListener(menuItem -> {
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

            // Sau khi thay đổi chế độ lọc, gọi hàm để áp dụng và cập nhật UI
            applyCurrentFilter();
            return true;
        });

        popup.show();
    }
}