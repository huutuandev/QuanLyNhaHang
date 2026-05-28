package com.restaurant.management.controller;

import com.restaurant.management.DTO.BillDTO;
import com.restaurant.management.DTO.UserDTO;
import com.restaurant.management.requests.ConfirmPaymentRequest;
import com.restaurant.management.service.IBillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/bills")
@RequiredArgsConstructor
@Validated
public class BillController {
    private final IBillService billService;

    @GetMapping
    public ResponseEntity<List<BillDTO>> getAllBillByUser(@AuthenticationPrincipal UserDTO userDTO){
        List<BillDTO> billDTOS = billService.getAllBillByUser(userDTO);
        return ResponseEntity.ok(billDTOS);
    }
    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long id){
        BillDTO billDTO = billService.getBillById(id);
        return ResponseEntity.ok(billDTO);
    }
    @PostMapping
    public ResponseEntity<BillDTO> createBill(@Valid @RequestBody BillDTO billDTO, @AuthenticationPrincipal UserDTO userDTO){
            return ResponseEntity.ok(billService.createBill(billDTO,userDTO));
    }
    @PutMapping("/confirm-payment")
    public ResponseEntity<BillDTO> confirmPayment(@RequestBody ConfirmPaymentRequest confirmPaymentRequest) {
        return ResponseEntity.ok(billService.confirmPayment(confirmPaymentRequest));
    }

}
