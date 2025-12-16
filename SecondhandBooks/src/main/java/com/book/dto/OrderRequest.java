package com.book.dto;

import java.time.LocalDate;

public class OrderRequest {
    private Long sellerId;
    private String meetupLocation;
    private LocalDate meetupDate;
    private String meetupTime;
    // We assume the cart items are automatically fetched for the buyer,
    // or passed as IDs. For now, let's assume we convert the user's entire cart
    // for that specific seller into an order.
    // Or simpler: The frontend sends the List of Cart Item IDs to checkout.

    // Based on user flow: "Cart -> Checkout". Usually checkouts whole cart or
    // selected items.
    // Let's assume for this MVP we checkout all items in the cart for a specific
    // seller (since orders are usually per seller).
    // Or if the app supports mixed orders, we might need to split them.
    // Given the schema "orders" has "seller_id", it implies one order per seller.
    // So the request should probably target a specific cart item or list of cart
    // items.

    // Let's keep it simple: Validate cart items on backend.

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getMeetupLocation() {
        return meetupLocation;
    }

    public void setMeetupLocation(String meetupLocation) {
        this.meetupLocation = meetupLocation;
    }

    public LocalDate getMeetupDate() {
        return meetupDate;
    }

    public void setMeetupDate(LocalDate meetupDate) {
        this.meetupDate = meetupDate;
    }

    public String getMeetupTime() {
        return meetupTime;
    }

    public void setMeetupTime(String meetupTime) {
        this.meetupTime = meetupTime;
    }
}
