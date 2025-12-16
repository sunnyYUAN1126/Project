package com.book.service;

import com.book.dto.OrderRequest;
import com.book.model.Book;
import com.book.model.Cart;
import com.book.model.Order;
import com.book.model.OrderItem;
import com.book.repository.BookRepository;
import com.book.repository.CartRepository;
import com.book.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final BookRepository bookRepository;
    private final CartRepository cartRepository;

    public OrderService(OrderRepository orderRepository, BookRepository bookRepository, CartRepository cartRepository) {
        this.orderRepository = orderRepository;
        this.bookRepository = bookRepository;
        this.cartRepository = cartRepository;
    }

    @Transactional
    public Order createOrder(Long buyerId, OrderRequest request) {
        Order order = new Order();
        order.setBuyerId(buyerId);
        order.setSellerId(request.getSellerId());
        order.setMeetupLocation(request.getMeetupLocation());
        order.setMeetupDate(request.getMeetupDate());
        order.setMeetupTime(request.getMeetupTime());
        order.setStatus("待面交");
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());

        List<Cart> cartItems = cartRepository.findByBuyerId(buyerId);
        boolean itemsFound = false;

        for (Cart cart : cartItems) {
            Book book = bookRepository.findById(cart.getProductId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));

            // Filter items by seller
            if (book.getSellerId().equals(request.getSellerId())) {
                // Check availability
                if (!"上架".equals(book.getShelfStatus())) {
                    throw new RuntimeException("Book '" + book.getName() + "' is no longer available.");
                }

                OrderItem item = new OrderItem();
                item.setProductId(book.getProductId());

                order.addItem(item);

                // Update Book Status to 'In Transaction'
                book.setShelfStatus("交易中");
                bookRepository.save(book);

                // Remove from Cart
                cartRepository.delete(cart);
                itemsFound = true;
            }
        }

        if (!itemsFound) {
            throw new RuntimeException("No items found in cart for this seller.");
        }

        return orderRepository.save(order);
    }

    @Transactional
    public void completeOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getSellerId().equals(userId)) {
            throw new RuntimeException("Only the seller can complete this order.");
        }

        if (!"待面交".equals(order.getStatus())) {
            throw new RuntimeException("Order is not in valid state for completion.");
        }

        order.setStatus("完成");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            Book book = bookRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            book.setStock(0);
            book.setShelfStatus("已售出"); // Mark as sold
            bookRepository.save(book);
        }
    }

    @Transactional
    public void cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Allow both buyer and seller to cancel? Usually yes.
        if (!order.getBuyerId().equals(userId) && !order.getSellerId().equals(userId)) {
            throw new RuntimeException("You are not authorized to cancel this order.");
        }

        // Only allow cancel if not already completed or cancelled
        if ("完成".equals(order.getStatus()) || "取消".equals(order.getStatus())) {
            throw new RuntimeException("Order cannot be cancelled.");
        }

        order.setStatus("取消");
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        for (OrderItem item : order.getItems()) {
            Book book = bookRepository.findById(item.getProductId())
                    .orElseThrow(() -> new RuntimeException("Book not found"));
            book.setShelfStatus("上架"); // Restore to shelf
            bookRepository.save(book);
        }
    }

    public List<Order> getOrdersByBuyer(Long buyerId) {
        return orderRepository.findByBuyerId(buyerId);
    }

    public List<Order> getOrdersBySeller(Long sellerId) {
        return orderRepository.findBySellerId(sellerId);
    }
}
