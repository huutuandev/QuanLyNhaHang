package com.restaurant.management.service;

import java.util.Map;

public interface IVnPayService {
    String createPayment(long amount, String orderInfo) throws Exception;
    boolean processIPN(Map<String, String> params);
}
