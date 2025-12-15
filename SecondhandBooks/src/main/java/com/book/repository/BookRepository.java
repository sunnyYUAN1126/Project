package com.book.repository;

import com.book.model.Book;
import com.book.dto.BookSummaryDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import org.springframework.data.repository.query.Param;

@Repository
public interface BookRepository extends ListCrudRepository<Book, Long> {
  // Find by category
  List<Book> findByCategory(String category);

  // Find all books (inherited)

  // Find by ISBN or Name
  List<Book> findByIsbnOrName(String isbn, String name);

  // Match DB Enum: '待審核'
  List<Book> findByAdminReview(String adminReview);

  @Query("""
          SELECT *
          FROM products
          WHERE book_ISBN = :identifier AND shelf_status = '上架'
          ORDER BY product_price ASC
      """)

  List<Book> findListings(@Param("identifier") String identifier);

  // === New Separated Queries ===

  // 1. Stock Queries
  @Query("""
      SELECT book_ISBN as isbn, SUM(product_stock) as total_stock
      FROM products
      WHERE shelf_status = '上架'
      GROUP BY book_ISBN
      """)
  List<com.book.dto.BookStockDTO> findAllStocks();

  @Query("""
      SELECT book_ISBN as isbn, SUM(product_stock) as total_stock
      FROM products
      WHERE shelf_status = '上架' AND category = :category
      GROUP BY book_ISBN
      """)
  List<com.book.dto.BookStockDTO> findStocksByCategory(@Param("category") String category);

  // 2. Detail Queries (ISBN + Min Price)
  @Query("""
      SELECT
          b.book_ISBN as isbn,
          MIN(b.book_name) as name,
          MIN(b.book_author) as author,
          MIN(b.book_publisher) as publisher,
          MIN(b.category) as category,
          MIN(b.product_price) as min_price,
          (SELECT pi.image_url
           FROM product_images pi
           JOIN products p2 ON pi.product_id = p2.product_id
           WHERE p2.book_ISBN = b.book_ISBN
             AND p2.product_price = MIN(b.product_price)
             AND p2.shelf_status = '上架'
           LIMIT 1) as cover_image
      FROM products b
      WHERE b.shelf_status = '上架'
      GROUP BY b.book_ISBN
      """)
  List<BookSummaryDTO> findAllBookDetails();

  @Query("""
      SELECT
          b.book_ISBN as isbn,
          MIN(b.book_name) as name,
          MIN(b.book_author) as author,
          MIN(b.book_publisher) as publisher,
          MIN(b.category) as category,
          MIN(b.product_price) as min_price,
          (SELECT pi.image_url
           FROM product_images pi
           JOIN products p2 ON pi.product_id = p2.product_id
           WHERE p2.book_ISBN = b.book_ISBN
             AND p2.product_price = MIN(b.product_price)
             AND p2.shelf_status = '上架'
           LIMIT 1) as cover_image
      FROM products b
      WHERE b.shelf_status = '上架' AND b.category = :category
      GROUP BY b.book_ISBN
      """)
  List<BookSummaryDTO> findBookDetailsByCategory(@Param("category") String category);

  // 3. Search Queries (Like Name)
  @Query("""
      SELECT book_ISBN as isbn, SUM(product_stock) as total_stock
      FROM products
      WHERE shelf_status = '上架' AND book_name LIKE :query
      GROUP BY book_ISBN
      """)
  List<com.book.dto.BookStockDTO> findStocksByNameContaining(@Param("query") String query);

  @Query("""
      SELECT
          b.book_ISBN as isbn,
          MIN(b.book_name) as name,
          MIN(b.book_author) as author,
          MIN(b.book_publisher) as publisher,
          MIN(b.category) as category,
          MIN(b.product_price) as min_price,
          (SELECT pi.image_url
           FROM product_images pi
           JOIN products p2 ON pi.product_id = p2.product_id
           WHERE p2.book_ISBN = b.book_ISBN
             AND p2.product_price = MIN(b.product_price)
             AND p2.shelf_status = '上架'
           LIMIT 1) as cover_image
      FROM products b
      WHERE b.shelf_status = '上架' AND b.book_name LIKE :query
      GROUP BY b.book_ISBN
      """)
  List<BookSummaryDTO> findBookDetailsByNameContaining(@Param("query") String query);

  // Find by Seller ID
  List<Book> findBySellerId(Long sellerId);
}
