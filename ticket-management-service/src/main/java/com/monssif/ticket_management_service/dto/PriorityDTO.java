package com.monssif.ticket_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriorityDTO {
    private Long id;
    private String name;
    private Integer level;
    private String description;
    private Long ticketCount;
}