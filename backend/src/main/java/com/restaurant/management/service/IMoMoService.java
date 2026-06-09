package com.restaurant.management.service;

import com.restaurant.management.responses.CreateMoMoResponse;
import java.util.Map;

public interface IMoMoService {
    CreateMoMoResponse createPayment(long amount, String orderInfo,
                                     String paymentType, Long referenceId) throws Exception;
    Map<String, Object> parseExtraData(String extraDataEncoded);
    boolean verifySignature(Map<String, String> params, String secureHash) throws Exception;
}
