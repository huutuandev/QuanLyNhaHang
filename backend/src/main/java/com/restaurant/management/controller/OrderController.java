package com.restaurant.management.controller;

import com.restaurant.management.dto.OrderDTO;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
@Validated
@Tag(name = "Orders", description = "Endpoints for placing and managing customer food orders")
public class OrderController {
    private final IOrderService orderService;

    @GetMapping
    @Operation(summary = "Get all orders", description = "Retrieves a paginated list of all customer food orders.")
    public ResponseEntity<ApiResponse<PagedResponse<OrderDTO>>> getAllOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        log.info("Fetching all orders: Page: {}, Size: {}", page, size);
        Page<OrderDTO> orderDTOPage = orderService.getAllOrder(page, size);
        PagedResponse<OrderDTO> pagedResponse = new PagedResponse<>(orderDTOPage, orderDTOPage.getContent());
        return ResponseEntity.ok(ApiResponse.success("Orders retrieved successfully", pagedResponse));
    }

    @PostMapping
    @Operation(summary = "Create or update order", description = "Submits a new food order or updates an existing order.")
    public ResponseEntity<ApiResponse<OrderDTO>> createOrUpdate(
            @Valid @RequestBody OrderDTO orderDTO,
            @AuthenticationPrincipal UserDTO userDTO
    ){
        log.info("Creating/updating order for User ID: {}", userDTO.getId());
        OrderDTO result = orderService.createOrUpdate(userDTO, orderDTO);
        return ResponseEntity.ok(ApiResponse.success("Order processed successfully", result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cancel order", description = "Cancels an order by its ID.")
    public ResponseEntity<ApiResponse<Void>> cancelById(@PathVariable Long id){
        log.info("Canceling Order ID: {}", id);
        orderService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Order canceled successfully", null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get order by ID", description = "Retrieves details of a specific food order by its ID.")
    public ResponseEntity<ApiResponse<OrderDTO>> getById(@PathVariable Long id){
        log.info("Fetching details for Order ID: {}", id);
        OrderDTO orderDTO = orderService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Order retrieved successfully", orderDTO));
    }
}
