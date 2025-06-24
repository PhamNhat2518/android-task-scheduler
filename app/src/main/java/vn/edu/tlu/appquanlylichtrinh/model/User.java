package vn.edu.tlu.appquanlylichtrinh.model;

public class User {
    public String email;
    // Bạn có thể thêm các trường khác ở đây, ví dụ: public String name;

    // Constructor rỗng là BẮT BUỘC để Firebase có thể đọc dữ liệu
    public User() {
    }

    public User(String email) {
        this.email = email;
    }
}