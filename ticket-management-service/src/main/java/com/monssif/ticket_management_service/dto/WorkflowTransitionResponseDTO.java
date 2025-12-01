package com.monssif.ticket_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowTransitionResponseDTO {

    private Long ticketId;
    private String ticketTitle;

    private String previousStatusName;
    private Long previousStatusId;

    private String newStatusName;
    private Long newStatusId;
    private Boolean isStatusFinal;

    private String transitionComment;
    private String changedByUserName;
    private Long changedByUserId;
    private LocalDateTime transitionedAt;
    private LocalDateTime resolvedAt;

    private Boolean isResolved;
    private String message;
}