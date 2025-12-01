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
public class CommentResponseDTO {

    private Long id;
    private String content;

    private Long ticketId;
    private String ticketTitle;

    private Long userId;
    private String userName;
    private String userEmail;
    private String userRole;

    private Boolean isInternal;
    private Boolean isPublic;

    private Double aiSentimentScore;
    private Boolean hasSentimentAnalysis;

    private LocalDateTime createdAt;

    private String visibility;
}