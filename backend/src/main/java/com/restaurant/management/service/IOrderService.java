package com.restaurant.management.service;

import com.restaurant.management.dto.OrderDTO;
import com.restaurant.management.dto.UserDTO;
import org.springframework.data.domain.Page;

public interface IOrderService {
    OrderDTO createOrUpdate(UserDTO userDTO, OrderDTO orderDTO);
    Page<OrderDTO> getAllOrder(int page, int size);
    OrderDTO getById(Long id);
    void cancel(Long id);
}
