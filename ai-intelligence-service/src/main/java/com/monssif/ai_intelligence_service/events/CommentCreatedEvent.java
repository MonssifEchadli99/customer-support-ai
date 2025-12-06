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
public class CommentCreatedEvent {
    private Long commentId;
    private Long ticketId;
    private String content;
    private Boolean isInternal;
    private Long userId;
    private LocalDateTime createdAt;
    private String eventType;
}