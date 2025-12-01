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
public class TicketResponseDTO {
    private Long id;
    private String title;
    private String description;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Long assignedAgentId;
    private String assignedAgentName;
    private String assignedAgentEmail;
    private Long categoryId;
    private String categoryName;
    private Long priorityId;
    private String priorityName;
    private Integer priorityLevel;
    private Long statusId;
    private String statusName;
    private Boolean isStatusFinal;
    private Double aiSentimentScore;
    private Long aiSuggestedCategoryId;
    private String aiSuggestedCategoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime resolvedAt;
    private Boolean isAssigned;
    private Boolean isResolved;
    private Integer commentsCount;
    private Integer attachmentsCount;
}
