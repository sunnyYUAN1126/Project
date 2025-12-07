package com.book.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    // Many Books to One Seller (User)
    @ManyToOne
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Column(name = "book_ISBN")
    private String isbn;

    @Column(name = "book_name", nullable = false)
    private String name;

    @Column(name = "category")
    private String category; // '文學類','社會科學類','商業管理類','理工資訊類','醫學健康類'

    @Column(name = "book_author")
    private String author;

    @Column(name = "book_publisher")
    private String publisher;

    @Column(name = "product_new")
    private String productNew; // '九成','八成'...

    @Column(name = "product_class_note")
    private String productClassNote; // '無','少量筆記'...

    @Column(name = "product_note", columnDefinition = "TEXT")
    private String productNote;

    @Column(name = "product_price", nullable = false)
    private Integer price;

    @Column(name = "product_stock")
    private Integer stock = 1;

    @Column(name = "shelf_status")
    private String shelfStatus = "下架"; // '上架','下架'

    @Column(name = "admin_review")
    private String adminReview = "待審核"; // '待審核','審核通過','審核不通過'

    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "book", fetch = FetchType.EAGER)
    private java.util.List<ProductImage> images;
}
