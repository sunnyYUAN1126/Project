package com.book.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
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
}
