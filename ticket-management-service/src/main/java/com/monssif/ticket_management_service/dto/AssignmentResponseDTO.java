package com.monssif.ticket_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentResponseDTO {

    private Long ticketId;
    private String ticketTitle;

    private Long currentAgentId;
    private String currentAgentName;
    private String currentAgentEmail;

    private Long previousAgentId;
    private String previousAgentName;

    private LocalDateTime assignedAt;
    private String note;
    private String message;
}