const API_BASE_URL = "http://localhost:8080/api/cart";

export const CartApi = {
    async getCart(buyerId) {
        const response = await fetch(`${API_BASE_URL}/${buyerId}`, {
            credentials: 'include'
        });
        if (!response.ok) {
            throw new Error("Failed to fetch cart");
        }
        return response.json();
    },

    async addToCart(buyerId, productId) {
        const response = await fetch(`${API_BASE_URL}/add`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({ buyerId, productId }),
            credentials: 'include'
        });
        if (!response.ok) {
            throw new Error("Failed to add to cart");
        }
        return response; // Retrieve text or json if needed
    },

    async removeFromCart(cartId) {
        const response = await fetch(`${API_BASE_URL}/${cartId}`, {
            method: "DELETE",
            credentials: 'include'
        });
        if (!response.ok) {
            throw new Error("Failed to remove item");
        }
        return response;
    },
};
