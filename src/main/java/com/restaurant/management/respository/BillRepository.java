package com.restaurant.management.respository;

import com.restaurant.management.models.BillEntity;
import com.restaurant.management.models.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<BillEntity, Long> {
    List<BillEntity> findByCashierId(Long cashierId);
    boolean existsByOrder(OrderEntity order);

}
