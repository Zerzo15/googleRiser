package com.example.app.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.app.dto.AiCompanyAnalysisDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CompanyIntelligenceService {

    @Value("${gemini.api.key:}")
    private String apiKey;

    @Value("${gemini.api.url:https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent}")
    private String apiUrl;

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public CompanyIntelligenceService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public AiCompanyAnalysisDto analyzeCompany(String name, String domain) throws Exception {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is not configured");
        }

        String prompt = """
            You are a corporate intelligence analyst. Research the following company:
            - Name: %s
            - Domain: %s

            Extract and return ONLY a valid JSON object matching this schema without Markdown formatting:
            {
              "sector": "Primary industry/sector in Vietnamese (e.g., Công nghệ thông tin)",
              "scale": "Estimated employee count or scale (e.g., 50 - 200 nhân sự)",
              "products": "Summary of core products/services in Vietnamese",
              "market": "Target market (e.g., Việt Nam & Đông Nam Á)",
              "taxId": "Tax code if publicly known or null",
              "sources": [
                {
                  "platformName": "Website / LinkedIn / Cổng ĐKKD",
                  "url": "URL or domain source",
                  "snippet": "Brief note on source validity"
                }
              ]
            }
            """.formatted(name, domain);

        Map<String, Object> requestBodyMap = java.util.Map.of(
            "contents", java.util.List.of(
                java.util.Map.of("parts", java.util.List.of(
                    java.util.Map.of("text", prompt)
                ))
            ),
            "generationConfig", java.util.Map.of(
                "responseMimeType", "application/json"
            )
        );

String requestBody = objectMapper.writeValueAsString(requestBodyMap);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl + "?key=" + apiKey))
                .header("Content-Type", "application/json")
                .timeout(Duration.ofSeconds(25))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("AI API error: HTTP " + response.statusCode() + " -> " + response.body());
        }

        JsonNode root = objectMapper.readTree(response.body());
        String rawJson = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        return objectMapper.readValue(rawJson, AiCompanyAnalysisDto.class);
    }
}