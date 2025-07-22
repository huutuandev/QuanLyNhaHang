package com.restaurant.management.controller;

import com.restaurant.management.DTO.TableDTO;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.ITableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("api/tables")
@RequiredArgsConstructor
@Validated
public class TableController {
    private final ITableService tableService;

    @GetMapping
    public ResponseEntity<PagedResponse<TableDTO>> getAllTables(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){

        Page<TableDTO> tableDTOPage = tableService.getAllTables(page,size);
        return ResponseEntity.ok(new PagedResponse<>(tableDTOPage,tableDTOPage.getContent()));
    }
    @PostMapping
    public ResponseEntity<?> createOrUpdateTable(@Valid @RequestBody TableDTO tableDTO){
        tableService.createOrUpdateTable(tableDTO);
        return ResponseEntity.ok("Thành Công");
    }
}
