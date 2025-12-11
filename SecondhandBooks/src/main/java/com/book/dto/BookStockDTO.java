package com.book.dto;

import lombok.Data;

@Data
public class BookStockDTO {
    private String isbn;
    private Long totalStock;
}
