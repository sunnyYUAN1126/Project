package com.book.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookReviewDTO {
    private Long productId;
    private Long sellerId;
    private String sellerAccount; // Added field for account name
    private String isbn;
    private String name;
    private String author;
    private String publisher;
    private String category;
    private String productNew; // condition
    private String productClassNote; // notes
    private String productNote; // status description
    private Integer price;
    private LocalDateTime createdAt;
    private String adminNote;
    private List<String> images;

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

    public String getSellerAccount() {
        return sellerAccount;
    }

    public void setSellerAccount(String sellerAccount) {
        this.sellerAccount = sellerAccount;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getAdminNote() {
        return adminNote;
    }

    public void setAdminNote(String adminNote) {
        this.adminNote = adminNote;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "BookReviewDTO{" +
                "productId=" + productId +
                ", sellerId=" + sellerId +
                ", sellerAccount='" + sellerAccount + '\'' +
                ", isbn='" + isbn + '\'' +
                ", name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", publisher='" + publisher + '\'' +
                ", category='" + category + '\'' +
                ", productNew='" + productNew + '\'' +
                ", productClassNote='" + productClassNote + '\'' +
                ", productNote='" + productNote + '\'' +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", adminNote='" + adminNote + '\'' +
                ", images=" + images +
                '}';
    }
}
