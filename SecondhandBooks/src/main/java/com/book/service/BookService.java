package com.book.service;

import com.book.model.Book;
import com.book.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookService {

    @Autowired
    private BookRepository bookRepository;

    /**
     * Get books by category (or all if category is null/empty).
     * Sorts "上架" items first.
     */
    /**
     * Get books by category (or all if category is null/empty).
     * Sorts "上架" items first.
     */
    public List<Book> getBooks(String category) {
        List<Book> books;
        if (category == null || category.trim().isEmpty() || "ALL".equalsIgnoreCase(category)) {
            books = bookRepository.findAll();
        } else {
            books = bookRepository.findByCategory(category);
        }

        // Sort: "上架" first, then others. Secondary sort by created_at desc (newest
        // first)
        return books.stream()
                .sorted(Comparator.comparing((Book b) -> "上架".equals(b.getShelfStatus()) ? 0 : 1)
                        .thenComparing(Book::getCreatedAt, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    /**
     * Get unique books grouped by ISBN (or Name if ISBN is missing).
     */
    public List<com.book.dto.BookSummaryDTO> getUniqueBooks(String category) {
        List<Book> allBooks = getBooks(category);

        // Group by ISBN (or Name if ISBN is null/empty)
        java.util.Map<String, List<Book>> groupedBooks = allBooks.stream()
                .collect(Collectors.groupingBy(book -> {
                    if (book.getIsbn() != null && !book.getIsbn().trim().isEmpty()) {
                        return book.getIsbn();
                    } else {
                        return book.getName(); // Fallback to name
                    }
                }));

        List<com.book.dto.BookSummaryDTO> summaries = new java.util.ArrayList<>();

        for (java.util.Map.Entry<String, List<Book>> entry : groupedBooks.entrySet()) {
            List<Book> group = entry.getValue();
            if (group.isEmpty())
                continue;

            Book representative = group.get(0); // Use the first one for common details

            com.book.dto.BookSummaryDTO dto = new com.book.dto.BookSummaryDTO();
            dto.setIsbn(representative.getIsbn());
            dto.setName(representative.getName());
            dto.setAuthor(representative.getAuthor());
            dto.setPublisher(representative.getPublisher());
            dto.setCategory(representative.getCategory());

            // Calculate stats from "上架" items only, or all?
            // User wants to see available products.
            List<Book> activeBooks = group.stream()
                    .filter(b -> "上架".equals(b.getShelfStatus()))
                    .collect(Collectors.toList());

            if (activeBooks.isEmpty()) {
                // If no active books, maybe show 0 stock and min price of inactive?
                // Or just show stats from all.
                dto.setTotalStock(0);
                dto.setMinPrice(group.stream().mapToInt(Book::getPrice).min().orElse(0));
            } else {
                dto.setTotalStock(activeBooks.stream().mapToInt(b -> b.getStock() == null ? 0 : b.getStock()).sum());
                dto.setMinPrice(activeBooks.stream().mapToInt(Book::getPrice).min().orElse(0));
            }

            // Find a cover image (first non-null image from any book in group)
            String coverImage = group.stream()
                    .flatMap(b -> b.getImages().stream())
                    .map(com.book.model.ProductImage::getImageUrl)
                    .findFirst()
                    .orElse(null);
            dto.setCoverImage(coverImage);

            summaries.add(dto);
        }

        return summaries;
    }

    /**
     * Get all listings for a specific book (by ISBN).
     * If ISBN is not found, try Name? The frontend will pass what it got from
     * BookSummaryDTO.
     * If it was grouped by Name (because ISBN was null), we need to handle that.
     * For now, let's assume we pass the key used for grouping.
     * But the URL /api/books/listings/{isbn} implies ISBN.
     * Let's support searching by ISBN first.
     */
    public List<com.book.dto.BookListingDTO> getBookListings(String identifier) {
        // We need to find books where ISBN = identifier OR Name = identifier
        List<Book> allBooks = bookRepository.findByIsbnOrName(identifier, identifier);

        return allBooks.stream()
                // .filter(b -> identifier.equals(b.getIsbn()) ||
                // identifier.equals(b.getName())) // Handled by DB query now
                .sorted(Comparator.comparing((Book b) -> "上架".equals(b.getShelfStatus()) ? 0 : 1)
                        .thenComparing(Book::getPrice)) // Cheapest first?
                .map(this::convertToBookListingDTO)
                .collect(Collectors.toList());
    }

    private com.book.dto.BookListingDTO convertToBookListingDTO(Book book) {
        com.book.dto.BookListingDTO dto = new com.book.dto.BookListingDTO();
        dto.setProductId(book.getProductId());
        if (book.getSeller() != null) {
            dto.setSellerName(book.getSeller().getAccount()); // Use account as name
        }
        dto.setCondition(book.getProductNew());
        dto.setNote(book.getProductNote());
        dto.setStatus(book.getProductClassNote()); // Mapping 'product_class_note' to 'status' (書況)
        dto.setPrice(book.getPrice());
        dto.setStock(book.getStock());
        dto.setUpdatedAt(book.getUpdatedAt());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setShelfStatus(book.getShelfStatus());

        dto.setImages(book.getImages().stream()
                .map(com.book.model.ProductImage::getImageUrl)
                .collect(Collectors.toList()));
        return dto;
    }
}
