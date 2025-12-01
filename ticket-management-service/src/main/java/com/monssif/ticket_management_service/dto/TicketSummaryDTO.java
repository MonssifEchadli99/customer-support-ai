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
public class TicketSummaryDTO {
    private Long id;
    private String title;
    private Long customerId;
    private String customerName;
    private Long assignedAgentId;
    private String assignedAgentName;
    private String categoryName;
    private String priorityName;
    private Integer priorityLevel;
    private String statusName;
    private Double aiSentimentScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isAssigned;
    private Boolean isResolved;
}
