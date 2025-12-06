package com.monssif.ai_intelligence_service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.types.GenerateContentResponse;
import com.monssif.ai_intelligence_service.dto.SentimentAnalysisResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class GeminiService {

    private final ObjectMapper objectMapper;

    private final ChatClient chatClient;

    public GeminiService(ObjectMapper objectMapper, ChatClient.Builder builder) {
        this.objectMapper = objectMapper;
        this.chatClient = builder.build();
    }

    public ChatResponse exempleMethod(){
        return chatClient.prompt()
                .user("Tell me what are SOLID Principles")
                .call()
                .chatResponse();
    }

    public SentimentAnalysisResult analyzeTicketSentiment(String title, String description, String category) {
        log.info("Analyzing ticket sentiment with Gemini API");

        String prompt = buildTicketAnalysisPrompt(title, description, category);
        String geminiResponse = callGeminiApi(prompt);

        return parseTicketAnalysisResponse(geminiResponse);
    }

    public SentimentAnalysisResult analyzeCommentSentiment(String content) {
        log.info("Analyzing comment sentiment with Gemini API");

        String prompt = buildCommentAnalysisPrompt(content);
        String geminiResponse = callGeminiApi(prompt);

        return parseCommentAnalysisResponse(geminiResponse);
    }

    private String buildTicketAnalysisPrompt(String title, String description, String currentCategory) {
        return String.format("""
            Analyze the following customer support ticket and provide:
            1. Sentiment score (between -1.0 for very negative and 1.0 for very positive)
            2. Suggested category from these options: "Technical Support", "Billing", "Account Management", "Product Inquiry", "Feature Request", "General"
            
            Ticket Title: %s
            Ticket Description: %s
            Current Category: %s
            
            Respond ONLY in this JSON format (no additional text):
            {
              "sentimentScore": <number between -1.0 and 1.0>,
              "suggestedCategory": "<one of the categories listed above>"
            }
            """, title, description, currentCategory);
    }

    private String buildCommentAnalysisPrompt(String content) {
        return String.format("""
            Analyze the sentiment of this customer support comment.
            Provide a sentiment score between -1.0 (very negative) and 1.0 (very positive).
            
            Comment: %s
            
            Respond ONLY in this JSON format (no additional text):
            {
              "sentimentScore": <number between -1.0 and 1.0>
            }
            """, content);
    }

    private String callGeminiApi(String prompt) {
        try {
            String response = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            log.info("Gemini API response: {}", response);
            return response;

        } catch (Exception e) {
            log.error("Error calling Gemini API: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to analyze with Gemini API", e);
        }
    }

    private SentimentAnalysisResult parseTicketAnalysisResponse(String response) {
        try {
            String jsonString = extractJson(response);
            Map<String, Object> result = objectMapper.readValue(jsonString, Map.class);

            Double sentimentScore = parseDouble(result.get("sentimentScore"));
            String suggestedCategory = (String) result.get("suggestedCategory");

            return SentimentAnalysisResult.builder()
                    .sentimentScore(sentimentScore)
                    .suggestedCategory(suggestedCategory)
                    .build();

        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage(), e);
            return SentimentAnalysisResult.builder()
                    .sentimentScore(0.0)
                    .suggestedCategory(null)
                    .build();
        }
    }

    private SentimentAnalysisResult parseCommentAnalysisResponse(String response) {
        try {
            String jsonString = extractJson(response);
            Map<String, Object> result = objectMapper.readValue(jsonString, Map.class);

            Double sentimentScore = parseDouble(result.get("sentimentScore"));

            return SentimentAnalysisResult.builder()
                    .sentimentScore(sentimentScore)
                    .build();

        } catch (Exception e) {
            log.error("Error parsing Gemini response: {}", e.getMessage(), e);
            return SentimentAnalysisResult.builder()
                    .sentimentScore(0.0)
                    .build();
        }
    }

    private String extractJson(String response) {
        if (response == null) {
            return "{}";
        }

        Pattern pattern = Pattern.compile("\\{[^}]+\\}");
        Matcher matcher = pattern.matcher(response);

        if (matcher.find()) {
            return matcher.group();
        }

        return response.trim();
    }

    private Double parseDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (NumberFormatException e) {
            log.warn("Could not parse sentiment score: {}", value);
            return 0.0;
        }
    }
}
