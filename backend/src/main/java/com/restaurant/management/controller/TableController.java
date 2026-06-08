package com.restaurant.management.controller;

import com.restaurant.management.dto.TableDTO;
import com.restaurant.management.responses.ApiResponse;
import com.restaurant.management.responses.PagedResponse;
import com.restaurant.management.service.ITableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("api/tables")
@RequiredArgsConstructor
@Validated
@Tag(name = "Tables", description = "Endpoints for managing restaurant dining tables")
public class TableController {
    private final ITableService tableService;

    @GetMapping
    @Operation(summary = "Get all tables", description = "Retrieves a paginated list of all dining tables.")
    public ResponseEntity<ApiResponse<PagedResponse<TableDTO>>> getAllTables(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size){
        log.info("Fetching dining tables: Page: {}, Size: {}", page, size);
        Page<TableDTO> tableDTOPage = tableService.getAllTables(page, size);
        PagedResponse<TableDTO> pagedResponse = new PagedResponse<>(tableDTOPage, tableDTOPage.getContent());
        return ResponseEntity.ok(ApiResponse.success("Tables retrieved successfully", pagedResponse));
    }

    @PostMapping
    @Operation(summary = "Create or update table", description = "Creates a new dining table or updates an existing table's capacity/number.")
    public ResponseEntity<ApiResponse<String>> createOrUpdateTable(@Valid @RequestBody TableDTO tableDTO){
        log.info("Creating/updating dining table number: {}", tableDTO.getTableNumber());
        tableService.createOrUpdateTable(tableDTO);
        return ResponseEntity.ok(ApiResponse.success("Thành Công", "Thành Công"));
    }
}
