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

    @Autowired
    private com.book.repository.ProductImageRepository productImageRepository;

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
                .filter(b -> "上架".equals(b.getShelfStatus())) // Filter only on-shelf products
                .sorted(Comparator.comparing(Book::getPrice)) // Sort by price
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
                    .map(com.book.model.ProductImage::getImageUrl)
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
}
