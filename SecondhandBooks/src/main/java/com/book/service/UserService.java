package com.book.service;

import com.book.dto.LoginRequest;
import com.book.dto.RegisterRequest;
import com.book.model.User;
import com.book.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 使用者註冊
     */
    public String register(RegisterRequest request) {
        // 1. 檢查帳號是否重複
        if (userRepository.existsByAccount(request.getAccount())) {
            return "帳號已存在";
        }

        // 2. 建立新使用者物件
        User user = new User();
        user.setAccount(request.getAccount());
        
        // 注意：專題演示為方便直接存明碼，實務上必須加密 (如 BCrypt)
        user.setPassword(request.getPassword()); 
        
        user.setStudentId(request.getStudentId());
        user.setDepartment(request.getDepartment());
        user.setRole("會員"); // 預設角色

        userRepository.save(user);
        return "註冊成功";
    }

    /**
     * 使用者登入
     */
    public User login(LoginRequest request) {
        // 1. 找尋使用者
        Optional<User> userOptional = userRepository.findByAccount(request.getAccount());

        // 2. 驗證密碼 (這裡直接比對明碼，配合你提供的 SQL Dump 資料)
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(request.getPassword())) {
                return user; // 登入成功，回傳使用者資訊
            }
        }
        return null; // 登入失敗
    }
}