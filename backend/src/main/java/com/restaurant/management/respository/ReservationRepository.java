package com.restaurant.management.respository;

import com.restaurant.management.responses.StatusCountResponse;
import com.restaurant.management.models.ReservationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<ReservationEntity, Long> {
    Optional<ReservationEntity> findByIdAndIsDeletedFalse(Long id);

    @Query("SELECT r FROM ReservationEntity r WHERE r.customer.id = :customerId AND r.isDeleted = false")
    Page<ReservationEntity> findByCustomerId(@Param("customerId") Long customerId, Pageable pageable);

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
    @Query("SELECT new com.restaurant.management.responses.StatusCountResponse(r.status, COUNT(r)) " +
            "FROM ReservationEntity r " +
            "WHERE r.isDeleted = false AND FUNCTION('YEAR', r.reservationDate) = :year " +
            "GROUP BY r.status")
    List<StatusCountResponse> countByYear(@Param("year") int year);

    @Query(
            "SELECT new com.restaurant.management.responses.StatusCountResponse(" +
                    "        r.status, COUNT(r) ) " +
                    "FROM ReservationEntity r " +
                    "WHERE r.isDeleted = false " +
                    "  AND FUNCTION('YEAR',  r.reservationDate) = :year " +
                    "  AND FUNCTION('MONTH', r.reservationDate) = :month " +
                    "GROUP BY r.status"
    )
    List<StatusCountResponse> countByMonth(@Param("year") int year,
                                           @Param("month") int month);
    // ReservationRepository.java
    @Query("SELECT r.table.id FROM ReservationEntity r " +
            "WHERE r.reservationDate = :date " +
            "AND (:excludeId IS NULL OR r.id != :excludeId)")
    List<Long> findOccupiedTableIdsByDate(@Param("date") LocalDate date,
                                          @Param("excludeId") Long excludeId);

    Page<ReservationEntity> findAllByIsDeletedFalse(Pageable pageable);
    List<ReservationEntity> findAllByCustomerIdAndReservationDate(Long customerId, LocalDate reservationDate);

    @Query("SELECT r FROM ReservationEntity r " +
            "WHERE r.reservationDate = :date " +
            "AND r.isDeleted = false " +
            "AND r.table.id = :tableId")
    List<ReservationEntity> findByTableIdAndReservationDate(Long tableId, LocalDate date);
}