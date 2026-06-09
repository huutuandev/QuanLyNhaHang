package com.restaurant.management.controller;

import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.FoodOrderResponse;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IFoodOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/orders")
@RequiredArgsConstructor
@Tag(name = "Admin Orders", description = "Endpoints for administrator management of takeaway/delivery orders")
public class AdminOrderController {

    private final IFoodOrderService foodOrderService;

    @GetMapping
    @Operation(summary = "Get admin orders", description = "Retrieves a paginated, filtered, and sorted list of all delivery orders for administration.")
    public ResponseEntity<ApiResponse<PagedResponse<FoodOrderResponse>>> getOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String paymentStatus,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt_desc") String sort
    ) {
        log.info("Admin fetching delivery orders - status: {}, paymentStatus: {}, query: {}, page: {}, size: {}, sort: {}",
                status, paymentStatus, q, page, size, sort);
        Page<FoodOrderResponse> ordersPage = foodOrderService.getAllOrdersForAdmin(status, paymentStatus, q, page, size, sort);
        PagedResponse<FoodOrderResponse> response = new PagedResponse<>(ordersPage, ordersPage.getContent());
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", response));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update order status", description = "Updates the status of a delivery order (e.g. CONFIRMED, PREPARING, DELIVERING, COMPLETED, CANCELLED).")
    public ResponseEntity<ApiResponse<FoodOrderResponse>> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status
    ) {
        log.info("Admin updating status of Order ID: {} to: {}", id, status);
        FoodOrderResponse response = foodOrderService.updateOrderStatusForAdmin(id, status);
        return ResponseEntity.ok(ApiResponse.success("Order status updated successfully", response));
    }
}
