package com.monssif.ai_intelligence_service.messaging;

import com.monssif.ai_intelligence_service.events.AIAnalysisResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiAnalysisProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.ai-analysis-result}")
    private String aiAnalysisResultTopic;

    public void publishAnalysisResult(AIAnalysisResultEvent event){
        String key = event.getTicketId() != null ?
                event.getTicketId().toString() :
                event.getCommentId().toString();

        log.info("Publishing AIAnalysisResultEvent with key: {}", key);
        kafkaTemplate.send(aiAnalysisResultTopic,key,event);
    }
}
