package com.book.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String account;
    private String password;
    private String studentId;
    private String department;
    // role 通常由後端預設為"會員"，不讓前端隨意傳
}