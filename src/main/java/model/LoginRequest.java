package model;

public class LoginRequest {
    private String phone;
    private String password;

    // Default constructor required by Jackson
    public LoginRequest() {
    }

    // Constructor with parameters
    public LoginRequest(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }

    // Getters and setters
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
