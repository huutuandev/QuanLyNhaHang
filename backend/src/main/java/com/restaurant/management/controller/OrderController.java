package com.restaurant.management.controller;

import com.restaurant.management.DTO.OrderDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("api/orders")
@RequiredArgsConstructor
@Validated
public class OrderController {
    private final IOrderService orderService;

    @GetMapping
    public ResponseEntity<PagedResponse<OrderDTO>> getAllOrder(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ){
        Page<OrderDTO> orderDTOPage = orderService.getAllOrder(page, size);
        return ResponseEntity.ok(new PagedResponse<>(orderDTOPage, orderDTOPage.getContent()));
    }

    @PostMapping
    public ResponseEntity<OrderDTO> createOrUpdate(
            @Valid @RequestBody OrderDTO orderDTO,
            @AuthenticationPrincipal UserDTO userDTO
    ){
        return ResponseEntity.ok(orderService.createOrUpdate(userDTO, orderDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> cancelById(@PathVariable Long id){
        orderService.cancel(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getById(@PathVariable Long id){
        OrderDTO orderDTO = orderService.getById(id);
        return ResponseEntity.ok(orderDTO);
    }
}
