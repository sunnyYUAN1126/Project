package com.book.dto;

public class RegisterRequest {
    private String account;
    private String password;
    private String studentId;
    private String department;
    // role 通常由後端預設為"會員"，不讓前端隨意傳

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", studentId='" + studentId + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}
