package com.monssif.ticket_management_service.messaging;

import com.monssif.ticket_management_service.events.AIAnalysisResultEvent;
import com.monssif.ticket_management_service.service.CommentService;
import com.monssif.ticket_management_service.service.TicketService;
import org.springframework.kafka.annotation.KafkaListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class AIAnalysisConsumer {

    private final TicketService ticketService;
    private final CommentService commentService;

    @KafkaListener(
            topics = "${kafka.topics.ai-analysis-result}",
            groupId  = "${spring.kafka.consumer.group-id}"
    )
    public void consumeAiAnalysisResult(AIAnalysisResultEvent event){
        log.info("Received AI analysis Result {} ", event);

        try{
            if(Objects.nonNull(event.getCommentId())){
                commentService.updateCommentWithAISentiment(
                        event.getCommentId(),
                        event.getSentimentScore()
                );
            } else if(Objects.nonNull(event.getTicketId())){
                ticketService.updateTicketWithAIAnalysis(
                        event.getTicketId(),
                        event.getSentimentScore(),
                        event.getSuggestedCategoryId());
            }
            log.info("Successfully processed AI analysis result");
        } catch(Exception e){
            log.error("Error processing AI analysis result: {}", e.getMessage(), e);
        }
    }
}
