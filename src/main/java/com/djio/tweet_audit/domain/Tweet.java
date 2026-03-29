package com.djio.tweet_audit.domain;

import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class Tweet {

    @JsonProperty("id")
    private String id;

    @JsonProperty("full_text")
    private String fullText;

    @JsonProperty("created_at")
    private String createdAt;

    private String tweetUrl;
}