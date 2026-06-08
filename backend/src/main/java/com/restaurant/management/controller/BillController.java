package com.restaurant.management.controller;

import com.restaurant.management.dto.BillDTO;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.requests.ConfirmPaymentRequest;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.service.IBillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("api/bills")
@RequiredArgsConstructor
@Validated
@Tag(name = "Bills", description = "Endpoints for cashier billing operations")
public class BillController {
    private final IBillService billService;

    @GetMapping
    @Operation(summary = "Get all bills by user", description = "Retrieves a list of all bills processed by the authenticated cashier.")
    public ResponseEntity<ApiResponse<List<BillDTO>>> getAllBillByUser(@AuthenticationPrincipal UserDTO userDTO){
        log.info("Fetching all bills for cashier ID: {}", userDTO.getId());
        List<BillDTO> billDTOS = billService.getAllBillByUser(userDTO);
        return ResponseEntity.ok(ApiResponse.success("Bills retrieved successfully", billDTOS));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get bill by ID", description = "Retrieves a specific bill details by its ID.")
    public ResponseEntity<ApiResponse<BillDTO>> getBillById(@PathVariable Long id){
        log.info("Fetching bill details with ID: {}", id);
        BillDTO billDTO = billService.getBillById(id);
        return ResponseEntity.ok(ApiResponse.success("Bill retrieved successfully", billDTO));
    }

    @PostMapping
    @Operation(summary = "Create a new bill", description = "Creates a billing record for an order or a booking reservation.")
    public ResponseEntity<ApiResponse<BillDTO>> createBill(@Valid @RequestBody BillDTO billDTO, @AuthenticationPrincipal UserDTO userDTO){
        log.info("Creating a new bill by cashier ID: {}", userDTO.getId());
        BillDTO createdBill = billService.createBill(billDTO, userDTO);
        return ResponseEntity.ok(ApiResponse.success("Bill created successfully", createdBill));
    }

    @PutMapping("/confirm-payment")
    @Operation(summary = "Confirm bill payment", description = "Manually confirms and registers payment for a reservation deposit.")
    public ResponseEntity<ApiResponse<BillDTO>> confirmPayment(@RequestBody ConfirmPaymentRequest confirmPaymentRequest) {
        log.info("Manually confirming payment for Reservation ID: {}", confirmPaymentRequest.getReservationId());
        BillDTO confirmedBill = billService.confirmPayment(confirmPaymentRequest);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed successfully", confirmedBill));
    }
}
