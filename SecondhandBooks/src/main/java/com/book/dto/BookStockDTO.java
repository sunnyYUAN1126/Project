package com.book.dto;

public class BookStockDTO {
    private String isbn;
    private Long totalStock;

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Long getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(Long totalStock) {
        this.totalStock = totalStock;
    }

    @Override
    public String toString() {
        return "BookStockDTO{" +
                "isbn='" + isbn + '\'' +
                ", totalStock=" + totalStock +
                '}';
    }
}
