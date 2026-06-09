package com.restaurant.management.service.Impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.management.requests.CreateMoMoRequest;
import com.restaurant.management.responses.CreateMoMoResponse;
import com.restaurant.management.service.IMoMoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoServiceImpl implements IMoMoService {

    @Value("${momo.partner-code}")
    private String PARTNER_CODE;

    @Value("${momo.access-key}")
    private String ACCESS_KEY;

    @Value("${momo.secret-key}")
    private String SECRET_KEY;

    @Value("${momo.return-url}")
    private String REDIRECT_URL;

    @Value("${momo.ipn-url}")
    private String IPN_URL;

    @Value("${momo.request-type}")
    private String REQUEST_TYPE;

    @Value("${momo.end-point}")
    private String END_POINT;

    private final ObjectMapper objectMapper;

    /**
     * @param amount        số tiền
     * @param orderInfo     mô tả đơn hàng
     * @param paymentType   "RESERVATION" hoặc "ORDER"
     * @param referenceId   id của reservation hoặc order trong DB
     */
    @Override
    public CreateMoMoResponse createPayment(long amount, String orderInfo,
                                            String paymentType, Long referenceId) throws Exception {
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();

        // Encode thông tin vào extraData để IPN biết cập nhật bảng nào
        Map<String, Object> extraMap = new HashMap<>();
        extraMap.put("type", paymentType);   // "RESERVATION" hoặc "ORDER"
        extraMap.put("id", referenceId);     // reservationId hoặc orderId trong DB
        String extraData = Base64.getEncoder().encodeToString(
                objectMapper.writeValueAsString(extraMap).getBytes(StandardCharsets.UTF_8)
        );

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                ACCESS_KEY, amount, extraData, IPN_URL, orderId, orderInfo,
                PARTNER_CODE, REDIRECT_URL, requestId, REQUEST_TYPE
        );

        String signature = signHmacSHA256(rawSignature, SECRET_KEY);

        log.info("=== MOMO CREATE PAYMENT ===");
        log.info("PaymentType: {}, ReferenceId: {}", paymentType, referenceId);
        log.info("ExtraData: {}", extraData);
        log.info("RawSignature: {}", rawSignature);

        CreateMoMoRequest request = CreateMoMoRequest.builder()
                .partnerCode(PARTNER_CODE)
                .requestType(REQUEST_TYPE)
                .ipnUrl(IPN_URL)
                .redirectUrl(REDIRECT_URL)
                .orderId(orderId)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .extraData(extraData)
                .amount(amount)
                .signature(signature)
                .lang("vi")
                .build();

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            ResponseEntity<CreateMoMoResponse> response = restTemplate.exchange(
                    END_POINT + "/create", HttpMethod.POST,
                    new HttpEntity<>(request, headers), CreateMoMoResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Lỗi khi gọi API MoMo", e);
            return null;
        }
    }

    /**
     * Parse extraData từ IPN để biết loại thanh toán và id tương ứng
     * return Map với keys: "type" (String) và "id" (Long)
     */
    @Override
    public Map<String, Object> parseExtraData(String extraDataEncoded) {
        try {
            if (extraDataEncoded == null || extraDataEncoded.isEmpty()) return null;
            String json = new String(Base64.getDecoder().decode(extraDataEncoded), StandardCharsets.UTF_8);
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.error("Không parse được extraData: {}", extraDataEncoded, e);
            return null;
        }
    }

    private String signHmacSHA256(String data, String key) throws Exception {
        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        hmacSHA256.init(secretKey);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    @Override
    public boolean verifySignature(Map<String, String> params, String secureHash) throws Exception {
        if (secureHash == null || secureHash.isEmpty()) return false;

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&partnerCode=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                ACCESS_KEY,
                params.get("amount"),
                params.getOrDefault("extraData", ""),
                params.getOrDefault("message", ""),
                params.get("orderId"),
                params.get("orderInfo"),
                params.get("partnerCode"),
                params.get("requestId"),
                params.get("responseTime"),
                params.get("resultCode"),
                params.get("transId")
        );

        String calculated = signHmacSHA256(rawSignature, SECRET_KEY);
        log.info("=== MOMO VERIFY ===");
        log.info("RawSignature: {}", rawSignature);
        log.info("Input:      {}", secureHash);
        log.info("Calculated: {}", calculated);
        log.info("Match: {}", calculated.equalsIgnoreCase(secureHash));

        return calculated.equalsIgnoreCase(secureHash);
    }
}