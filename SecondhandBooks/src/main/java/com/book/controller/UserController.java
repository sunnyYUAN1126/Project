package com.book.controller;

import com.book.dto.LoginRequest;
import com.book.dto.RegisterRequest;
import com.book.model.User;
import com.book.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // 允許前端跨域呼叫 (開發階段方便)
public class UserController {

    @Autowired
    private UserService userService;

    // === 註冊 API ===
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        String result = userService.register(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", result);
        
        if ("註冊成功".equals(result)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // === 登入 API ===
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest request, HttpSession session) {
        User user = userService.login(request);

        if (user != null) {
            // 登入成功，將 user_id 存入 Session
            session.setAttribute("user_id", user.getUserId());
            session.setAttribute("role", user.getRole());
            
            // 回傳給前端的資料 (不包含密碼)
            Map<String, Object> response = new HashMap<>();
            response.put("message", "登入成功");
            response.put("userId", user.getUserId());
            response.put("account", user.getAccount());
            response.put("role", user.getRole());
            
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "帳號或密碼錯誤"));
        }
    }

    // === 登出 API ===
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpSession session) {
        session.invalidate(); // 清除 Session
        return ResponseEntity.ok(Map.of("message", "登出成功"));
    }

    // === 測試目前登入狀態 API (選用) ===
    @GetMapping("/current")
    public ResponseEntity<Object> getCurrentUser(HttpSession session) {
        Object userId = session.getAttribute("user_id");
        if (userId != null) {
            return ResponseEntity.ok(Map.of("userId", userId, "role", session.getAttribute("role")));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "未登入"));
        }
    }
}