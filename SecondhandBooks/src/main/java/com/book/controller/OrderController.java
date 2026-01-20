package com.book.controller;

import com.book.dto.OrderDTO;
import com.book.dto.OrderItemDTO;
import com.book.dto.OrderRequest;
import com.book.model.Book;
import com.book.model.Order;
import com.book.model.OrderItem;
import com.book.repository.BookRepository;
import com.book.service.OrderService;
import com.book.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final BookRepository bookRepository;
    private final com.book.repository.UserRepository userRepository;
    private final AuthService authService;

    public OrderController(OrderService orderService, BookRepository bookRepository,
            com.book.repository.UserRepository userRepository, AuthService authService) {
        this.orderService = orderService;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.authService = authService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody OrderRequest request, HttpSession session) {
        Long userId;
        try {
            userId = authService.getCurrentUserId(session);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

        try {
            Order order = orderService.createOrder(userId, request);
            return ResponseEntity.ok(convertToDTO(order));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<?> complete(@PathVariable Long orderId, HttpSession session) {
        Long userId;
        try {
            userId = authService.getCurrentUserId(session);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

        try {
            // Fetch order to verify ownership (should be seller)
            // In a real app service should handle this, but for MVP we do it here or let
            // service handle it.
            // Let's pass userId to service methods for verification.
            orderService.completeOrder(orderId, userId);

            return ResponseEntity.ok(Map.of("message", "Order completed"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancel(@PathVariable Long orderId, HttpSession session) {
        Long userId;
        try {
            userId = authService.getCurrentUserId(session);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

        try {
            // Pass userId to validation
            orderService.cancelOrder(orderId, userId);
            return ResponseEntity.ok(Map.of("message", "Order cancelled"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> listOrders(HttpSession session) {
        Long userId;
        try {
            userId = authService.getCurrentUserId(session);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).body(e.getReason());
        }

        List<Order> buyingOrders = orderService.getOrdersByBuyer(userId);
        List<OrderDTO> buyingDTOs = buyingOrders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        List<Order> sellingOrders = orderService.getOrdersBySeller(userId);
        List<OrderDTO> sellingDTOs = sellingOrders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        Map<String, List<OrderDTO>> response = new HashMap<>();
        response.put("buying", buyingDTOs);
        response.put("selling", sellingDTOs);

        return ResponseEntity.ok(response);
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setBuyerId(order.getBuyerId());

        com.book.model.User buyer = userRepository.findById(order.getBuyerId()).orElse(null);
        dto.setBuyerName(buyer != null ? buyer.getAccount() : "Unknown");

        dto.setSellerId(order.getSellerId());

        com.book.model.User seller = userRepository.findById(order.getSellerId()).orElse(null);
        dto.setSellerName(seller != null ? seller.getAccount() : "Unknown");

        dto.setMeetupLocation(order.getMeetupLocation());

        dto.setMeetupDate(order.getMeetupDate());
        dto.setMeetupTime(order.getMeetupTime());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        int total = 0;
        List<OrderItemDTO> itemDTOs = new ArrayList<>();
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                Book book = bookRepository.findById(item.getProductId()).orElse(null);
                String name = (book != null) ? book.getName() : "Unknown Product";
                String author = (book != null) ? book.getAuthor() : "";

                String coverImage = null;
                if (book != null && book.getImages() != null && !book.getImages().isEmpty()) {
                    String rawPath = book.getImages().first().getImageUrl();
                    if (rawPath != null) {
                        // Extract filename and prepend /images/
                        // Assuming rawPath is like D:/Project/picture/filename.jpg
                        String filename = new java.io.File(rawPath).getName();
                        coverImage = "http://localhost:8080/images/" + filename;
                    }
                }

                String isbn = (book != null) ? book.getIsbn() : "";
                Integer price = (book != null) ? book.getPrice() : 0;
                String productNew = (book != null) ? book.getProductNew() : "";
                String productClassNote = (book != null) ? book.getProductClassNote() : "";
                String productNote = (book != null) ? book.getProductNote() : "";

                itemDTOs.add(new OrderItemDTO(
                        item.getProductId(),
                        name,
                        author,
                        coverImage,
                        isbn,
                        price,
                        productNew,
                        productClassNote,
                        productNote));

                total += price;

            }
        }
        dto.setItems(itemDTOs);
        dto.setTotalPrice(total);
        return dto;
    }
}
