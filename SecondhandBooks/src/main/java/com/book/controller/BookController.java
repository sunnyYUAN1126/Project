package com.book.controller;

import com.book.model.Book;
import com.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookService bookService;

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(@RequestParam(required = false) String category) {
        List<Book> books = bookService.getBooks(category);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/unique")
    public ResponseEntity<List<com.book.dto.BookSummaryDTO>> getUniqueBooks(
            @RequestParam(required = false) String category) {
        List<com.book.dto.BookSummaryDTO> books = bookService.getUniqueBooks(category);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/listings/{isbn}")
    public ResponseEntity<List<com.book.dto.BookListingDTO>> getBookListings(@PathVariable String isbn) {
        List<com.book.dto.BookListingDTO> listings = bookService.getBookListings(isbn);
        return ResponseEntity.ok(listings);
    }
}
