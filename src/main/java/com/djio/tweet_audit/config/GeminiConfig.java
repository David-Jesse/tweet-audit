package com.djio.tweet_audit.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

// This file basically creates one shared instance of RestClient
// and makes it available to any service that needs it
@Configuration
public class GeminiConfig {

    // Reads directly from application.properties at startup
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Bean
    public RestClient geminiRestClient() {
        return RestClient.builder()
                .baseUrl(apiUrl + "?key=" + apiKey)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
}