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

    @Autowired
    private com.book.repository.UserRepository userRepository;

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
    /**
     * Get unique books grouped by ISBN.
     * Uses efficient database aggregation via Spring Data JDBC.
     */
    public List<com.book.dto.BookSummaryDTO> getUniqueBooks(String category) {
        List<com.book.dto.BookSummaryDTO> details;
        List<com.book.dto.BookStockDTO> stocks;

        if (category == null || category.trim().isEmpty() || "ALL".equalsIgnoreCase(category)) {
            details = bookRepository.findAllBookDetails();
            stocks = bookRepository.findAllStocks();
        } else {
            details = bookRepository.findBookDetailsByCategory(category);
            stocks = bookRepository.findStocksByCategory(category);
        }

        return mergeStockIntoDetails(details, stocks);
    }

    public List<com.book.dto.BookSummaryDTO> searchBooks(String query) {
        if (query == null || query.trim().isEmpty()) {
            return java.util.Collections.emptyList();
        }
        String likeQuery = "%" + query.trim() + "%";
        List<com.book.dto.BookSummaryDTO> details = bookRepository.findBookDetailsByNameContaining(likeQuery);
        List<com.book.dto.BookStockDTO> stocks = bookRepository.findStocksByNameContaining(likeQuery);

        return mergeStockIntoDetails(details, stocks);
    }

    private List<com.book.dto.BookSummaryDTO> mergeStockIntoDetails(
            List<com.book.dto.BookSummaryDTO> details,
            List<com.book.dto.BookStockDTO> stocks) {

        // Create a map for quick stock lookup by ISBN
        java.util.Map<String, Integer> stockMap = stocks.stream()
                .collect(Collectors.toMap(
                        com.book.dto.BookStockDTO::getIsbn,
                        dto -> dto.getTotalStock().intValue(), // Convert Long to Integer
                        (existing, replacement) -> existing // In case of duplicate ISBNs (shouldn't happen with GROUP
                                                            // BY)
                ));

        // Merge stock info into details
        for (com.book.dto.BookSummaryDTO detail : details) {
            String isbn = detail.getIsbn();
            if (isbn != null && stockMap.containsKey(isbn)) {
                detail.setTotalStock(stockMap.get(isbn));
            } else {
                detail.setTotalStock(0);
            }
        }
        return details;
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
        // Use DB sorting and filtering
        List<Book> allBooks = bookRepository.findListings(identifier);

        return allBooks.stream()
                // .filter and .sorted are now handled by DB
                .map(this::convertToBookListingDTO)
                .collect(Collectors.toList());
    }

    private com.book.dto.BookListingDTO convertToBookListingDTO(Book book) {
        com.book.dto.BookListingDTO dto = new com.book.dto.BookListingDTO();
        dto.setProductId(book.getProductId());

        // JDBC: Fetch user manually by ID
        if (book.getSellerId() != null) {
            String sellerName = userRepository.findById(book.getSellerId())
                    .map(com.book.model.User::getAccount)
                    .orElse("Unknown");
            dto.setSellerName(sellerName);
        }
        dto.setSellerId(book.getSellerId());

        dto.setCondition(book.getProductNew());
        dto.setNote(book.getProductNote());
        dto.setStatus(book.getProductClassNote()); // Mapping 'product_class_note' to 'status' (書況)
        dto.setPrice(book.getPrice());
        dto.setStock(book.getStock());
        dto.setUpdatedAt(book.getUpdatedAt());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setShelfStatus(book.getShelfStatus());

        if (book.getImages() != null) {
            dto.setImages(book.getImages().stream()
                    .map(img -> sanitizeImageUrl(img.getImageUrl()))
                    .collect(Collectors.toList()));
        } else {
            dto.setImages(java.util.Collections.emptyList());
        }
        return dto;
    }

    public String addBook(com.book.dto.AddBookRequest request,
            java.util.List<org.springframework.web.multipart.MultipartFile> files, Long sellerId) {
        // 1. Verify seller
        com.book.model.User seller = userRepository.findById(sellerId).orElse(null);
        if (seller == null) {
            return "Seller not found";
        }

        // 2. Create Book entity
        Book book = new Book();
        book.setIsbn(request.getIsbn());
        book.setName(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setPublisher(request.getPublisher());
        book.setCategory(request.getCategory());
        book.setProductNew(request.getCondition());
        book.setProductClassNote(request.getNotes()); // "none", "few", "many" or whatever frontend sends
        book.setProductNote(request.getDescription());
        book.setPrice(request.getPrice());
        book.setSellerId(seller.getUserId()); // Set ID
        book.setShelfStatus("下架");
        book.setAdminReview("待審核");
        book.setStock(1); // Default to 1

        // Initialize image list
        java.util.SortedSet<com.book.model.ProductImage> images = new java.util.TreeSet<>();

        // 3. Process files
        if (files != null && !files.isEmpty()) {
            String uploadDir = "D:/Project/picture/";
            java.io.File dir = new java.io.File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            int sortOrder = 1;
            for (org.springframework.web.multipart.MultipartFile file : files) {
                if (file.isEmpty())
                    continue;
                try {
                    String originalFilename = file.getOriginalFilename();
                    String extension = "";
                    if (originalFilename != null && originalFilename.contains(".")) {
                        extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                    }
                    String newFilename = java.util.UUID.randomUUID().toString() + extension;
                    java.nio.file.Path path = java.nio.file.Paths.get(uploadDir + newFilename);
                    java.nio.file.Files.copy(file.getInputStream(), path,
                            java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                    com.book.model.ProductImage image = new com.book.model.ProductImage();
                    // Don't set book, it's an aggregate child
                    // Use the mapped URL
                    image.setImageUrl("http://localhost:8080/images/" + newFilename);
                    image.setSortOrder(sortOrder++);

                    images.add(image);

                } catch (java.io.IOException e) {
                    e.printStackTrace();
                    return "Error saving file";
                }
            }
        }

        // Add images to book
        book.setImages(images);

        // Save Book (and cascades to images)
        bookRepository.save(book);

        return "Book added successfully";
    }

    public List<com.book.dto.BookReviewDTO> getPendingBooks() {
        List<Book> books = bookRepository.findByAdminReview("待審核");
        return books.stream()
                .map(this::convertToBookReviewDTO)
                .collect(Collectors.toList());
    }

    // 轉換 Helper 方法
    private com.book.dto.BookReviewDTO convertToBookReviewDTO(Book book) {
        com.book.dto.BookReviewDTO dto = new com.book.dto.BookReviewDTO();
        dto.setProductId(book.getProductId());
        dto.setSellerId(book.getSellerId());

        // 關鍵邏輯：透過 SellerId 去 User 表查詢 "賣家帳號" (sellerAccount)
        if (book.getSellerId() != null) {
            String sellerAccount = userRepository.findById(book.getSellerId())
                    .map(com.book.model.User::getAccount)
                    .orElse("Unknown");
            dto.setSellerAccount(sellerAccount);
        }

        dto.setIsbn(book.getIsbn());
        dto.setName(book.getName());
        dto.setAuthor(book.getAuthor());
        dto.setPublisher(book.getPublisher());
        dto.setCategory(book.getCategory());
        dto.setProductNew(book.getProductNew());
        dto.setProductClassNote(book.getProductClassNote());
        dto.setProductNote(book.getProductNote());
        dto.setPrice(book.getPrice());
        dto.setCreatedAt(book.getCreatedAt());
        dto.setAdminNote(book.getAdminNote());

        if (book.getImages() != null) {
            dto.setImages(book.getImages().stream()
                    .map(img -> sanitizeImageUrl(img.getImageUrl()))
                    .collect(Collectors.toList()));
        } else {
            dto.setImages(java.util.Collections.emptyList());
        }

        return dto;
    }

    private String sanitizeImageUrl(String url) {
        if (url == null)
            return null;

        // Handle file encoded paths or raw paths
        String cleanUrl = url.trim();

        // If it's already a correct HTTP URL, return it
        if (cleanUrl.startsWith("http://localhost:8080/images/")) {
            return cleanUrl;
        }

        // Check for local file path patterns (both forward and backward slashes)
        // Adjust regex or logic based on what's actually in DB.
        // Assuming common "D:/Project/picture/" or "file:///D:/Project/picture/"

        String filename = null;

        if (cleanUrl.contains("D:/Project/picture/") || cleanUrl.contains("D:\\Project\\picture\\")) {
            // Extract filename
            java.io.File f = new java.io.File(cleanUrl.replace("file:///", ""));
            filename = f.getName();
        } else if (cleanUrl.startsWith("file:/")) {
            // Try to just take the last part
            int lastSlash = cleanUrl.lastIndexOf('/');
            if (lastSlash != -1) {
                filename = cleanUrl.substring(lastSlash + 1);
            }
        }

        if (filename != null && !filename.isEmpty()) {
            return "http://localhost:8080/images/" + filename;
        }

        // Fallback: return original if we can't parse it, or maybe it's a relative
        // path?
        return cleanUrl;
    }

    public String reviewBook(Long bookId, boolean isApproved, String note) {
        return bookRepository.findById(bookId).map(book -> {
            if (isApproved) {
                // DB Enum: '審核通過'
                book.setAdminReview("審核通過");
                book.setShelfStatus("上架");
            } else {
                // DB Enum: '審核不通過'
                book.setAdminReview("審核不通過");
                book.setShelfStatus("下架");
            }
            book.setAdminNote(note);
            // Since we are updating, we should update updatedAt if using auditing, but JDBC
            // auditing handles it.
            // If not, we might need manual update: book.setUpdatedAt(LocalDateTime.now());
            // Based on Book.java existing annotations (@LastModifiedDate), it should handle
            // it.
            bookRepository.save(book);
            return "Review submitted";
        }).orElse("Book not found");
    }
}
