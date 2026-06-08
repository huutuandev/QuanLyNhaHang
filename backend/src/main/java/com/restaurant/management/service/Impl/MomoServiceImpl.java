package com.restaurant.management.service.Impl;

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

    public CreateMoMoResponse createPayment(long amount, String orderInfo) throws Exception {
        String orderId = UUID.randomUUID().toString();
        String requestId = UUID.randomUUID().toString();
        String extraData = "";

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                ACCESS_KEY, amount, extraData, IPN_URL, orderId, orderInfo, PARTNER_CODE, REDIRECT_URL, requestId, REQUEST_TYPE
        );

        String signature;
        try {
            signature = signHmacSHA256(rawSignature, SECRET_KEY);
        } catch (Exception e) {
            log.error("Lỗi khi ký chữ ký MoMo", e);
            return null;
        }

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

            HttpEntity<CreateMoMoRequest> httpRequest = new HttpEntity<>(request, headers);

            ResponseEntity<CreateMoMoResponse> response = restTemplate.exchange(
                    END_POINT + "/create", HttpMethod.POST, httpRequest, CreateMoMoResponse.class
            );

            return response.getBody();
        } catch (Exception e) {
            log.error("Lỗi khi gọi API MoMo", e);
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
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    @Override
    public boolean verifySignature(Map<String, String> params, String secureHash) throws Exception {
        String amount = params.get("amount");
        String extraData = params.getOrDefault("extraData", "");
        String message = params.get("message");
        String orderId = params.get("orderId");
        String orderInfo = params.get("orderInfo");
        String partnerCode = params.get("partnerCode");
        String requestId = params.get("requestId");
        String responseTime = params.get("responseTime");
        String resultCode = params.get("resultCode");
        String transId = params.get("transId");

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&message=%s&orderId=%s&orderInfo=%s&partnerCode=%s&requestId=%s&responseTime=%s&resultCode=%s&transId=%s",
                ACCESS_KEY, amount, extraData, message, orderId, orderInfo, partnerCode, requestId, responseTime, resultCode, transId
        );

        String secureHashCheck = signHmacSHA256(rawSignature, SECRET_KEY);
        return secureHashCheck != null && secureHashCheck.equalsIgnoreCase(secureHash);
    }
}
