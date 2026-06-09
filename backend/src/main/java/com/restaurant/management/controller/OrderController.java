package com.restaurant.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.management.dto.OrderDTO;
import com.restaurant.management.security.CustomUserDetails;
import com.restaurant.management.requests.CheckoutRequest;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.CheckoutResponse;
import com.restaurant.management.responses.FoodOrderResponse;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IFoodOrderService;
import com.restaurant.management.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Orders", description = "Endpoints for placing and managing customer food orders and table orders")
public class OrderController {
    private final IOrderService orderService;
    private final IFoodOrderService foodOrderService;
    private final ObjectMapper objectMapper;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieves a paginated list of all customer table orders (Staff only).")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDTO>>> getAllOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        log.info("Fetching all table orders: Page: {}, Size: {}", page, size);
        Page<OrderDTO> orderDTOPage = orderService.getAllOrder(page, size);
        PagedResponse<OrderDTO> pagedResponse = new PagedResponse<>(orderDTOPage, orderDTOPage.getContent());
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", pagedResponse));
    }

    @PostMapping
    @Operation(summary = "Create order / Checkout", description = "Submits a new table order OR checkouts a takeaway/delivery order from the cart.")
    public ResponseEntity<ApiResponse<Object>> createOrder(
            @RequestBody String body,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) throws Exception {
        if (body.contains("receiverName") || body.contains("deliveryAddress")) {
            log.info("Processing delivery order checkout for User ID: {}", currentUser.getId());
            CheckoutRequest checkoutRequest = objectMapper.readValue(body, CheckoutRequest.class);
            CheckoutResponse response = foodOrderService.checkout(currentUser.getId(), checkoutRequest);
            return ResponseEntity.ok(ApiResponse.success("Order created successfully", response));
        } else {
            log.info("Processing table order for User ID: {}", currentUser.getId());
            OrderDTO orderDTO = objectMapper.readValue(body, OrderDTO.class);
            OrderDTO result = orderService.createOrUpdate(currentUser.getId(), orderDTO);
            return ResponseEntity.ok(ApiResponse.success("Order processed successfully", result));
        }
    }

    @GetMapping("/my")
    @Operation(summary = "Get my delivery orders", description = "Retrieves all delivery and takeaway orders placed by the current customer.")
    public ResponseEntity<ApiResponse<List<FoodOrderResponse>>> getMyOrders(
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Fetching delivery orders for User ID: {}", currentUser.getId());
        List<FoodOrderResponse> responses = foodOrderService.getMyOrders(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("My orders retrieved successfully", responses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieves details of a specific table order or delivery order by its ID.")
    public ResponseEntity<ApiResponse<Object>> getById(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Fetching details for Order ID: {}", id);
        try {
            // Try to find delivery order first
            FoodOrderResponse foodOrder = foodOrderService.getOrderDetails(currentUser.getId(), id);
            return ResponseEntity.ok(ApiResponse.success("Delivery order retrieved successfully", foodOrder));
        } catch (Exception e) {
            // Fall back to table order
            OrderDTO orderDTO = orderService.getById(id);
            return ResponseEntity.ok(ApiResponse.success("Table order retrieved successfully", orderDTO));
        }
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancel delivery order", description = "Cancels a delivery order before preparation starts.")
    public ResponseEntity<ApiResponse<FoodOrderResponse>> cancelDeliveryOrder(
            @PathVariable Long id,
            @Parameter(hidden = true) @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        log.info("Canceling Delivery Order ID: {}", id);
        FoodOrderResponse response = foodOrderService.cancelOrder(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Order cancelled successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cancel table order", description = "Cancels a table order by its ID.")
    public ResponseEntity<ApiResponse<Void>> cancelById(@PathVariable Long id){
        log.info("Canceling Table Order ID: {}", id);
        orderService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Table order canceled successfully", null));
    }
}
