package com.djio.tweet_audit.services;


import com.djio.tweet_audit.domain.AlignmentCriteria;
import com.djio.tweet_audit.domain.AuditResult;
import com.djio.tweet_audit.domain.Tweet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);
    private final RestClient restClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_RETRIES = 3;
    private static final long INITIAL_BACKOFF_MS = 2000;

    public GeminiService(RestClient geminiRestClient) {
        this.restClient = geminiRestClient;
    }

    public AuditResult evaluate(Tweet tweet, AlignmentCriteria criteria) {
        int attempts = 0;
        long backoffMs = INITIAL_BACKOFF_MS;

        while (attempts < MAX_RETRIES) {
            try {
                String requestBody = buildRequestBody(tweet, criteria);

                String response = restClient.post()
                        .body(requestBody)
                        .retrieve()
                        .body(String.class);

                return parseResponse(response, tweet.getTweetUrl());
            } catch (Exception e) {
                attempts++;
                boolean isRetryable = e.getMessage() != null &&
                        (e.getMessage().contains("503") ||
                                e.getMessage().contains("429") ||
                                e.getMessage().contains("500")
                        );

                if (!isRetryable || attempts >= MAX_RETRIES) {
                    log.error("Gemini evaluation failed for tweet {} after {} attempt(s): {}",
                            tweet.getId(), attempts, e.getMessage());
                    return new AuditResult(tweet.getTweetUrl(), false,
                            "Evaluation failed: " + e.getMessage()
                    );
                }

                log.warn("Gemini returned retryable error for tweet {}. " +
                                "Attempts {}/{}. Retrying in {}ms...",
                        tweet.getId(), attempts, MAX_RETRIES, backoffMs
                );

                try {
                    Thread.sleep(backoffMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                // Doubles the wait each time a retry happens
                backoffMs *= 2;
            }
        }

        return new AuditResult(tweet.getTweetUrl(), false, "Max retries exceeded");
    }

    private String buildRequestBody(Tweet tweet, AlignmentCriteria criteria) {
        // This structure was gotten from the Gemini docs
        // contents -> parts -> text

        String prompt = buildPrompt(tweet, criteria);

        // Escape the prompt safely for embedding in JSON
        String escapedPrompt = prompt
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");

        return String.format("""
                {
                    "contents": [
                        {
                            "parts": [
                                {
                                    "text": "%s"
                                }
                            ]
                        }
                    ]
                }
                """, escapedPrompt
        );
    }

    private String buildPrompt(Tweet tweet, AlignmentCriteria criteria) {
        return String.format(
                "You are a tweet auditor. Evaluate this tweet against the following criteria.\n" +
                        "Forbidden words: %s\n" +
                        "Must be professional: %s\n" +
                        "Required tone: %s\n" +
                        "Exclude politics: %s\n\n" +
                        "Tweet: %s\n\n" +
                        "Respond ONLY with a valid JSON object. No markdown, no explanation, no extra text.\n" +
                        "Format: {\"flagged\": true or false, \"reason\": \"brief reason\"}",
                criteria.getForbiddenWords(),
                criteria.isProfessionalCheck(),
                criteria.getTone(),
                criteria.isExcludePolitics(),
                tweet.getFullText()
        );
    }

    private AuditResult parseResponse(String response, String tweetUrl) {
        try {
            JsonNode root = objectMapper.readTree(response);

            // Response structure
            // candidates -> content -> parts -> text
            String text = root
                    .path("candidates").get(0)
                    .path("content")
                    .path("parts").get(0)
                    .path("text")
                    .asText();

            // clean up text
            text = text.replace("```json", "")
                    .replace("```", "")
                    .trim();

            JsonNode verdict = objectMapper.readTree(text);
            boolean flagged = verdict.path("flagged").asBoolean();
            String reason = verdict.path("reason").asText("No reason provided");

            return new AuditResult(tweetUrl, flagged, reason);
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", e.getMessage());
            return new AuditResult(tweetUrl, false,
                    "Response parse error:" + e.getMessage()
            );
        }
    }
}