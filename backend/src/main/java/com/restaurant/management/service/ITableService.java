package com.restaurant.management.service;

import com.restaurant.management.dto.TableDTO;
import com.restaurant.management.models.TableEntity;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ITableService {
    Page<TableDTO> getAllTables(int page, int size);
    TableDTO createOrUpdateTable(TableDTO dto);
    List<TableEntity> getAvailableTables(LocalDate date, Long excludeReservationId);

}
