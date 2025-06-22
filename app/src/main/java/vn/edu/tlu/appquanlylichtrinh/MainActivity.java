package vn.edu.tlu.appquanlylichtrinh;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private EditText editName;
    private Button btnContinue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editName = findViewById(R.id.editName);
        btnContinue = findViewById(R.id.btnContinue);

        btnContinue.setOnClickListener(v -> {
            String name = editName.getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(MainActivity.this, "Vui lòng nhập tên!", Toast.LENGTH_SHORT).show();
            } else if (name.length() < 2) {
                Toast.makeText(MainActivity.this, "Tên phải có ít nhất 2 ký tự!", Toast.LENGTH_SHORT).show();
            } else {
                // Truyền tên sang LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.putExtra("USER_NAME", name);
                startActivity(intent);
            }
        });
    }
}