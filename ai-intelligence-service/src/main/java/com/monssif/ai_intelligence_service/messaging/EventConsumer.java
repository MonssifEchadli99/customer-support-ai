package com.monssif.ai_intelligence_service.messaging;

import com.monssif.ai_intelligence_service.events.CommentCreatedEvent;
import com.monssif.ai_intelligence_service.events.TicketCreatedEvent;
import com.monssif.ai_intelligence_service.events.TicketUpdatedEvent;
import com.monssif.ai_intelligence_service.service.AIAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventConsumer {
    private final AIAnalysisService aiAnalysisService;

    @KafkaListener(
            topics = "${kafka.topics.ticket-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeTicketCreated(TicketCreatedEvent event){
        log.info("Received TicketCreatedEvent {}",event);
        try{
            aiAnalysisService.processTicketCreated(event);
            log.info("Successfully processed ticket created event for ticket ID: {}",event.getTicketId());
        }catch(Exception e){
            log.error("Error processing TicketCreatedEvent: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.ticket-updated}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeTicketUpdated(TicketUpdatedEvent event){
        log.info("Received TicketUpdatedEvent: {}", event);
        try{
            aiAnalysisService.processTicketUpdated(event);
            log.info("Successfully processed ticket updated event for ticket ID: {}", event.getTicketId());
        }catch(Exception e){
            log.error("Error processing TicketUpdatedEvent: {}", e.getMessage(), e);
        }
    }


    @KafkaListener(
            topics = "${kafka.topics.comment-created}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeCommentCreated(CommentCreatedEvent event) {
        log.info("Received CommentCreatedEvent: {}", event);
        try {
            aiAnalysisService.processCommentCreated(event);
            log.info("Successfully processed comment created event for comment ID: {}", event.getCommentId());
        } catch (Exception e) {
            log.error("Error processing CommentCreatedEvent: {}", e.getMessage(), e);
        }
    }
}
