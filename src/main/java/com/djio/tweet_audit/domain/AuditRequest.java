package com.djio.tweet_audit.domain;

import lombok.Data;

@Data
public class AuditRequest {
    private String archivePath;
    private AlignmentCriteria criteria;
}