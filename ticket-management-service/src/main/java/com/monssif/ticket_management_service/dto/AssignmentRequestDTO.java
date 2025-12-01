package com.monssif.ticket_management_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentRequestDTO {

    @NotNull(message = "Agent ID is required")
    @Positive(message = "Agent ID must be positive")
    private Long agentId;

    private String note;
}