package com.book.dto;

public class OrderItemDTO {
    private Long productId;
    private String productName;
    private String productAuthor;
    private String coverImage;
    private String isbn;
    private Integer price;

    public OrderItemDTO(Long productId, String productName, String productAuthor, String coverImage, String isbn,
            Integer price) {
        this.productId = productId;
        this.productName = productName;
        this.productAuthor = productAuthor;
        this.coverImage = coverImage;
        this.isbn = isbn;
        this.price = price;
    }

    // Getters and Setters
    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductAuthor() {
        return productAuthor;
    }

    public void setProductAuthor(String productAuthor) {
        this.productAuthor = productAuthor;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
