package com.monssif.ticket_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LookupDataSummaryDTO {
    private List<CategoryDTO> categories;
    private List<PriorityDTO> priorities;
    private List<StatusDTO> statuses;
    private Integer totalCategories;
    private Integer totalPriorities;
    private Integer totalStatuses;
}