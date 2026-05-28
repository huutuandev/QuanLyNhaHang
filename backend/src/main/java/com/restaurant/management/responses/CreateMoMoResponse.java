package com.restaurant.management.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateMoMoResponse {
    private String partnerCode;
    private String orderId;
    private long amount;
    private String responseTime;
    private int resultCode;
    private String message;
    private String payUrl;
    private String deepLink;
    private String qrCodeUrl;
}