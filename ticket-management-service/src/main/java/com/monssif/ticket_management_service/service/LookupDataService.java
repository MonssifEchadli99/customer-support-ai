package com.monssif.ticket_management_service.service;

import com.monssif.ticket_management_service.dto.*;
import com.monssif.ticket_management_service.entity.Category;
import com.monssif.ticket_management_service.entity.Priority;
import com.monssif.ticket_management_service.entity.Status;
import com.monssif.ticket_management_service.mapper.CategoryMapper;
import com.monssif.ticket_management_service.mapper.PriorityMapper;
import com.monssif.ticket_management_service.mapper.StatusMapper;
import com.monssif.ticket_management_service.repository.CategoryRepository;
import com.monssif.ticket_management_service.repository.PriorityRepository;
import com.monssif.ticket_management_service.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class LookupDataService {

    private final CategoryRepository categoryRepository;
    private final PriorityRepository priorityRepository;
    private final StatusRepository statusRepository;

    private final CategoryMapper categoryMapper;
    private final PriorityMapper priorityMapper;
    private final StatusMapper statusMapper;


    public List<CategoryDTO> getAllCategories(boolean includeInactive) {
        log.info("Fetching all categories - includeInactive: {}", includeInactive);

        List<Category> categories = includeInactive
                ? categoryRepository.findAll()
                : categoryRepository.findAllActive();

        return categories.stream()
                .map(category -> {
                    CategoryDTO dto = categoryMapper.toDTO(category);
                    dto.setTicketCount(categoryRepository.countTicketsByCategoryId(category.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public List<PriorityDTO> getAllPriorities() {
        log.info("Fetching all priorities ordered by level");

        List<Priority> priorities = priorityRepository.findAllOrderedByLevel();

        return priorities.stream()
                .map(priority -> {
                    PriorityDTO dto = priorityMapper.toDTO(priority);
                    dto.setTicketCount(priorityRepository.countTicketsByPriorityId(priority.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }


    public List<StatusDTO> getAllStatuses(String filter) {
        log.info("Fetching all statuses - filter: {}", filter);

        List<Status> statuses;

        if ("final".equalsIgnoreCase(filter)) {
            statuses = statusRepository.findAllFinalStatuses();
        } else if ("active".equalsIgnoreCase(filter)) {
            statuses = statusRepository.findAllActiveStatuses();
        } else {
            statuses = statusRepository.findAll();
        }

        return statuses.stream()
                .map(status -> {
                    StatusDTO dto = statusMapper.toDTO(status);
                    dto.setTicketCount(statusRepository.countTicketsByStatusId(status.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public LookupDataSummaryDTO getAllLookupData() {
        log.info("Fetching all lookup data in single call");

        List<CategoryDTO> categories = getAllCategories(false);
        List<PriorityDTO> priorities = getAllPriorities();
        List<StatusDTO> statuses = getAllStatuses(null);

        return LookupDataSummaryDTO.builder()
                .categories(categories)
                .priorities(priorities)
                .statuses(statuses)
                .totalCategories(categories.size())
                .totalPriorities(priorities.size())
                .totalStatuses(statuses.size())
                .build();
    }

    public CategoryDTO getCategoryById(Long id) {
        log.info("Fetching category with ID: {}", id);

        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found with ID: " + id));

        CategoryDTO dto = categoryMapper.toDTO(category);
        dto.setTicketCount(categoryRepository.countTicketsByCategoryId(id));
        return dto;
    }

    public PriorityDTO getPriorityById(Long id) {
        log.info("Fetching priority with ID: {}", id);

        Priority priority = priorityRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Priority not found with ID: " + id));

        PriorityDTO dto = priorityMapper.toDTO(priority);
        dto.setTicketCount(priorityRepository.countTicketsByPriorityId(id));
        return dto;
    }

    public StatusDTO getStatusById(Long id) {
        log.info("Fetching status with ID: {}", id);

        Status status = statusRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Status not found with ID: " + id));

        StatusDTO dto = statusMapper.toDTO(status);
        dto.setTicketCount(statusRepository.countTicketsByStatusId(id));
        return dto;
    }
}