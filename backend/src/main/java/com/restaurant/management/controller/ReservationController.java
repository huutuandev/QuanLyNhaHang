package com.restaurant.management.controller;

import com.restaurant.management.dto.ReservationDTO;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.IReservationService;
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
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("api/reservations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Reservations", description = "Endpoints for booking, modifying, and canceling table reservations")
public class ReservationController {
    private final IReservationService reservationService;

    @PostMapping
    @Operation(summary = "Create or update reservation", description = "Creates a new table booking reservation or updates an existing one.")
    public ResponseEntity<ApiResponse<ReservationDTO>> createOrUpdate(@AuthenticationPrincipal UserDTO user, @Valid @RequestBody ReservationDTO dto) {
        log.info("Creating or updating reservation for customer: {}", (user != null ? user.getPhoneNumber() : "Anonymous"));
        ReservationDTO result = reservationService.createOrUpdate(user, dto);
        return ResponseEntity.ok(ApiResponse.success("Reservation saved successfully", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get reservation by ID", description = "Retrieves the details of a specific table reservation by its ID.")
    public ResponseEntity<ApiResponse<ReservationDTO>> getById(@PathVariable Long id) {
        log.info("Fetching reservation details with ID: {}", id);
        ReservationDTO reservationDTO = reservationService.getById(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation retrieved successfully", reservationDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cancel reservation", description = "Cancels a booking reservation by its ID.")
    public ResponseEntity<ApiResponse<Void>> cancelById(@PathVariable Long id) {
        log.info("Canceling Reservation ID: {}", id);
        reservationService.cancel(id);
        return ResponseEntity.ok(ApiResponse.success("Reservation canceled successfully", null));
    }

    @GetMapping("/my")
    @Operation(summary = "Get current user's reservations", description = "Retrieves a paginated list of reservations belonging to the authenticated customer.")
    public ResponseEntity<ApiResponse<PagedResponse<ReservationDTO>>> getAllByUser(
            @AuthenticationPrincipal UserDTO user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        log.info("Fetching reservations for user ID: {}, Page: {}, Size: {}", user.getId(), page, size);
        Page<ReservationDTO> reservationDTOPage = reservationService.getAllByUser(user.getId(), page, size);
        PagedResponse<ReservationDTO> pagedResponse = new PagedResponse<>(reservationDTOPage, reservationDTOPage.getContent());
        return ResponseEntity.ok(ApiResponse.success("My reservations retrieved successfully", pagedResponse));
    }

    @GetMapping
    @Operation(summary = "Get all reservations", description = "Retrieves a paginated list of all reservations in the system (Staff/Admin only).")
    public ResponseEntity<ApiResponse<PagedResponse<ReservationDTO>>> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        log.info("Fetching all reservations: Page: {}, Size: {}", page, size);
        Page<ReservationDTO> reservationDTOPage = reservationService.getAllReservations(page, size);
        PagedResponse<ReservationDTO> pagedResponse = new PagedResponse<>(reservationDTOPage, reservationDTOPage.getContent());
        return ResponseEntity.ok(ApiResponse.success("All reservations retrieved successfully", pagedResponse));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update reservation status", description = "Updates the status of a booking reservation (Staff/Admin only).")
    public ResponseEntity<ApiResponse<ReservationDTO>> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String status = body.get("status");
        log.info("Updating status of Reservation ID: {} to {}", id, status);
        ReservationDTO updated = reservationService.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Reservation status updated successfully", updated));
    }
}
