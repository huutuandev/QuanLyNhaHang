package com.restaurant.management.controller;

import com.restaurant.management.DTO.TableDTO;
import com.restaurant.management.service.ITableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/tables")
@RequiredArgsConstructor
@Validated
public class TableController {
    private final ITableService tableService;

    @GetMapping
    public ResponseEntity<List<TableDTO>> getAllTables(){
        List<TableDTO> tableDTOS = tableService.getAllTables();
        return ResponseEntity.ok(tableDTOS);
    }
}
