package vn.edu.tlu.appquanlylichtrinh.controller; // <-- Thay bằng package của bạn

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import vn.edu.tlu.appquanlylichtrinh.R;

public class Activity2 extends AppCompatActivity {

    private MaterialButton btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity2); // Liên kết với activity2.xml

        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNextActivity();
            }
        });
    }

    private void goToNextActivity() {
        // Tạo Intent để mở Activity3
        Intent intent = new Intent(Activity2.this, Activity3.class);
        startActivity(intent);
    }
}