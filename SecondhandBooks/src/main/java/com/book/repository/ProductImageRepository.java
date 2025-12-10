package com.book.repository;

import com.book.model.ProductImage;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends ListCrudRepository<ProductImage, Long> {
}
