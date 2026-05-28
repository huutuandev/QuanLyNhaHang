package com.restaurant.management.respository;

import com.restaurant.management.models.TableEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRepository extends JpaRepository<TableEntity,Long> {
}
