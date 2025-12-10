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

    @Query("""
                SELECT
                    b.book_ISBN as isbn,
                    MIN(b.book_name) as name,
                    MIN(b.book_author) as author,
                    MIN(b.book_publisher) as publisher,
                    MIN(b.category) as category,
                    MIN(b.product_price) as min_price,
                    SUM(b.product_stock) as total_stock,
                    (SELECT pi.image_url
                     FROM product_images pi
                     JOIN products p2 ON pi.product_id = p2.product_id
                     WHERE p2.book_ISBN = b.book_ISBN
                     LIMIT 1) as cover_image
                FROM products b
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
                    SUM(b.product_stock) as total_stock,
                    (SELECT pi.image_url
                     FROM product_images pi
                     JOIN products p2 ON pi.product_id = p2.product_id
                     WHERE p2.book_ISBN = b.book_ISBN
                     LIMIT 1) as cover_image
                FROM products b
                WHERE b.shelf_status = '上架' AND b.category = :category
                GROUP BY b.book_ISBN
            """)
    List<BookSummaryDTO> findBookSummariesByCategory(String category);
}
