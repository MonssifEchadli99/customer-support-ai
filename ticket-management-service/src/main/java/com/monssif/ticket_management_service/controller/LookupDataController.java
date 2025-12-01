package com.monssif.ticket_management_service.controller;

import com.monssif.ticket_management_service.dto.*;
import com.monssif.ticket_management_service.service.LookupDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lookup")
@RequiredArgsConstructor
@Slf4j
public class LookupDataController {

    private final LookupDataService lookupDataService;

    @GetMapping("/all")
    public ResponseEntity<LookupDataSummaryDTO> getAllLookupData() {
        log.info("REST request to get all lookup data");
        LookupDataSummaryDTO lookupData = lookupDataService.getAllLookupData();
        return ResponseEntity.ok(lookupData);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories(
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        log.info("REST request to get all categories - includeInactive: {}", includeInactive);
        List<CategoryDTO> categories = lookupDataService.getAllCategories(includeInactive);
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        log.info("REST request to get category by ID: {}", id);
        CategoryDTO category = lookupDataService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    @GetMapping("/priorities")
    public ResponseEntity<List<PriorityDTO>> getAllPriorities() {
        log.info("REST request to get all priorities");
        List<PriorityDTO> priorities = lookupDataService.getAllPriorities();
        return ResponseEntity.ok(priorities);
    }

    @GetMapping("/priorities/{id}")
    public ResponseEntity<PriorityDTO> getPriorityById(@PathVariable Long id) {
        log.info("REST request to get priority by ID: {}", id);
        PriorityDTO priority = lookupDataService.getPriorityById(id);
        return ResponseEntity.ok(priority);
    }

    @GetMapping("/statuses")
    public ResponseEntity<List<StatusDTO>> getAllStatuses(
            @RequestParam(required = false) String filter) {
        log.info("REST request to get all statuses - filter: {}", filter);
        List<StatusDTO> statuses = lookupDataService.getAllStatuses(filter);
        return ResponseEntity.ok(statuses);
    }

    @GetMapping("/statuses/{id}")
    public ResponseEntity<StatusDTO> getStatusById(@PathVariable Long id) {
        log.info("REST request to get status by ID: {}", id);
        StatusDTO status = lookupDataService.getStatusById(id);
        return ResponseEntity.ok(status);
    }
}
