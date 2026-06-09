package com.restaurant.management.service;

import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.requests.AddToCartRequest;
import com.restaurant.management.requests.CheckoutRequest;
import com.restaurant.management.requests.UpdateCartItemRequest;
import com.restaurant.management.responses.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IFoodOrderService {
    // Favorites
    void addFavorite(Long userId, Long foodId);
    void removeFavorite(Long userId, Long foodId);
    List<FavoriteResponse> getMyFavorites(Long userId);

    // Cart
    CartItemResponse addToCart(Long userId, AddToCartRequest request);
    CartItemResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request);
    void removeCartItem(Long userId, Long cartItemId);
    CartResponse getMyCart(Long userId);
    void clearCart(Long userId);

    // Orders
    CheckoutResponse checkout(Long userId, CheckoutRequest request) throws Exception;
    List<FoodOrderResponse> getMyOrders(Long userId);
    FoodOrderResponse getOrderDetails(Long userId, Long orderId);
    FoodOrderResponse cancelOrder(Long userId, Long orderId);

    // Admin Orders
    Page<FoodOrderResponse> getAllOrdersForAdmin(String status, String paymentStatus, String query, int page, int size, String sort);
    FoodOrderResponse updateOrderStatusForAdmin(Long orderId, String newStatus);

    // IPN Processing
    void processOrderPaymentIPN(Long orderId, String paymentMethod, double amount);
    void updatePaymentStatus(Long id, String paymentStatus, String transId);
}

