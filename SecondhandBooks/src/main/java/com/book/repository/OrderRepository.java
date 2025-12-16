package com.book.repository;

import com.book.model.Order;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends ListCrudRepository<Order, Long> {
    List<Order> findByBuyerId(Long buyerId);

    List<Order> findBySellerId(Long sellerId);
}
