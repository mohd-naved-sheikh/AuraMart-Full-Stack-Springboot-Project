package com.ecom.ai.intent;

import org.springframework.stereotype.Component;

@Component
public class IntentDetector {

    public IntentType detect(String prompt) {

        if (prompt == null || prompt.isBlank()) {
            return IntentType.UNKNOWN;
        }

        String p = prompt.toLowerCase().trim();

        /* ==========================
                Greetings
        ========================== */

        if (p.matches(".*\\b(hi|hello|hey|hii|hola|good morning|good evening|good afternoon)\\b.*")) {
            return IntentType.GREETING;
        }

        /* ==========================
                Identity
        ========================== */

        if (p.contains("who are you")
                || p.contains("who developed you")
                || p.contains("who created you")
                || p.contains("who made you")
                || p.contains("owner")
                || p.contains("developer")
                || p.contains("creator")
                || p.contains("auramart")) {

            return IntentType.IDENTITY;
        }

        /* ==========================
                Shopping
        ========================== */

        if (p.contains("buy")
                || p.contains("price")
                || p.contains("product")
                || p.contains("category")
                || p.contains("stock")
                || p.contains("discount")
                || p.contains("shop")
                || p.contains("recommend")
                || p.contains("laptop")
                || p.contains("mobile")
                || p.contains("phone")
                || p.contains("watch")
                || p.contains("headphone")
                || p.contains("earbuds")
                || p.contains("shoe")
                || p.contains("shirt")
                || p.contains("pant")) {

            return IntentType.SHOPPING;
        }

        /* ==========================
                Comparison
        ========================== */

        if (p.contains("compare")
                || p.contains("difference between")
                || p.contains("vs")) {

            return IntentType.COMPARISON;
        }

        /* ==========================
                Coding
        ========================== */

        if (p.contains("java")
                || p.contains("spring")
                || p.contains("spring boot")
                || p.contains("hibernate")
                || p.contains("mysql")
                || p.contains("html")
                || p.contains("css")
                || p.contains("javascript")
                || p.contains("python")
                || p.contains("react")
                || p.contains("api")
                || p.contains("code")
                || p.contains("program")) {

            return IntentType.CODING;
        }

        /* ==========================
                Mathematics
        ========================== */

        if (p.matches(".*\\d+.*[+\\-*/].*\\d+.*")
                || p.contains("solve")
                || p.contains("equation")
                || p.contains("math")
                || p.contains("mathematics")) {

            return IntentType.MATH;
        }

        return IntentType.GENERAL;
    }

}