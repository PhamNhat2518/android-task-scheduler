package vn.edu.tlu.appquanlylichtrinh.controller;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast; // Thêm Toast để kiểm tra
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import vn.edu.tlu.appquanlylichtrinh.R;
// Import AddTaskActivity từ đúng package
import vn.edu.tlu.appquanlylichtrinh.controller.AddTaskActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    // Phương thức xử lý sự kiện click trên tất cả các icon của Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Dùng if-else if thay vì nhiều if riêng lẻ sẽ tốt hơn
        if (id == android.R.id.home) {
            // Xử lý click cho icon menu bên trái
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;

        } else if (id == R.id.action_history) {
            // Xử lý click cho icon lịch sử bên phải
            Toast.makeText(this, "Lịch sử đã được nhấn", Toast.LENGTH_SHORT).show();
            return true;

        } else if (id == R.id.action_add) {
            // Xử lý click cho icon thêm bên phải
            Intent intent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}