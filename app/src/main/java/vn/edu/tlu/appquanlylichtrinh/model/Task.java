
package vn.edu.tlu.appquanlylichtrinh.model;
import java.io.Serializable;

public class Task implements Serializable {
    private String taskId;
    private String title;
    private String subtask;
    private String date;
    private String startTime;
    private String endTime;
    private String userId; // Để biết tác vụ này của ai
    private boolean isCompleted;

    public boolean isSubtaskCompleted() {
        return subtaskCompleted;
    }

    public void setSubtaskCompleted(boolean subtaskCompleted) {
        this.subtaskCompleted = subtaskCompleted;
    }

    private boolean subtaskCompleted;

    // Constructor rỗng là BẮT BUỘC cho Firebase
    public Task() {
    }

    public Task(String taskId, String title, String subtask, String date, String startTime, String endTime, String userId) {
        this.taskId = taskId;
        this.title = title;
        this.subtask = subtask;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userId = userId;
        this.isCompleted = false; // Mặc định là chưa hoàn thành
    }

    // Getters và Setters (Bấm Alt+Insert để tự động tạo)
    public String getTaskId() { return taskId; }
    public void setTaskId(String taskId) { this.taskId = taskId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSubtask() { return subtask; }
    public void setSubtask(String subtask) { this.subtask = subtask; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }
    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public boolean isCompleted() { return isCompleted; }
    public void setCompleted(boolean completed) { isCompleted = completed; }
}