package com.monssif.ai_intelligence_service.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketCreatedEvent {
    private Long ticketId;
    private String title;
    private String description;
    private String categoryName;
    private String priorityName;
    private Long customerId;
    private LocalDateTime createdAt;
    private String eventType;
}