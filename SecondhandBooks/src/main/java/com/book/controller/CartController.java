package com.book.controller;

import com.book.dto.CartItemDTO;
import com.book.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{buyerId}")
    public ResponseEntity<?> getCartItems(@PathVariable Long buyerId, jakarta.servlet.http.HttpSession session) {
        // 1. 檢查是否登入
        Long loggedInUserId = (Long) session.getAttribute("user_id");
        if (loggedInUserId == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        // 2. 檢查權限 (只能看自己的購物車)
        if (!loggedInUserId.equals(buyerId)) {
            return ResponseEntity.status(403).body("無權限查看他人購物車");
        }

        List<CartItemDTO> items = cartService.getCartItems(buyerId);
        return ResponseEntity.ok(items);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody Map<String, Long> payload,
            jakarta.servlet.http.HttpSession session) {
        // 1. 檢查是否登入
        Long loggedInUserId = (Long) session.getAttribute("user_id");
        if (loggedInUserId == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        Long buyerId = payload.get("buyerId");
        Long productId = payload.get("productId");

        if (buyerId == null || productId == null) {
            return ResponseEntity.badRequest().body("buyerId and productId are required");
        }

        // 2. 檢查權限 (只能幫自己加購物車)
        if (!loggedInUserId.equals(buyerId)) {
            return ResponseEntity.status(403).body("無權限為他人加入購物車");
        }

        cartService.addToCart(buyerId, productId);
        return ResponseEntity.ok("Item added to cart");
    }

    @DeleteMapping("/{cartId}")
    public ResponseEntity<?> removeFromCart(@PathVariable Long cartId, jakarta.servlet.http.HttpSession session) {
        // 1. 檢查是否登入
        Long loggedInUserId = (Long) session.getAttribute("user_id");
        if (loggedInUserId == null) {
            return ResponseEntity.status(401).body("請先登入");
        }

        // 2. 檢查權限 (只能刪除自己的購物車項目)
        com.book.model.Cart cart = cartService.getCart(cartId);
        if (cart != null) {
            if (!loggedInUserId.equals(cart.getBuyerId())) {
                return ResponseEntity.status(403).body("無權限刪除此項目");
            }
            cartService.removeFromCart(cartId);
            return ResponseEntity.ok("Item removed from cart");
        } else {
            // 項目不存在，也算刪除成功或回傳 404，這裡回傳成功保持冪等性或 404 告知
            return ResponseEntity.ok("Item not found or already removed");
        }
    }
}
