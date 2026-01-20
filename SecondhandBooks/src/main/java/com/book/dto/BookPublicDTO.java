package com.book.dto;

import java.time.LocalDateTime;
import java.util.SortedSet;
import com.book.model.ProductImage;

public class BookPublicDTO {

    private Long productId;
    private Long sellerId;
    private String isbn;
    private String name;
    private String category;
    private String author;
    private String publisher;
    private String productNew;
    private String productClassNote;
    private String productNote;
    private Integer price;
    private Integer stock;
    private String shelfStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private SortedSet<ProductImage> images;

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

    public SortedSet<ProductImage> getImages() {
        return images;
    }

    public void setImages(SortedSet<ProductImage> images) {
        this.images = images;
    }
}
