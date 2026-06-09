package com.restaurant.management.controller;

import com.restaurant.management.requests.CreatePaymentRequest;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.CreateMoMoResponse;
import com.restaurant.management.service.IBillService;
import com.restaurant.management.service.IMoMoService;
import com.restaurant.management.service.IVnPayService;
import com.restaurant.management.service.IReservationService;
import com.restaurant.management.service.IFoodOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/payment")
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Endpoints for creating payments and handling payment webhook IPNs")
public class PaymentController {

    private final IMoMoService momoService;
    private final IVnPayService vnPayService;
    private final IBillService billService;
    private final IReservationService reservationService;
    private final IFoodOrderService orderService;

    @PostMapping("/momo")
    @Operation(summary = "Create MoMo Payment Link", description = "Generates a MoMo payment URL for deposit/bill payment.")
    public ResponseEntity<ApiResponse<String>> createMoMoPayment(@RequestBody CreatePaymentRequest dto) throws Exception {
        log.info("Request to create MoMo payment for amount: {}", dto.getAmount());
        CreateMoMoResponse response = momoService.createPayment(dto.getAmount(), dto.getOrderInfo(), dto.getPaymentType(), dto.getReferenceId());
        if (response != null && response.getPayUrl() != null) {
            log.info("MoMo payment URL created successfully");
            return ResponseEntity.ok(ApiResponse.success("MoMo payment created", response.getPayUrl()));
        } else {
            log.error("Failed to create MoMo payment link");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Tạo thanh toán MoMo thất bại"));
        }
    }

    @PostMapping("/vnpay")
    @Operation(summary = "Create VNPAY Payment Link", description = "Generates a VNPAY payment URL for deposit/bill payment.")
    public ResponseEntity<ApiResponse<String>> createVnPayPayment(@RequestBody CreatePaymentRequest dto) throws Exception {
        log.info("Request to create VNPAY payment for amount: {}", dto.getAmount());
        String payUrl = vnPayService.createPayment(dto.getAmount(), dto.getOrderInfo());
        if (payUrl != null) {
            log.info("VNPAY payment URL created successfully");
            return ResponseEntity.ok(ApiResponse.success("VNPAY payment created", payUrl));
        } else {
            log.error("Failed to create VNPAY payment link");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Tạo thanh toán VNPAY thất bại"));
        }
    }

    @RequestMapping(value = "/vnpay/ipn", method = {RequestMethod.GET, RequestMethod.POST})
    @Operation(summary = "VNPAY Instant Payment Notification (IPN) Hook", description = "Called asynchronously by VNPAY to update payment transaction status.")
    public ResponseEntity<Map<String, String>> processVnPayIPN(@RequestParam Map<String, String> params) {
        log.info("Received VNPAY IPN notification: {}", params);
        Map<String, String> response = new HashMap<>();

        try {
            String secureHash = params.get("vnp_SecureHash");
            if (secureHash == null || !vnPayService.verifySignature(params, secureHash)) {
                log.warn("VNPAY IPN signature verification failed");
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return ResponseEntity.ok(response);
            }

            String orderInfo = params.get("vnp_OrderInfo");
            if (orderInfo != null && orderInfo.startsWith("FoodOrder#")) {
                Long foodOrderId = Long.parseLong(orderInfo.substring("FoodOrder#".length()).trim());
                String responseCode = params.get("vnp_ResponseCode");
                if ("00".equals(responseCode)) {
                    double amount = Double.parseDouble(params.get("vnp_Amount")) / 100.0;
                    orderService.processOrderPaymentIPN(foodOrderId, "VNPAY", amount);
                    log.info("VNPAY IPN processed successfully for Food Order ID: {}", foodOrderId);
                }
                response.put("RspCode", "00");
                response.put("Message", "Confirm success");
                return ResponseEntity.ok(response);
            }

            int hashIndex = orderInfo != null ? orderInfo.indexOf('#') : -1;
            if (hashIndex == -1) {
                log.warn("VNPAY IPN failed: Reservation ID not found in orderInfo: {}", orderInfo);
                response.put("RspCode", "01");
                response.put("Message", "Order not found");
                return ResponseEntity.ok(response);
            }

            Long reservationId = Long.parseLong(orderInfo.substring(hashIndex + 1).trim());
            String responseCode = params.get("vnp_ResponseCode");

            if ("00".equals(responseCode)) {
                double amount = Double.parseDouble(params.get("vnp_Amount")) / 100.0;
                billService.processPaymentIPN(reservationId, "VNPAY", amount);
                log.info("VNPAY IPN processed successfully for Reservation ID: {}", reservationId);
                response.put("RspCode", "00");
                response.put("Message", "Confirm success");
            } else {
                log.warn("VNPAY payment unsuccessful. Response code: {}", responseCode);
                response.put("RspCode", "00");
                response.put("Message", "Confirm success (payment failed status registered)");
            }

        } catch (Exception e) {
            log.error("Error processing VNPAY IPN: ", e);
            response.put("RspCode", "99");
            response.put("Message", "Input required data invalid");
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/momo/ipn")
    public ResponseEntity<?> momoIpn(@RequestBody Map<String, String> params) {
        log.info("Received MoMo IPN: {}", params);

        String resultCode = params.get("resultCode");
        String extraDataEncoded = params.get("extraData");
        String transId = params.get("transId");
        String amount = params.get("amount");

        // Bỏ qua verify chữ ký trong môi trường test (bật lại khi production)
        // String signature = params.get("signature");
        // if (!momoService.verifySignature(params, signature)) {
        //     log.warn("MoMo IPN invalid signature");
        //     return ResponseEntity.ok(Map.of("status", "success")); // vẫn trả 200 cho MoMo
        // }

        if (!"0".equals(resultCode)) {
            log.warn("MoMo payment FAILED - resultCode: {}, message: {}", resultCode, params.get("message"));
            return ResponseEntity.ok(Map.of("status", "success"));
        }

        // Parse extraData để biết loại thanh toán
        Map<String, Object> extraData = momoService.parseExtraData(extraDataEncoded);
        if (extraData == null) {
            log.error("Không parse được extraData: {}", extraDataEncoded);
            return ResponseEntity.ok(Map.of("status", "success"));
        }

        String type = (String) extraData.get("type");
        Long referenceId = Long.valueOf(extraData.get("id").toString());

        log.info("MoMo payment SUCCESS - type: {}, id: {}, transId: {}, amount: {}",
                type, referenceId, transId, amount);

        if ("RESERVATION".equals(type)) {
            // Cập nhật trạng thái đặt bàn → đã thanh toán
            reservationService.updatePaymentStatus(referenceId, "PAID", transId);

        } else if ("ORDER".equals(type)) {
            // Cập nhật trạng thái đơn đồ ăn → đã thanh toán
            orderService.updatePaymentStatus(referenceId, "PAID", transId);
        }

        return ResponseEntity.ok(Map.of("status", "success"));
    }
}
