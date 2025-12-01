package com.monssif.ticket_management_service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIAnalysisResultEvent {
    private Long ticketId;
    private Long commentId;
    private Double sentimentScore;
    private Long suggestedCategoryId;
    private String suggestedCategoryName;
    private LocalDateTime analyzedAt;
    private String eventType;
}