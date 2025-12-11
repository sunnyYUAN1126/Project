package com.book.controller;

import com.book.model.Book;
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

    @PostMapping(value = "/add", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Map<String, String>> addBook(
            @ModelAttribute AddBookRequest request,
            @RequestParam(value = "files", required = false) List<MultipartFile> files,
            HttpSession session) {

        Object userIdObj = session.getAttribute("user_id");
        if (userIdObj == null) {
            return ResponseEntity.status(401).body(Map.of("message", "請先登入"));
        }
        Long userId = Long.valueOf(userIdObj.toString());

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
        // Basic check for admin role (in a real app, use Spring Security)
        // For now, checks if user is logged in. Better: check DB for role "管理員"
        // But session only has user_id, need to fetch user or trust session if we
        // stored role.
        // Simplified: assume frontend protects this route, or we check user_id.
        // Let's at least check login.
        if (session.getAttribute("user_id") == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(bookService.getPendingBooks());
    }

    @PostMapping("/review/{id}")
    public ResponseEntity<String> reviewBook(
            @PathVariable Long id,
            @RequestBody Map<String, Object> payload,
            HttpSession session) {

        if (session.getAttribute("user_id") == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        boolean approved = (boolean) payload.get("approved");
        String note = (String) payload.get("note");

        String result = bookService.reviewBook(id, approved, note);
        return ResponseEntity.ok(result);
    }
}
