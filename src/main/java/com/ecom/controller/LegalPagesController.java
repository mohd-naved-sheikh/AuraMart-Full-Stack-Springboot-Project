package com.ecom.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LegalPagesController {
    @GetMapping("/terms")
    public String showTermsPage() {
        return "legal/terms"; // templates/legal/terms.html return karega
    }
}