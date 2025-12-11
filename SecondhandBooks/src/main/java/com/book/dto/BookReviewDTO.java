package com.book.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
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
}
