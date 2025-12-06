package com.monssif.ai_intelligence_service.service;

import com.monssif.ai_intelligence_service.dto.SentimentAnalysisResult;
import com.monssif.ai_intelligence_service.events.*;
import com.monssif.ai_intelligence_service.messaging.AiAnalysisProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAnalysisService {
    private final AiAnalysisProducer producer;
    private final GeminiService geminiService;

    public void processTicketCreated(TicketCreatedEvent event) {
        log.info("Processing ticket created event for ticket ID: {}", event.getTicketId());

        try{
            SentimentAnalysisResult analysis = this.geminiService.analyzeTicketSentiment
                    (event.getTitle(),event.getDescription(),event.getCategoryName());

            // TODO: Store in Redis
            AIAnalysisResultEvent resultEvent = AIAnalysisResultEvent.builder()
                    .ticketId(event.getTicketId())
                    .sentimentScore(analysis.getSentimentScore())
                    .suggestedCategoryName(analysis.getSuggestedCategory())
                    .analyzedAt(LocalDateTime.now())
                    .eventType("AI_ANALYSIS_RESULT")
                    .build();
            producer.publishAnalysisResult(resultEvent);
            log.info("Published analysis result for created ticket ID: {}", event.getTicketId());
        } catch (Exception e) {
            log.error("Error analyzing ticket {}: {}", event.getTicketId(), e.getMessage(), e);
            sendDefaultResult(event.getTicketId(), null);
        }
    }

    public void processTicketUpdated(TicketUpdatedEvent event) {
        log.info("Processing ticket updated event for ticket ID: {}", event.getTicketId());

        try {
            SentimentAnalysisResult analysis = geminiService.analyzeTicketSentiment(
                    event.getTitle(),
                    event.getDescription(),
                    null
            );
        // TODO: Update Redis
        AIAnalysisResultEvent resultEvent = AIAnalysisResultEvent.builder()
                .ticketId(event.getTicketId())
                .sentimentScore(analysis.getSentimentScore())
                .suggestedCategoryName(analysis.getSuggestedCategory())
                .analyzedAt(LocalDateTime.now())
                .eventType("AI_ANALYSIS_RESULT")
                .build();
        producer.publishAnalysisResult(resultEvent);
        log.info("Published analysis result for updated ticket ID: {}", event.getTicketId());
        } catch (Exception e) {
            log.error("Error analyzing updated ticket {}: {}", event.getTicketId(), e.getMessage(), e);
            sendDefaultResult(event.getTicketId(), null);
        }
    }

    public void processCommentCreated(CommentCreatedEvent event) {
        log.info("Processing comment created event for comment ID: {} on ticket ID: {}",
                event.getCommentId(), event.getTicketId());
        try{
            SentimentAnalysisResult analysis = this.geminiService.analyzeCommentSentiment(event.getContent());
            // TODO: Store in Redis
            AIAnalysisResultEvent resultEvent = AIAnalysisResultEvent.builder()
                    .commentId(event.getCommentId())
                    .ticketId(event.getTicketId())
                    .sentimentScore(analysis.getSentimentScore())
                    .analyzedAt(LocalDateTime.now())
                    .eventType("AI_ANALYSIS_RESULT")
                    .build();

            producer.publishAnalysisResult(resultEvent);
            log.info("Published analysis result for comment ID: {}", event.getCommentId());

        } catch (Exception e) {
            log.error("Error analyzing comment {}: {}", event.getCommentId(), e.getMessage(), e);
            sendDefaultResult(null, event.getCommentId());
        }
    }

    private void sendDefaultResult(Long ticketId, Long commentId) {
        AIAnalysisResultEvent resultEvent = AIAnalysisResultEvent.builder()
                .ticketId(ticketId)
                .commentId(commentId)
                .sentimentScore(0.0)
                .analyzedAt(LocalDateTime.now())
                .eventType("AI_ANALYSIS_RESULT")
                .build();

        producer.publishAnalysisResult(resultEvent);
        log.warn("Sent default analysis result for ticket: {}, comment: {}", ticketId, commentId);
    }
}