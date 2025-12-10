package com.book.repository;

import com.book.model.Book;
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
}
