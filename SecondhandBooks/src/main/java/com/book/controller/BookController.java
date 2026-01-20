package com.book.controller;

import com.book.model.Book;
import com.book.service.AuthService;
import com.book.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.multipart.MultipartFile;
import com.book.dto.AddBookRequest;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<List<Book>> getBooks(@RequestParam(name = "category", required = false) String category) {
        List<Book> books = bookService.getBooks(category);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/unique")
    public ResponseEntity<com.book.dto.PageResult<com.book.dto.BookSummaryDTO>> getUniqueBooks(
            @RequestParam(name = "category", required = false) String category,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        com.book.dto.PageResult<com.book.dto.BookSummaryDTO> books = bookService.getUniqueBooks(category, page, size);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/search")
    public ResponseEntity<com.book.dto.PageResult<com.book.dto.BookSummaryDTO>> searchBooks(
            @RequestParam("query") String query,
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        com.book.dto.PageResult<com.book.dto.BookSummaryDTO> books = bookService.searchBooks(query, page, size);
        return ResponseEntity.ok(books);
    }

    @GetMapping("/listings/{isbn}")
    public ResponseEntity<List<com.book.dto.BookListingDTO>> getBookListings(@PathVariable("isbn") String isbn) {
        List<com.book.dto.BookListingDTO> listings = bookService.getBookListings(isbn);
        return ResponseEntity.ok(listings);
    }

    @PostMapping(value = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, String>> addBook(
            @ModelAttribute AddBookRequest request,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            HttpSession session) {

        // Use AuthService to validate login and get ID
        Long userId;
        try {
            userId = authService.getCurrentUserId(session);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
        }

        try {
            String result = bookService.addBook(request, files, userId);
            if ("Book added successfully".equals(result)) {
                return ResponseEntity.ok(Map.of("message", "新增成功"));
            } else {
                return ResponseEntity.badRequest().body(Map.of("message", result));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("message", "伺服器錯誤: " + e.getMessage()));
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<com.book.dto.BookReviewDTO>> getPendingBooks(HttpSession session) {
        // Check for Admin role
        try {
            authService.validateAdmin(session);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            // Return 401 or 403 based on exception
            return ResponseEntity.status(e.getStatusCode()).build();
        }
        return ResponseEntity.ok(bookService.getPendingBooks());
    }

    @PostMapping("/review/{id}")
    public ResponseEntity<String> reviewBook(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload,
            HttpSession session) {

        try {
            authService.validateAdmin(session);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

        boolean approved = (boolean) payload.get("approved");
        String note = (String) payload.get("note");

        String result = bookService.reviewBook(id, approved, note);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/my-books")
    public ResponseEntity<List<com.book.dto.BookListingDTO>> getMyBooks(HttpSession session) {
        Long userId;
        try {
            userId = authService.getCurrentUserId(session);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        }
        return ResponseEntity.ok(bookService.getMyBooks(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBook(@PathVariable Long id, HttpSession session) {
        Long userId;
        try {
            userId = authService.getCurrentUserId(session);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(Map.of("message", e.getReason()));
        }

        String result = bookService.deleteBook(id, userId);
        if ("Book deleted successfully".equals(result)) {
            return ResponseEntity.ok(Map.of("message", result));
        } else if (result.startsWith("Unauthorized")) {
            return ResponseEntity.status(403).body(Map.of("message", result));
        } else {
            return ResponseEntity.status(404).body(Map.of("message", result));
        }
    }
}
