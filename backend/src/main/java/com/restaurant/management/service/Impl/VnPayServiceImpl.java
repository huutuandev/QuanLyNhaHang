package com.restaurant.management.service.Impl;

import com.restaurant.management.service.IVnPayService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnPayServiceImpl implements IVnPayService {

    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret;

    @Value("${vnpay.payment-url}")
    private String vnp_PayUrl;

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;

    @Value("${vnpay.version}")
    private String vnp_Version;

    @Value("${vnpay.command}")
    private String vnp_Command;

    @Value("${vnpay.currency}")
    private String vnp_CurrCode;

    @Value("${vnpay.locale}")
    private String vnp_Locale;

    private static final TimeZone VN_TIMEZONE = TimeZone.getTimeZone("Asia/Ho_Chi_Minh");

    @Override
    public String createPayment(long amount, String orderInfo) throws Exception {
        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String vnp_IpAddr = "127.0.0.1";

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        formatter.setTimeZone(VN_TIMEZONE);

        // FIX: cả createDate và expireDate đều dùng cùng timezone
        Calendar now = Calendar.getInstance(VN_TIMEZONE);
        String vnp_CreateDate = formatter.format(now.getTime());

        Calendar expire = Calendar.getInstance(VN_TIMEZONE);
        expire.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(expire.getTime());



        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", vnp_Version);
        params.put("vnp_Command", vnp_Command);
        params.put("vnp_TmnCode", vnp_TmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100));
        params.put("vnp_CurrCode", vnp_CurrCode);
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "billpayment");
        params.put("vnp_Locale", vnp_Locale);
        params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        params.put("vnp_IpAddr", vnp_IpAddr);
        params.put("vnp_CreateDate", vnp_CreateDate);
        params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Sort theo alphabet — bắt buộc của VNPAY
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                // hashData: raw, KHÔNG encode
                if (hashData.length() > 0) hashData.append("&");
                hashData.append(fieldName).append("=").append(fieldValue);

                // query string: encode
                String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8)
                        .replace("+", "%20")
                        .replace("*", "%2A")
                        .replace("%7E", "~");
                if (query.length() > 0) query.append("&");
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8))
                        .append("=").append(encodedValue);
            }
        }

        String vnp_SecureHash = hmacSHA512(vnp_HashSecret, hashData.toString());

        String paymentUrl = vnp_PayUrl + "?" + query
                + "&vnp_SecureHashType=HmacSHA512&vnp_SecureHash=" + vnp_SecureHash;

        log.info("=== VNPAY CREATE PAYMENT ===");
        log.info("HashData: {}", hashData);
        log.info("SecureHash: {}", vnp_SecureHash);
        log.info("PaymentURL: {}", paymentUrl);

        return paymentUrl;
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKey);
        byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            hash.append(String.format("%02x", b));
        }
        return hash.toString();
    }

    @Override
    public boolean verifySignature(Map<String, String> params, String secureHash) throws Exception {
        if (secureHash == null || secureHash.isEmpty()) return false;

        Map<String, String> cleanParams = new HashMap<>(params);
        cleanParams.remove("vnp_SecureHash");
        cleanParams.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>();
        for (String key : cleanParams.keySet()) {
            if (key.startsWith("vnp_")) fieldNames.add(key);
        }
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        for (String fieldName : fieldNames) {
            String fieldValue = cleanParams.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                if (hashData.length() > 0) hashData.append("&");
                hashData.append(fieldName).append("=").append(fieldValue);
            }
        }

        String calculatedHash = hmacSHA512(vnp_HashSecret, hashData.toString());

        log.info("=== VNPAY VERIFY SIGNATURE ===");
        log.info("HashData: {}", hashData);
        log.info("Input SecureHash: {}", secureHash);
        log.info("Calculated Hash:  {}", calculatedHash);
        log.info("Match: {}", calculatedHash.equalsIgnoreCase(secureHash));

        return calculatedHash.equalsIgnoreCase(secureHash);
    }
}