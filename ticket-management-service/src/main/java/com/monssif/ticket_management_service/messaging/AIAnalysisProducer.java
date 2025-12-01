package com.monssif.ticket_management_service.messaging;

import com.monssif.ticket_management_service.events.CommentCreatedEvent;
import com.monssif.ticket_management_service.events.TicketCreatedEvent;
import com.monssif.ticket_management_service.events.TicketUpdatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAnalysisProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.ticket-created}")
    private String ticketCreatedTopic;

    @Value("${kafka.topics.ticket-updated}")
    private String ticketUpdatedTopic;

    @Value("${kafka.topics.comment-created}")
    private String commentCreatedTopic;

    public void publishTicketCreated(TicketCreatedEvent event) {
        log.info("Publishing TicketCreatedEvent for ticket ID: {}", event.getTicketId());
        kafkaTemplate.send(ticketCreatedTopic, event.getTicketId().toString(), event);
    }

    public void publishTicketUpdated(TicketUpdatedEvent event) {
        log.info("Publishing TicketUpdatedEvent for ticket ID: {}", event.getTicketId());
        kafkaTemplate.send(ticketUpdatedTopic, event.getTicketId().toString(), event);
    }

    public void publishCommentCreated(CommentCreatedEvent event) {
        log.info("Publishing CommentCreatedEvent for comment ID: {}", event.getCommentId());
        kafkaTemplate.send(commentCreatedTopic, event.getCommentId().toString(), event);
    }
}