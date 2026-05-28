package com.restaurant.management.respository;

import com.restaurant.management.models.FoodEntity;
import com.restaurant.management.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<UserEntity> findByPhoneNumber(String phoneNumber);
    List<UserEntity> findAllByIsDeletedFalse();
    Optional<UserEntity> findByIdAndIsDeletedFalse(Long id);
    Optional<UserEntity> findFirstByRoles_RoleName(String roleName);
}
