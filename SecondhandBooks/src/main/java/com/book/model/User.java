package com.book.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table("users")
public class User {

    @Id
    @Column("user_id")
    private Long userId;

    @Column("account")
    private String account;

    @Column("password")
    private String password;

    @Column("student_id")
    private String studentId;

    @Column("department")
    private String department;

    @Column("role")
    private String role = "會員";

    @Column("user_picture")
    private byte[] userPicture;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}