package com.djio.tweet_audit;

import com.djio.tweet_audit.domain.AlignmentCriteria;
import com.djio.tweet_audit.services.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;

@SpringBootApplication
public class TweetAuditApplication {

    private static final Logger log = LoggerFactory.getLogger(TweetAuditApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TweetAuditApplication.class, args);
    }

//    @Bean
//    public CommandLineRunner run(AuditService auditService) {
//        return args -> {
//            log.info("--- Starting Manual Audit Run ---");
//
//            // 1. Create your hardcoded criteria
//            AlignmentCriteria criteria = new AlignmentCriteria();
//            criteria.setForbiddenWords(List.of("crypto", "bitcoin", "nft", "scam"));
//            criteria.setProfessionalCheck(true);
//            criteria.setTone("professional");
//            criteria.setExcludePolitics(true);
//
//            // 2. Define the path to your sample fixture
//            // Ensure this file exists in your project root or provide the full path
//            String fixturePath = "src/test/resources/sample_tweets.js";
//
//            try {
//                // 3. Execute the audit
//                auditService.runAudit(fixturePath, criteria);
//                log.info("--- Audit Finished Successfully ---");
//            } catch (Exception e) {
//                log.error("Audit failed with error: {}", e.getMessage());
//                e.printStackTrace();
//            }
//        };
//    }
}