package com.ecom.ai.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ecom.ai.dto.ChatRequest;
import com.ecom.ai.dto.ChatResponse;
import com.ecom.ai.service.GeminiService;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final GeminiService geminiService;

    public AiController(GeminiService geminiService) {
        this.geminiService = geminiService;
    }

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {

        String reply = geminiService.generateResponse(request.getMessage());

        return ResponseEntity.ok(new ChatResponse(reply));
    }
}