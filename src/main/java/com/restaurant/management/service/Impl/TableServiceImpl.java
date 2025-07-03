package com.restaurant.management.service.Impl;

import com.restaurant.management.DTO.TableDTO;
import com.restaurant.management.models.TableEntity;
import com.restaurant.management.respository.TableRepository;
import com.restaurant.management.service.ITableService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements ITableService {

    private final TableRepository tableRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<TableDTO> getAllTables() {
        List<TableEntity> tableEntities = tableRepository.findAll();

        return tableEntities.stream()
                .map(entity -> modelMapper.map(entity,TableDTO.class))
                .collect(Collectors.toList());
    }
}
