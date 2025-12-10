package com.book.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Table("products")
public class Book {

    @Id
    @Column("product_id")
    private Long productId;

    // JDBC: Reference by ID
    @Column("seller_id")
    private Long sellerId;

    @Column("book_ISBN")
    private String isbn;

    @Column("book_name")
    private String name;

    @Column("category")
    private String category;

    @Column("book_author")
    private String author;

    @Column("book_publisher")
    private String publisher;

    @Column("product_new")
    private String productNew;

    @Column("product_class_note")
    private String productClassNote;

    @Column("product_note")
    private String productNote;

    @Column("product_price")
    private Integer price;

    @Column("product_stock")
    private Integer stock = 1;

    @Column("shelf_status")
    private String shelfStatus = "下架";

    @Column("admin_review")
    private String adminReview = "待審核";

    @Column("admin_note")
    private String adminNote;

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;

    // JDBC One-to-Many
    // idColumn is the Foreign Key in product_images table pointing to Book
    // We use SortedSet to manually control the order via Comparable implementation
    // in ProductImage
    // and storing explicit values in 'sort_order' column.
    @MappedCollection(idColumn = "product_id")
    private java.util.SortedSet<ProductImage> images;
}
