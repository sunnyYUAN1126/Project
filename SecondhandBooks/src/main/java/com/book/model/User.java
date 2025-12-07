package com.book.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, unique = true)
    private String account;

    @Column(nullable = false)
    private String password;

    @Column(name = "student_id")
    private String studentId;

    private String department;

    // 對應 DB ENUM('會員','管理員')
    // 注意：資料庫存的是中文，這裡用 String 簡單對應，或者自訂 Enum
    @Column(columnDefinition = "ENUM('會員','管理員')")
    private String role = "會員"; // 預設值

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}