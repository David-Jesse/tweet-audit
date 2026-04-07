package com.djio.tweet_audit.services;


import com.djio.tweet_audit.domain.AuditResult;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

@Service
public class CsvExportService {

    private static final Logger log = LoggerFactory.getLogger(CsvExportService.class);

    public String export(List<AuditResult> results, String outputPath) throws Exception {

        // Create the output directory if it doesn't exist
        File outputFile = new File(outputPath);
        outputFile.getParentFile().mkdirs();

        try (CSVWriter writer = new CSVWriter(new FileWriter(outputFile))) {

            // Write header row
            writer.writeNext(new String[]{"tweet_url", "deleted"});

            // Write one row per flagged tweet
            for (AuditResult result : results) {
                writer.writeNext(new String[]{
                        result.getTweetUrl(),
                        "false"
                });
            }
        }

        log.info("CSV written to: {}", outputPath);
        return outputPath;
    }
}