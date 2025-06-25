// Tạo file mới: model/DaySchedule.java
package vn.edu.tlu.appquanlylichtrinh.model;

import java.util.Date;
import java.util.List;

public class DaySchedule {
    private Date date;
    private List<Task> tasks;

    public DaySchedule(Date date, List<Task> tasks) {
        this.date = date;
        this.tasks = tasks;
    }

    public Date getDate() {
        return date;
    }

    public List<Task> getTasks() {
        return tasks;
    }
}