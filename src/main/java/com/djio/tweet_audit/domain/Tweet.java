package com.djio.tweet_audit.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@JsonIgnoreProperties(ignoreUnknown =  true)
public class Tweet {

    @JsonProperty("id")
    private String id;

    @JsonProperty("full_text")
    private String fullText;

    @JsonProperty("created_at")
    private String createdAt;

    private String tweetUrl;
}