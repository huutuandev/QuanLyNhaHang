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

    public String createPayment(long amount, String orderInfo) throws Exception {
        String vnp_TxnRef = String.valueOf(System.currentTimeMillis());
        String vnp_IpAddr = "127.0.0.1";

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        String vnp_CreateDate = new SimpleDateFormat("yyyyMMddHHmmss").format(cld.getTime());

        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", vnp_Version);
        params.put("vnp_Command", vnp_Command);
        params.put("vnp_TmnCode", vnp_TmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100)); // nhân 100
        params.put("vnp_CurrCode", vnp_CurrCode);
        params.put("vnp_TxnRef", vnp_TxnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", vnp_Locale);
        params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        params.put("vnp_IpAddr", vnp_IpAddr);
        params.put("vnp_CreateDate", vnp_CreateDate);

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (int i = 0; i < fieldNames.size(); i++) {
            String fieldName = fieldNames.get(i);
            String fieldValue = params.get(fieldName);

            String encodedValue = URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.name());

            hashData.append(fieldName).append('=').append(encodedValue);

            query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.name()))
                    .append('=')
                    .append(encodedValue);

            if (i < fieldNames.size() - 1) {
                hashData.append('&');
                query.append('&');
            }
        }

        String secureHash = hmacSHA512(vnp_HashSecret, hashData.toString());

        String paymentUrl = vnp_PayUrl + "?" + query.toString()
                + "&vnp_SecureHashType=HmacSHA512&vnp_SecureHash=" + secureHash;

        return paymentUrl;
    }



//    @Override
//    public boolean processIPN(Map<String, String> params) {
//        try {
//            String vnp_SecureHash = params.remove("vnp_SecureHash");
//
//            List<String> fieldNames = new ArrayList<>(params.keySet());
//            Collections.sort(fieldNames);
//            StringBuilder hashData = new StringBuilder();
//            for (int i = 0; i < fieldNames.size(); i++) {
//                String fieldName = fieldNames.get(i);
//                String fieldValue = params.get(fieldName);
//                hashData.append(fieldName).append("=").append(fieldValue);
//                if (i < fieldNames.size() - 1) {
//                    hashData.append("&");
//                }
//            }
//
//            String secureHashCheck = hmacSHA512(vnp_HashSecret, hashData.toString());
//            if (!secureHashCheck.equals(vnp_SecureHash)) {
//                return false;
//            }
//
//            if ("00".equals(params.get("vnp_ResponseCode"))) {
//                // ✅ Thanh toán thành công → cập nhật đơn hàng
//                String orderId = params.get("vnp_TxnRef");
//                // orderService.updateStatus(orderId, "PAID");
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }


    private String hmacSHA512(String key, String data) throws Exception {
        if (key == null || data == null) return null;
        Mac hmac512 = Mac.getInstance("HmacSHA512");
        SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        hmac512.init(secretKey);
        byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hash.append('0');
            hash.append(hex);
        }
        return hash.toString();
    }

}
