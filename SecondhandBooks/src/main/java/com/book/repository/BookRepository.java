package com.book.repository;

import com.book.model.Book;
import com.book.dto.BookSummaryDTO;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

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
                SELECT
                    b.book_ISBN as isbn,
                    MIN(b.book_name) as name,
                    MIN(b.book_author) as author,
                    MIN(b.book_publisher) as publisher,
                    MIN(b.category) as category,
                    MIN(b.product_price) as min_price,
                    MAX(stock_agg.total_stock) as total_stock,
                    (SELECT pi.image_url
                     FROM product_images pi
                     JOIN products p2 ON pi.product_id = p2.product_id
                     WHERE p2.book_ISBN = b.book_ISBN
                       AND p2.product_price = MIN(b.product_price)
                       AND p2.shelf_status = '上架'
                     LIMIT 1) as cover_image
                FROM products b
                JOIN (
                    SELECT book_ISBN, MIN(product_price) as min_price, SUM(product_stock) as total_stock
                    FROM products
                    WHERE shelf_status = '上架'
                    GROUP BY book_ISBN
                ) stock_agg ON b.book_ISBN = stock_agg.book_ISBN AND b.product_price = stock_agg.min_price
                WHERE b.shelf_status = '上架'
                GROUP BY b.book_ISBN
            """)
    List<BookSummaryDTO> findBookSummaries();

    @Query("""
                SELECT
                    b.book_ISBN as isbn,
                    MIN(b.book_name) as name,
                    MIN(b.book_author) as author,
                    MIN(b.book_publisher) as publisher,
                    MIN(b.category) as category,
                    MIN(b.product_price) as min_price,
                    MAX(stock_agg.total_stock) as total_stock,
                    (SELECT pi.image_url
                     FROM product_images pi
                     JOIN products p2 ON pi.product_id = p2.product_id
                     WHERE p2.book_ISBN = b.book_ISBN
                       AND p2.product_price = MIN(b.product_price)
                       AND p2.shelf_status = '上架'
                     LIMIT 1) as cover_image
                FROM products as b
                JOIN (
                    SELECT book_ISBN, MIN(product_price) as min_price, SUM(product_stock) as total_stock
                    FROM products
                    WHERE shelf_status = '上架' AND category = :category
                    GROUP BY book_ISBN
                ) as stock_agg
                ON b.book_ISBN = stock_agg.book_ISBN AND b.product_price = stock_agg.min_price
                WHERE b.shelf_status = '上架' AND b.category = :category
                GROUP BY b.book_ISBN
            """)
    List<BookSummaryDTO> findBookSummariesByCategory(String category);
}
