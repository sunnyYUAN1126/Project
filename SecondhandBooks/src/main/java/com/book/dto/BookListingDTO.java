package com.book.dto;

import java.time.LocalDateTime;
import java.util.List;

public class BookListingDTO {
    private Long productId;
    private String sellerName;
    private String condition; // productNew
    private String note; // productNote
    private String status; // productClassNote (using this for '書況' as per user's mock data mapping, or
                           // maybe shelfStatus? User mock had 'status' as '良好'/'泛黃', likely
                           // productClassNote or a new field. I'll map productClassNote to it for now)
    private Integer price;
    private Integer stock;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private String shelfStatus;
    private List<String> images;

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getShelfStatus() {
        return shelfStatus;
    }

    public void setShelfStatus(String shelfStatus) {
        this.shelfStatus = shelfStatus;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "BookListingDTO{" +
                "productId=" + productId +
                ", sellerName='" + sellerName + '\'' +
                ", condition='" + condition + '\'' +
                ", note='" + note + '\'' +
                ", status='" + status + '\'' +
                ", price=" + price +
                ", stock=" + stock +
                ", updatedAt=" + updatedAt +
                ", createdAt=" + createdAt +
                ", shelfStatus='" + shelfStatus + '\'' +
                ", images=" + images +
                '}';
    }
}
