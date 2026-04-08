package com.djio.tweet_audit.controllers;


import com.djio.tweet_audit.domain.AuditRequest;
import com.djio.tweet_audit.domain.AuditResult;
import com.djio.tweet_audit.services.AuditService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/audit")
public class AuditController {

    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<String> runAudit(@RequestBody AuditRequest request) {
        try {
            List<AuditResult> results = auditService.runAudit(
                    request.getArchivePath(),
                    request.getCriteria()
            );
            return ResponseEntity.ok(
                    "Audit complete. " + results.size() +
                            " tweets flagged. CSV saved to output/flagged_tweets.csv"
            );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Audit failed: " + e.getMessage());
        }
    }
}