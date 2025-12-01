package com.monssif.ticket_management_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Value("${kafka.topics.ticket-created}")
    private String ticketCreatedTopic;

    @Value("${kafka.topics.ticket-updated}")
    private String ticketUpdatedTopic;

    @Value("${kafka.topics.comment-created}")
    private String commentCreatedTopic;

    @Value("${kafka.topics.ai-analysis-result}")
    private String aiAnalysisResultTopic;

    @Bean
    public NewTopic ticketCreatedTopic(){
        return TopicBuilder.name(ticketCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic ticketUpdatedTopic() {
        return TopicBuilder.name(ticketUpdatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic commentCreatedTopic() {
        return TopicBuilder.name(commentCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic aiAnalysisResultTopic() {
        return TopicBuilder.name(aiAnalysisResultTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
