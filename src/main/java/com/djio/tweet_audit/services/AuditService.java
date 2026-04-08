package com.djio.tweet_audit.services;


import com.djio.tweet_audit.domain.AlignmentCriteria;
import com.djio.tweet_audit.domain.AuditResult;
import com.djio.tweet_audit.domain.Tweet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);

    private final ArchiveParserService archiveParserService;
    private final GeminiService geminiService;
    private final CsvExportService csvExportService;

    public AuditService(ArchiveParserService archiveParserService,
                        GeminiService geminiService,
                        CsvExportService csvExportService
    ) {
        this.archiveParserService = archiveParserService;
        this.geminiService = geminiService;
        this.csvExportService = csvExportService;
    }

    public List<AuditResult> runAudit(String archivePath, AlignmentCriteria criteria) throws Exception {

        // Parse the archive
        log.info("Parsing archive from: {}", archivePath);
        List<Tweet> tweets = archiveParserService.parse(archivePath);
//        tweets = tweets.subList(0, Math.min(15, tweets.size()));
        log.info("Found {} tweets to evaluate", tweets.size());

        // Evaluate each tweet sequentially
        List<AuditResult> flaggedResults = new ArrayList<>();
        int processed = 0;

        for (Tweet tweet : tweets) {
            AuditResult result = geminiService.evaluate(tweet, criteria);
            processed++;

            if (result.isFlagged()) {
                flaggedResults.add(result);
                log.info("Tweet {} flagged: {}", tweet.getId(), result.getReason());
            }

            // Progress Logging - important for large archives
            if (processed % 10 == 0) {
                log.info("Progress: {}/{} tweet evaluated, {} flagged so far",
                        processed, tweets.size(), flaggedResults.size()
                );
            }
        }

        log.info("Audit complete. {}/{} tweet flagged.",
                flaggedResults.size(), tweets.size()
        );
        String csvPath = csvExportService.export(flaggedResults, "output/flagged_tweets.csv");
        log.info("Results saved to: {}", csvPath);
        return flaggedResults;
    }
}