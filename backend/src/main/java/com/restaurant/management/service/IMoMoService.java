package com.restaurant.management.service;

import com.restaurant.management.responses.CreateMoMoResponse;

public interface IMoMoService {
    CreateMoMoResponse createPayment(long amount, String orderInfo) throws Exception;
}
