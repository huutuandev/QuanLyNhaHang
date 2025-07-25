package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.TableDTO;
import com.restaurant.management.models.TableEntity;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.respository.ReservationRepository;
import com.restaurant.management.respository.TableRepository;
import com.restaurant.management.service.ITableService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements ITableService {

    private final TableRepository tableRepository;
    private final ReservationRepository reservationRepo;
    private final ModelMapper modelMapper;

    @Override
    public Page<TableDTO> getAllTables(int page, int size ) {
        Pageable pageable = PageRequest.of(page,size);
        Page<TableEntity> tableEntities = tableRepository.findAll(pageable);
        return tableEntities.map(tableEntity -> modelMapper.map(tableEntity,TableDTO.class));
    }

    @Override
    public TableDTO createOrUpdateTable(TableDTO dto) {
        TableEntity table;
        if (dto.getId() != null) {
            table = tableRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn với ID: " + dto.getId()));
            modelMapper.map(dto, table);
        } else {
            table = modelMapper.map(dto, TableEntity.class);
        }
        if (table.getStatus() == null) {
            table.setStatus("Available");
        }
        TableEntity saved = tableRepository.save(table);
        return modelMapper.map(saved, TableDTO.class);
    }

    // TableServiceImpl.java
    @Override
    public List<TableEntity> getAvailableTables(LocalDate date, Long excludeReservationId) {
        List<TableEntity> allTables = tableRepository.findAll();
        List<Long> occupiedTableIds = reservationRepo.findOccupiedTableIdsByDate(date, excludeReservationId);
        return allTables.stream()
                .filter(table -> !occupiedTableIds.contains(table.getId()))
                .collect(Collectors.toList());
    }
}
