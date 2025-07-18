package com.restaurant.management.respository;

import com.restaurant.management.models.ReservationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    Optional<ReservationEntity> findByIdAndIsDeletedFalse(Long id);
    List<ReservationEntity> findAllByIsDeletedFalse();

    @Query("SELECT r FROM ReservationEntity r WHERE r.customer.id = :customerId AND r.isDeleted = false")
    List<ReservationEntity> findByCustomerId(@Param("customerId") Long customerId);

    @Query("SELECT r FROM ReservationEntity r " +
            "WHERE r.table.id = :tableId " +
            "AND r.reservationDate = :date " +
            "AND r.id <> :currentId " +
            "AND r.isDeleted = false")
    List<ReservationEntity> findAllByTableAndDate(
            @Param("tableId") Long tableId,
            @Param("date") LocalDate date,
            @Param("currentId") Long currentId
    );

    List<ReservationEntity> findAllByReservationDate(LocalDate date);


}