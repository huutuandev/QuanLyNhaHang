package com.restaurant.management;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Test {
    public static void main(String[] args) throws Exception {
        String key = "DRW1PGDY3HVZ711ZW9PTD7UOU9ZXCSU0L2"; // ← thay vào đây
        String data = "vnp_Amount=20000000&vnp_Command=pay&vnp_CreateDate=20260609104432&vnp_CurrCode=VND&vnp_ExpireDate=20260609105932&vnp_IpAddr=127.0.0.1&vnp_Locale=vn&vnp_OrderInfo=TEST&vnp_OrderType=billpayment&vnp_ReturnUrl=http://localhost:3000/payment-success&vnp_TmnCode=HBC00V1H&vnp_TxnRef=1780976672383&vnp_Version=2.1.0";

        Mac hmac512 = Mac.getInstance("HmacSHA512");
        hmac512.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));

        StringBuilder hash = new StringBuilder();
        for (byte b : bytes) hash.append(String.format("%02x", b));

        System.out.println("Length: " + hash.length());
        System.out.println("Calculated: " + hash);
        System.out.println("Expected:   5b9667e0fb3e4e461bf34108f5908a5e4f35f5ab2eec8656ca31e7065bc8d3ebd167f019a951f575bde5d03c7c72f8cb7126e9ece95ebca420cd3df8d2a38f5d");
        System.out.println("Match: " + hash.toString().equals("5b9667e0fb3e4e461bf34108f5908a5e4f35f5ab2eec8656ca31e7065bc8d3ebd167f019a951f575bde5d03c7c72f8cb7126e9ece95ebca420cd3df8d2a38f5d"));
    }
}
