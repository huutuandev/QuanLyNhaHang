package com.restaurant.management.respository;

import com.restaurant.management.models.BillEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BillRepository extends JpaRepository<BillEntity, Long> {
    List<BillEntity> findByCashierId(Long cashierId);
}
