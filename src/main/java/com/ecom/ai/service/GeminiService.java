package com.ecom.ai.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.ecom.ai.intent.IntentDetector;
import com.ecom.ai.prompt.GeneralPrompt;
import com.ecom.ai.prompt.IdentityPrompt;
import com.ecom.ai.prompt.ShoppingPrompt;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ecom.ai.intent.IntentType;

@Service
public class GeminiService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final ProductContextService productContextService;
    private final IntentDetector intentDetector;

    @Value("${gemini.model}")
    private String model;

    public GeminiService(RestClient restClient,
                         ObjectMapper objectMapper,
                         ProductContextService productContextService, IntentDetector intentDetector) {

        this.restClient = restClient;
        this.objectMapper = objectMapper;
        this.productContextService = productContextService;
        this.intentDetector = intentDetector;
    }

    public String generateResponse(String prompt) {

        try {

            String finalPrompt;

            // ---------- Identity Questions ----------
            IntentType intent = intentDetector.detect(prompt);

            switch (intent) {

            case IDENTITY:

                finalPrompt = IdentityPrompt.PROMPT
                        + "\n\nUser Question:\n"
                        + prompt;
                break;

            case SHOPPING:
            case COMPARISON:
            case RECOMMENDATION:

                String context = productContextService.buildProductContext(prompt);

                finalPrompt = ShoppingPrompt.PROMPT
                        + "\n\n"
                        + context
                        + "\n\nCustomer Question:\n"
                        + prompt;
                break;

            case CODING:
            case MATH:
            case GREETING:
            case GENERAL:
            default:

                finalPrompt = GeneralPrompt.PROMPT
                        + "\n\nUser Question:\n"
                        + prompt;
            }            Map<String, Object> text = new HashMap<>();
            text.put("text", finalPrompt);

            Map<String, Object> parts = new HashMap<>();
            parts.put("parts", List.of(text));

            Map<String, Object> body = new HashMap<>();
            body.put("contents", List.of(parts));

            String response = restClient.post()
                    .uri("/v1beta/models/" + model + ":generateContent")
                    .body(body)
                    .retrieve()
                    .body(String.class);

            JsonNode root = objectMapper.readTree(response);

            return root.path("candidates")
                    .get(0)
                    .path("content")
                    .path("parts")
                    .get(0)
                    .path("text")
                    .asText();

        } catch (Exception e) {

            e.printStackTrace();

            return "Sorry! Aura AI is temporarily unavailable.";

        }

    }



}