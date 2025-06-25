package vn.edu.tlu.appquanlylichtrinh.controller;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import vn.edu.tlu.appquanlylichtrinh.R;

public class PomodoroActivity extends AppCompatActivity {

    private TextView tvTimer, tvMode, tvCancel;
    private Button btnStartPause, btnReset;
    private ProgressBar progressBar;

    private CountDownTimer countDownTimer;
    private boolean isRunning = false;
    private boolean isWorkMode = true;

    private long timeLeftInMillis;
    private long totalTimeInMillis;

    private final long WORK_DURATION = 1 * 60 * 1000;  // 25 phút
    private final long BREAK_DURATION = 5 * 60 * 1000;  // 5 phút

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pomodoro);

        tvTimer = findViewById(R.id.tvTimer);
        tvMode = findViewById(R.id.tvMode);
        btnStartPause = findViewById(R.id.btnStartPause);
        btnReset = findViewById(R.id.btnReset);
        progressBar = findViewById(R.id.progressBar);
        tvCancel = findViewById(R.id.tvCancel); // Nút hủy bỏ

        startNewSession(true); // bắt đầu ở chế độ làm việc

        btnStartPause.setOnClickListener(v -> {
            if (isRunning) pauseTimer();
            else startTimer();
        });

        btnReset.setOnClickListener(v -> resetTimer());

        tvCancel.setOnClickListener(v -> {
            // Trở về MainActivity
            Intent intent = new Intent(PomodoroActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    private void startNewSession(boolean workMode) {
        isWorkMode = workMode;
        totalTimeInMillis = isWorkMode ? WORK_DURATION : BREAK_DURATION;
        timeLeftInMillis = totalTimeInMillis;
        tvMode.setText(isWorkMode ? "Pomodoro: Làm việc" : "Pomodoro: Nghỉ ngơi");
        updateTimerUI();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerUI();
            }

            public void onFinish() {
                isRunning = false;

                // Phát âm thanh và thông báo
                playAlarm();
                String message = isWorkMode ? "✅ Hết phiên làm việc! Đến giờ nghỉ ngơi." : "☕ Hết phiên nghỉ! Quay lại làm việc.";
                Toast.makeText(PomodoroActivity.this, message, Toast.LENGTH_LONG).show();

                // Tự động chuyển sang phiên tiếp theo
                startNewSession(!isWorkMode);
                updateTimerUI();
                btnStartPause.setText("Bắt đầu");
            }

        }.start();

        isRunning = true;
        btnStartPause.setText("Tạm dừng");
    }

    private void pauseTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isRunning = false;
        btnStartPause.setText("Tiếp tục");
    }

    private void resetTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        isRunning = false;
        timeLeftInMillis = totalTimeInMillis;
        updateTimerUI();
        btnStartPause.setText("Bắt đầu");
    }

    private void updateTimerUI() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        tvTimer.setText(timeFormatted);

        int progress = (int) (100 * timeLeftInMillis / totalTimeInMillis);
        progressBar.setProgress(progress);
        tvMode.setText(isWorkMode ? "Pomodoro: Làm việc" : "Pomodoro: Nghỉ ngơi");
    }

    private void playAlarm() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound); // Đặt âm thanh ở res/raw/alarm_sound.mp3

        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.stop();
                mp.release(); // Giải phóng tài nguyên
            });
            mediaPlayer.start();
        }
    }
}
