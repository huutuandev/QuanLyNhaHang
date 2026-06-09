package com.restaurant.management.service.Impl;

import com.restaurant.management.dto.OrderDTO;
import com.restaurant.management.dto.OrderItemDTO;
import com.restaurant.management.dto.UserDTO;
import com.restaurant.management.constant.OrderStatusConstant;
import com.restaurant.management.customexceptions.ResourceNotFoundException;
import com.restaurant.management.models.OrderEntity;
import com.restaurant.management.models.OrderItemEntity;
import com.restaurant.management.respository.FoodRepository;
import com.restaurant.management.respository.OrderRepository;
import com.restaurant.management.respository.TableRepository;
import com.restaurant.management.respository.UserRepository;
import com.restaurant.management.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final OrderRepository orderRepo;
    private final UserRepository userRepo;
    private final TableRepository tableRepo;
    private final FoodRepository foodRepo;
    private final ModelMapper modelMapper;

    @Override
    public OrderDTO createOrUpdate(Long userId, OrderDTO orderDTO) {
        OrderEntity order = orderDTO.getId() != null
                ? orderRepo.findById(orderDTO.getId())
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderDTO.getId()))
                : new OrderEntity();
        mapDTOToEntity(orderDTO, order);
        order.setStaff(userRepo.findById(orderDTO.getStaffId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + userId)));
        order.setTable(tableRepo.findById(orderDTO.getTableId())
                .orElseThrow(() -> new RuntimeException("Table not found with id: " + orderDTO.getTableId())));
        setOrderItems(orderDTO, order);
        OrderEntity saved = orderRepo.save(order);
        return modelMapper.map(saved, OrderDTO.class);
    }

    @Override
    public Page<OrderDTO> getAllOrder(int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Page<OrderEntity> orderEntityPage = orderRepo.findAll(pageable);
        return orderEntityPage.map(orderEntity -> modelMapper.map(orderEntity, OrderDTO.class));
    }

    @Override
    public OrderDTO getById(Long id) {
        OrderEntity order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lỗi không phân định"));
        return modelMapper.map(order, OrderDTO.class);
    }

    @Override
    public void cancel(Long id) {
        OrderEntity order = orderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Lỗi không phân định"));
        order.setStatus(OrderStatusConstant.CANCELLED);
        orderRepo.save(order);
    }
    private void mapDTOToEntity(OrderDTO dto, OrderEntity entity){
        entity.setStatus(dto.getStatus());
        if (dto.getId() == null) {
            entity.setCreatedAt(LocalDateTime.now());
            entity.setOrderItems(new ArrayList<>());
        }
    }
    private void setOrderItems(OrderDTO dto, OrderEntity entity){
        entity.getOrderItems().clear();
        if(dto.getOrderItems() != null){
            for (OrderItemDTO orderItemDTO : dto.getOrderItems()){
                OrderItemEntity orderItem = OrderItemEntity.builder()
                        .order(entity)
                        .food(foodRepo.findById(orderItemDTO.getFoodId())
                                .orElseThrow(() -> new RuntimeException("Food not found with id: " + orderItemDTO.getFoodId())))
                        .quantity(orderItemDTO.getQuantity())
                        .note(orderItemDTO.getNote())
                        .status(orderItemDTO.getStatus())
                        .build();
                entity.getOrderItems().add(orderItem);
            }
        }
    }
}
