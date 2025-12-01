package com.monssif.ticket_management_service.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowTransitionRequestDTO {
    @NotNull(message = "Status ID is required")
    @Positive(message = "Status ID must be positive")
    private Long statusId;

    @Size(max = 1000, message = "Transition comment must not exceed 1000 characters")
    private String comment;

    @Positive(message = "Changed by user ID must be positive")
    private Long changedByUserId;
}
