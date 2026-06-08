package com.restaurant.management.service;

import com.restaurant.management.responses.CreateMoMoResponse;
import java.util.Map;

public interface IMoMoService {
    CreateMoMoResponse createPayment(long amount, String orderInfo) throws Exception;
    boolean verifySignature(Map<String, String> params, String secureHash) throws Exception;
}
