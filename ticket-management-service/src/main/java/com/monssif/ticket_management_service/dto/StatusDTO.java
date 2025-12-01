package com.monssif.ticket_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatusDTO {
    private Long id;
    private String name;
    private String description;
    private Boolean isFinal;
    private Long ticketCount;
}