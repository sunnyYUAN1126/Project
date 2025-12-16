package com.book.repository;

import com.book.dto.CartItemDTO;
import com.book.model.Cart;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends CrudRepository<Cart, Long> {
    List<Cart> findByBuyerId(Long buyerId);

    @Query("""
            SELECT
                c.cart_id,
                c.product_id,
                p.book_name as product_name,
                p.product_price as product_price,
                u.account as seller_name,
                u.user_id as seller_id,
                (SELECT pi.image_url

                 FROM product_images pi
                 WHERE pi.product_id = p.product_id
                 LIMIT 1) as cover_image
            FROM carts c
            JOIN products p ON c.product_id = p.product_id
            JOIN users u ON p.seller_id = u.user_id
            WHERE c.buyer_id = :buyerId
            """)
    List<CartItemDTO> findCartItemsByBuyerId(@Param("buyerId") Long buyerId);

    boolean existsByBuyerIdAndProductId(Long buyerId, Long productId);

    // To count items in cart if needed
    long countByBuyerId(Long buyerId);
}
