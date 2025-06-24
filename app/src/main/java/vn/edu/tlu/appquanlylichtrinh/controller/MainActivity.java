package vn.edu.tlu.appquanlylichtrinh.controller;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.model.Task;
import vn.edu.tlu.appquanlylichtrinh.controller.ScheduleAdapter; // Import Adapter mới

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ScheduleAdapter scheduleAdapter;
    private List<Object> displayList; // Danh sách chứa cả Header (String) và Task
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

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
        scheduleAdapter = new ScheduleAdapter(displayList);
        recyclerView.setAdapter(scheduleAdapter);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("tasks").child(userId);
            fetchAndGroupTasks();
        }
    }

    private void fetchAndGroupTasks() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Dùng LinkedHashMap để giữ thứ tự các ngày
                Map<String, List<Task>> groupedTasks = new LinkedHashMap<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Task task = snapshot.getValue(Task.class);
                    if (task != null && task.getDate() != null) {
                        String date = task.getDate(); // Lấy ngày từ task
                        // Nếu ngày này chưa có trong map, tạo một danh sách mới
                        if (!groupedTasks.containsKey(date)) {
                            groupedTasks.put(date, new ArrayList<>());
                        }
                        // Thêm task vào danh sách của ngày đó
                        groupedTasks.get(date).add(task);
                    }
                }

                // Chuyển đổi Map đã nhóm thành một danh sách phẳng để hiển thị
                displayList.clear();
                for (Map.Entry<String, List<Task>> entry : groupedTasks.entrySet()) {
                    displayList.add(entry.getKey()); // Thêm Header ngày
                    displayList.addAll(entry.getValue()); // Thêm tất cả các task của ngày đó
                }

                scheduleAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi
            }
        });
    }

    // ... (Các phương thức menu không đổi)
}