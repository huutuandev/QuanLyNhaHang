package com.restaurant.management.controller;


import com.restaurant.management.DTO.ReservationDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.responses.UnavailableTableResponse;
import com.restaurant.management.service.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/reservations")
@RequiredArgsConstructor
@Validated
public class ReservationController {
    private final IReservationService reservationService;

    @PostMapping
    public ResponseEntity<ReservationDTO> createOrUpdate(@AuthenticationPrincipal UserDTO user, @Valid @RequestBody ReservationDTO dto) {
        return ResponseEntity.ok(reservationService.createOrUpdate(user, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservationDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(reservationService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> cancelById(@PathVariable Long id) {
        reservationService.cancel(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<PagedResponse<ReservationDTO>> getAllByUser(@AuthenticationPrincipal UserDTO user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<ReservationDTO> reservationDTOPage = reservationService.getAllByUser(user.getId(), page, size);
        return ResponseEntity.ok(new PagedResponse<>(reservationDTOPage, reservationDTOPage.getContent()));
    }


    @GetMapping
    public ResponseEntity<PagedResponse<ReservationDTO>> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        Page<ReservationDTO> reservationDTOPage = reservationService.getAllReservations(page,size);
        return ResponseEntity.ok(new PagedResponse<>(reservationDTOPage, reservationDTOPage.getContent()));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ReservationDTO> updateStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        String status = body.get("status");
        ReservationDTO updated = reservationService.updateStatus(id, status);
        return ResponseEntity.ok(updated);
    }
}
