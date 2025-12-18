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
public class UserController {

    @Autowired
    private UserService userService;

    // === 註冊 API ===
    // === 註冊 API ===
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @ModelAttribute RegisterRequest request,
            @RequestParam(value = "file", required = false) org.springframework.web.multipart.MultipartFile file) {

        String result = userService.register(request, file);
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

            if (user.getUserPicture() != null) {
                String base64Image = java.util.Base64.getEncoder().encodeToString(user.getUserPicture());
                response.put("userPicture", "data:image/jpeg;base64," + base64Image);
            }

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

    // === 會員管理 API (需權限控管) ===

    @GetMapping
    public ResponseEntity<java.util.List<Map<String, Object>>> getUsers(
            @RequestParam(name = "search", required = false) String search) {

        java.util.List<User> users = userService.searchUsers(search);

        // Convert to response map (handle binary image)
        java.util.List<Map<String, Object>> response = users.stream().map(u -> {
            Map<String, Object> map = new HashMap<>();
            map.put("user_id", u.getUserId());
            map.put("account", u.getAccount());
            map.put("password", u.getPassword()); // As requested, though usually unsafe
            map.put("student_id", u.getStudentId());
            map.put("department", u.getDepartment());
            map.put("role", u.getRole());
            map.put("created_at", u.getCreatedAt());
            map.put("updated_at", u.getUpdatedAt());

            if (u.getUserPicture() != null) {
                String base64 = java.util.Base64.getEncoder().encodeToString(u.getUserPicture());
                map.put("user_picture", "data:image/jpeg;base64," + base64);
            } else {
                map.put("user_picture", null);
            }
            return map;
        }).collect(java.util.stream.Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable Long id, @RequestBody User user) {
        String result = userService.updateUser(id, user);
        if ("更新成功".equals(result)) {
            return ResponseEntity.ok(Map.of("message", result));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", result));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        String result = userService.deleteUser(id);
        if ("刪除成功".equals(result)) {
            return ResponseEntity.ok(Map.of("message", result));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", result));
        }
    }
}