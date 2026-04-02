package com.djio.tweet_audit;

import com.djio.tweet_audit.domain.AlignmentCriteria;
import com.djio.tweet_audit.domain.AuditResult;
import com.djio.tweet_audit.domain.Tweet;
import com.djio.tweet_audit.services.GeminiService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class TweetAuditApplication {

    public static void main(String[] args) {
        SpringApplication.run(TweetAuditApplication.class, args);
    }
}