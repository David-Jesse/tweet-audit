package com.djio.tweet_audit.services;

import com.djio.tweet_audit.domain.Tweet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class ArchiveParserService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Tweet> parse(String filePath) throws IOException {

        // Read the whole file into a string and reads it as a text
        String raw = Files.readString(Path.of(filePath));

        // Here we remove the JS prefix by finding where the JSON array starts
        int startIndex = raw.indexOf("[");
        if (startIndex == -1) {
            throw new IllegalArgumentException(
                    "Could not find JSON array in files: " + filePath
            );
        }

        String cleanJson = raw.substring(startIndex);

        // Parse the JSON
        JsonNode rootArray = objectMapper.readTree(cleanJson);

        // Extract each tweet
        List<Tweet> tweets = new ArrayList<>();

        for (JsonNode node : rootArray) {
            JsonNode tweetNode = node.get("tweet");
            if (tweetNode != null) {
                Tweet tweet = objectMapper.treeToValue(tweetNode, Tweet.class);
                tweet.setTweetUrl(
                        "https://x.com/i/web/status/" + tweet.getId()
                );
                tweets.add(tweet);
            }
        }
        return tweets;
    }
}