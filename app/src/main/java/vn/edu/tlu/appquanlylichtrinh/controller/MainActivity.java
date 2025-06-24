package vn.edu.tlu.appquanlylichtrinh.controller; // Hoặc package controller của bạn

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu; // <-- QUAN TRỌNG: Thêm import này
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import vn.edu.tlu.appquanlylichtrinh.R;
import vn.edu.tlu.appquanlylichtrinh.controller.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    // --- BƯỚC QUAN TRỌNG: Phương thức này tạo ra các icon bên phải trên Toolbar ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }


    // Phương thức xử lý sự kiện click trên tất cả các icon của Toolbar
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Xử lý click cho icon menu bên trái
        if (id == android.R.id.home) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        // Xử lý click cho icon lịch sử bên phải
        if (id == R.id.action_history) {
            // TODO: Code xử lý khi nhấn nút lịch sử
            return true;
        }

        // Xử lý click cho icon thêm bên phải
        if (id == R.id.action_add) {
            // TODO: Code xử lý khi nhấn nút thêm
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}