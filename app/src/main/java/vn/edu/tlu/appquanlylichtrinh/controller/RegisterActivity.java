package vn.edu.tlu.appquanlylichtrinh.controller;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import vn.edu.tlu.appquanlylichtrinh.R;

public class RegisterActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Kết nối file Java này với file layout activity_register.xml
        setContentView(R.layout.activity_register);
    }
}
