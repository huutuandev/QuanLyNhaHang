package com.restaurant.management.service.Impl;

import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.dto.FoodDTO;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.models.*;
import com.restaurant.management.requests.AddToCartRequest;
import com.restaurant.management.requests.CheckoutRequest;
import com.restaurant.management.requests.UpdateCartItemRequest;
import com.restaurant.management.responses.*;
import com.restaurant.management.respository.*;
import com.restaurant.management.service.IFoodOrderService;
import com.restaurant.management.service.IMoMoService;
import com.restaurant.management.service.IVnPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodOrderServiceImpl implements IFoodOrderService {

    private final FavoriteRepository favoriteRepository;
    private final CartItemRepository cartItemRepository;
    private final FoodOrderRepository foodOrderRepository;
    private final FoodRepository foodRepository;
    private final UserRepository userRepository;
    private final IMoMoService momoService;
    private final IVnPayService vnPayService;

    // --- FAVORITES SYSTEM ---

    @Override
    @Transactional
    public void addFavorite(Long userId, Long foodId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        FoodEntity food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        if (favoriteRepository.existsByUserAndFood(user, food)) {
            throw new IllegalArgumentException("Food is already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .food(food)
                .build();
        favoriteRepository.save(favorite);
        log.info("Favorite added: User ID: {}, Food ID: {}", user.getId(), food.getId());
    }

    @Override
    @Transactional
    public void removeFavorite(Long userId, Long foodId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        FoodEntity food = foodRepository.findById(foodId)
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        Favorite favorite = favoriteRepository.findByUserAndFood(user, food)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found"));

        favoriteRepository.delete(favorite);
        log.info("Favorite removed: User ID: {}, Food ID: {}", user.getId(), food.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponse> getMyFavorites(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Favorite> favorites = favoriteRepository.findByUser(user);
        return favorites.stream()
                .map(fav -> FavoriteResponse.builder()
                        .id(fav.getId())
                        .food(mapToFoodDTO(fav.getFood()))
                        .createdAt(fav.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // --- SHOPPING CART ---

    @Override
    @Transactional
    public CartItemResponse addToCart(Long userId, AddToCartRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        FoodEntity food = foodRepository.findById(foodId(request.getFoodId()))
                .orElseThrow(() -> new ResourceNotFoundException("Food not found"));

        if (food.getIsDeleted() != null && food.getIsDeleted()) {
            throw new IllegalArgumentException("Food item is no longer available");
        }

        CartItem cartItem = cartItemRepository.findByUserAndFood(user, food)
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartItem.setUnitPrice(food.getPrice());
        } else {
            cartItem = CartItem.builder()
                    .user(user)
                    .food(food)
                    .quantity(request.getQuantity())
                    .unitPrice(food.getPrice())
                    .build();
        }

        CartItem saved = cartItemRepository.save(cartItem);
        log.info("Cart updated: User ID: {}, Food ID: {}, Quantity added: {}", user.getId(), food.getId(), request.getQuantity());

        return mapToCartItemResponse(saved);
    }

    @Override
    @Transactional
    public CartItemResponse updateCartItemQuantity(Long userId, Long cartItemId, UpdateCartItemRequest request) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this cart item");
        }

        cartItem.setQuantity(request.getQuantity());
        CartItem saved = cartItemRepository.save(cartItem);
        log.info("Cart item quantity updated: Item ID: {}, New Quantity: {}", cartItemId, request.getQuantity());

        return mapToCartItemResponse(saved);
    }

    @Override
    @Transactional
    public void removeCartItem(Long userId, Long cartItemId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found"));

        if (!cartItem.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not own this cart item");
        }

        cartItemRepository.delete(cartItem);
        log.info("Cart item removed: Item ID: {} for user: {}", cartItemId, user.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public CartResponse getMyCart(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CartItem> items = cartItemRepository.findByUser(user);
        List<CartItemResponse> itemResponses = items.stream()
                .map(this::mapToCartItemResponse)
                .collect(Collectors.toList());

        double subtotal = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();

        int totalQuantity = items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        return CartResponse.builder()
                .items(itemResponses)
                .subtotal(subtotal)
                .totalQuantity(totalQuantity)
                .build();
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        cartItemRepository.deleteByUser(user);
        log.info("Cart cleared for User ID: {}", user.getId());
    }

    // --- DELIVERY ORDER MODULE ---

    @Override
    @Transactional
    public CheckoutResponse checkout(Long userId, CheckoutRequest request) throws Exception {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<CartItem> cartItems = cartItemRepository.findByUser(user);
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Shopping cart is empty");
        }

        double subtotal = cartItems.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        double shippingFee = 15000.0; // Flat-rate shipping fee
        double totalAmount = subtotal + shippingFee;

        // Create the order entity
        FoodOrder order = FoodOrder.builder()
                .user(user)
                .deliveryAddress(request.getDeliveryAddress())
                .receiverName(request.getReceiverName())
                .receiverPhone(request.getReceiverPhone())
                .note(request.getNote())
                .paymentMethod(request.getPaymentMethod().toUpperCase())
                .paymentStatus("UNPAID")
                .orderStatus("PENDING")
                .subtotal(subtotal)
                .shippingFee(shippingFee)
                .totalAmount(totalAmount)
                .build();

        FoodOrder savedOrder = foodOrderRepository.save(order);

        // Convert cart items to order items
        List<FoodOrderItem> orderItems = cartItems.stream()
                .map(cartItem -> FoodOrderItem.builder()
                        .foodOrder(savedOrder)
                        .food(cartItem.getFood())
                        .quantity(cartItem.getQuantity())
                        .unitPrice(cartItem.getUnitPrice())
                        .lineTotal(cartItem.getQuantity() * cartItem.getUnitPrice())
                        .build())
                .collect(Collectors.toList());

        savedOrder.setOrderItems(orderItems);
        foodOrderRepository.save(savedOrder);

        // Clear user's cart
        cartItemRepository.deleteByUser(user);
        log.info("Order created. Order ID: {}, Code: {}, Total: {}", savedOrder.getId(), savedOrder.getOrderCode(), totalAmount);

        // Process payment integration URL generation
        String paymentUrl = null;
        String payMethod = request.getPaymentMethod().toUpperCase();

        if ("MOMO".equals(payMethod)) {
            CreateMoMoResponse response = momoService.createPayment(Math.round(totalAmount), "FoodOrder#" + savedOrder.getId(), "ORDER", savedOrder.getId());
            if (response != null) {
                paymentUrl = response.getPayUrl();
            }
        } else if ("VNPAY".equals(payMethod)) {
            paymentUrl = vnPayService.createPayment(Math.round(totalAmount), "FoodOrder#" + savedOrder.getId());
        }

        return CheckoutResponse.builder()
                .orderId(savedOrder.getId())
                .orderCode(savedOrder.getOrderCode())
                .paymentUrl(paymentUrl)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FoodOrderResponse> getMyOrders(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<FoodOrder> orders = foodOrderRepository.findByUserOrderByCreatedAtDesc(user);
        return orders.stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FoodOrderResponse getOrderDetails(Long userId, Long orderId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to view this order");
        }

        return mapToOrderResponse(order);
    }

    @Override
    @Transactional
    public FoodOrderResponse cancelOrder(Long userId, Long orderId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to cancel this order");
        }

        String status = order.getOrderStatus().toUpperCase();
        if (!"PENDING".equals(status) && !"CONFIRMED".equals(status)) {
            throw new IllegalStateException("Orders cannot be cancelled once food preparation has started");
        }

        order.setOrderStatus("CANCELLED");
        FoodOrder saved = foodOrderRepository.save(order);
        log.info("Order cancelled. Order ID: {}", orderId);

        return mapToOrderResponse(saved);
    }

    // --- ADMIN ORDER MANAGEMENT ---

    @Override
    @Transactional(readOnly = true)
    public Page<FoodOrderResponse> getAllOrdersForAdmin(
            String status,
            String paymentStatus,
            String query,
            int page,
            int size,
            String sort
    ) {
        Sort sorting = Sort.by(Sort.Direction.DESC, "createdAt");
        if (sort != null && !sort.trim().isEmpty()) {
            String[] parts = sort.split("_");
            if (parts.length == 2) {
                Sort.Direction direction = "asc".equalsIgnoreCase(parts[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
                sorting = Sort.by(direction, parts[0]);
            }
        }

        Pageable pageable = PageRequest.of(page, size, sorting);
        Page<FoodOrder> pageResult = foodOrderRepository.searchOrders(
                status != null && !status.trim().isEmpty() ? status.toUpperCase() : null,
                paymentStatus != null && !paymentStatus.trim().isEmpty() ? paymentStatus.toUpperCase() : null,
                query != null && !query.trim().isEmpty() ? query : null,
                pageable
        );

        return pageResult.map(this::mapToOrderResponse);
    }

    @Override
    @Transactional
    public FoodOrderResponse updateOrderStatusForAdmin(Long orderId, String newStatus) {
        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        String oldStatus = order.getOrderStatus();
        order.setOrderStatus(newStatus.toUpperCase());
        FoodOrder saved = foodOrderRepository.save(order);

        log.info("Order status changed. Order ID: {}, Old Status: {}, New Status: {}", orderId, oldStatus, newStatus.toUpperCase());
        return mapToOrderResponse(saved);
    }

    // --- IPN CALLBACK PROCESSING ---

    @Override
    @Transactional
    public void processOrderPaymentIPN(Long orderId, String paymentMethod, double amount) {
        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if ("PAID".equals(order.getPaymentStatus())) {
            log.info("Order ID: {} is already paid. Skipping duplicate IPN processing.", orderId);
            return;
        }

        order.setPaymentStatus("PAID");
        order.setOrderStatus("CONFIRMED");
        foodOrderRepository.save(order);

        log.info("Payment received. Order ID: {}, Method: {}, Amount: {}", orderId, paymentMethod, amount);
        log.info("Order status changed. Order ID: {}, New Status: CONFIRMED", orderId);
    }

    @Override
    @Transactional
    public void updatePaymentStatus(Long orderId, String paymentStatus, String transId) {
        FoodOrder order = foodOrderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        if ("PAID".equals(order.getPaymentStatus())) {
            log.info("Order ID: {} is already paid. Skipping duplicate payment status update.", orderId);
            return;
        }

        order.setPaymentStatus(paymentStatus);
        order.setOrderStatus("CONFIRMED");
        foodOrderRepository.save(order);

        log.info("Payment updated. Order ID: {}, Status: {}, transId: {}", orderId, paymentStatus, transId);
    }

    // --- HELPER MAPPINGS ---


    private Long foodId(Long foodId) {
        if (foodId == null) {
            throw new IllegalArgumentException("Food ID must not be null");
        }
        return foodId;
    }

    private FoodDTO mapToFoodDTO(FoodEntity food) {
        if (food == null) return null;
        return FoodDTO.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .price(food.getPrice())
                .imageUrl(food.getImageUrl())
                .build();
    }

    private CartItemResponse mapToCartItemResponse(CartItem item) {
        if (item == null) return null;
        return CartItemResponse.builder()
                .id(item.getId())
                .food(mapToFoodDTO(item.getFood()))
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getQuantity() * item.getUnitPrice())
                .build();
    }

    private FoodOrderResponse mapToOrderResponse(FoodOrder order) {
        if (order == null) return null;

        List<FoodOrderItemResponse> items = new ArrayList<>();
        if (order.getOrderItems() != null) {
            items = order.getOrderItems().stream()
                    .map(item -> FoodOrderItemResponse.builder()
                            .id(item.getId())
                            .food(mapToFoodDTO(item.getFood()))
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .lineTotal(item.getLineTotal())
                            .build())
                    .collect(Collectors.toList());
        }

        return FoodOrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .userId(order.getUser().getId())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .deliveryAddress(order.getDeliveryAddress())
                .note(order.getNote())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .subtotal(order.getSubtotal())
                .shippingFee(order.getShippingFee())
                .totalAmount(order.getTotalAmount())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .orderItems(items)
                .build();
    }
}
