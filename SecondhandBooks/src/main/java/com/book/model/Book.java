package com.book.model;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

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

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getProductNew() {
        return productNew;
    }

    public void setProductNew(String productNew) {
        this.productNew = productNew;
    }

    public String getProductClassNote() {
        return productClassNote;
    }

    public void setProductClassNote(String productClassNote) {
        this.productClassNote = productClassNote;
    }

    public String getProductNote() {
        return productNote;
    }

    public void setProductNote(String productNote) {
        this.productNote = productNote;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public String getShelfStatus() {
        return shelfStatus;
    }

    public void setShelfStatus(String shelfStatus) {
        this.shelfStatus = shelfStatus;
    }

    public String getAdminReview() {
        return adminReview;
    }

    public void setAdminReview(String adminReview) {
        this.adminReview = adminReview;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public java.util.SortedSet<ProductImage> getImages() {
        return images;
    }

    public void setImages(java.util.SortedSet<ProductImage> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "Book{" +
                "productId=" + productId +
                ", sellerId=" + sellerId +
                ", isbn='" + isbn + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", productNew='" + productNew + '\'' +
                ", productClassNote='" + productClassNote + '\'' +
                ", productNote='" + productNote + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", shelfStatus='" + shelfStatus + '\'' +
                ", adminReview='" + adminReview + '\'' +
                ", adminNote='" + adminNote + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
