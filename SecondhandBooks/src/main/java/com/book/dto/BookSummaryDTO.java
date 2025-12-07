package com.book.dto;

import lombok.Data;

@Data
public class BookSummaryDTO {
    private String isbn;
    private String name;
    private String author;
    private String publisher;
    private String category;
    private Integer minPrice;
    private Integer totalStock;
    private String coverImage;
}
