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
public class TicketUpdatedEvent {
    private Long ticketId;
    private String title;
    private String description;
    private String statusName;
    private LocalDateTime updatedAt;
    private String eventType;
}