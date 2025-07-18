package com.restaurant.management.service;

import com.restaurant.management.DTO.TableDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ITableService {
    Page<TableDTO> getAllTables(int page, int size);
    TableDTO createOrUpdateTable(TableDTO dto);


}
