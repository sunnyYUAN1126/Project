package com.book.service;

import com.book.dto.CartItemDTO;
import com.book.model.Cart;
import com.book.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    private final CartRepository cartRepository;

    public CartService(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    public List<CartItemDTO> getCartItems(Long buyerId) {
        return cartRepository.findCartItemsByBuyerId(buyerId);
    }

    public void addToCart(Long buyerId, Long productId) {
        // Prevent duplicates
        if (cartRepository.existsByBuyerIdAndProductId(buyerId, productId)) {
            return; // Or throw exception
        }

        Cart cart = new Cart();
        cart.setBuyerId(buyerId);
        cart.setProductId(productId);
        cart.setUpdatedAt(LocalDateTime.now());

        cartRepository.save(cart);
    }

    public void removeFromCart(Long cartId) {
        cartRepository.deleteById(cartId);
    }

    public Cart getCart(Long cartId) {
        return cartRepository.findById(cartId).orElse(null);
    }
}
