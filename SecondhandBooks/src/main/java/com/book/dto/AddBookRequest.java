package com.book.dto;

import lombok.Data;

@Data
public class AddBookRequest {
    private String isbn;
    private String title; // corresponds to 'name' in Book entity
    private String author;
    private String publisher;
    private String category;
    private String condition; // productNew
    private String notes; // productClassNote (e.g., 'none', 'few', 'many' mapped to description)
                          // Actually, frontend sends 'none', 'few', 'many'. We might want to map this.
                          // Let's keep it as String and handle mapping in Service or just store it.
    private String description; // productNote
    private Integer price;
}
