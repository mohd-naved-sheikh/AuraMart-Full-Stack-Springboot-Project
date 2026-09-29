package com.ecom.ai.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.model.Product;
import com.ecom.repository.ProductRepository;

@Service
public class ProductContextService {

    private final ProductRepository productRepository;

    public ProductContextService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public String buildProductContext(String userQuestion) {

        String keyword = extractKeyword(userQuestion);

        List<Product> products = productRepository
                .findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(keyword, keyword);

        if (products.isEmpty()) {
            products = productRepository.findByIsActiveTrue();
        }

        if (products.size() > 5) {
            products = products.subList(0, 5);
        }

        StringBuilder context = new StringBuilder();

        context.append("""
                ========================
                AVAILABLE AURAMART PRODUCTS
                ========================

                Below are the products currently available in AuraMart.

                Use ONLY these products while answering shopping related questions.

                """);

        for (Product product : products) {

            context.append("Product ID: ")
                    .append(product.getId())
                    .append("\n");

            context.append("Title: ")
                    .append(product.getTitle())
                    .append("\n");

            context.append("Category: ")
                    .append(product.getCategory())
                    .append("\n");

            context.append("Selling Price: ₹")
                    .append(product.getDiscountPrice())
                    .append("\n");

            context.append("Original Price: ₹")
                    .append(product.getPrice())
                    .append("\n");

            context.append("Stock: ")
                    .append(product.getStock())
                    .append("\n");

            context.append("Description: ")
                    .append(product.getDescription())
                    .append("\n");

            context.append("----------------------------------------\n");
        }

        return context.toString();
    }

    private String extractKeyword(String question) {

        String lower = question.toLowerCase();

        if (lower.contains("laptop"))
            return "laptop";

        if (lower.contains("mobile"))
            return "mobile";

        if (lower.contains("phone"))
            return "phone";

        if (lower.contains("watch"))
            return "watch";

        if (lower.contains("shoe"))
            return "shoe";

        if (lower.contains("shirt"))
            return "shirt";

        if (lower.contains("pant"))
            return "pant";

        if (lower.contains("headphone"))
            return "headphone";

        if (lower.contains("earbuds"))
            return "earbuds";

        return question;
    }
}