package com.djio.tweet_audit.services;

import com.djio.tweet_audit.domain.Tweet;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ArchiveParserServiceTest {

    private final ArchiveParserService parser = new ArchiveParserService();

//    private Path getResourcePath(String fileName) throws Exception {
//        URL resource = getClass().getClassLoader().getResource(fileName);
//        assertNotNull(resource, "Test resource file not found: " + fileName);
//        return Paths.get(resource.toURI());
//    }

    @Test
    void shouldParseAllTweetsFromArchive() throws Exception {
        List<Tweet> tweets = parser.parse("src/test/java/resources/sample_tweets.js");
        assertEquals(2, tweets.size());
    }

    @Test
    void shouldExtractCorrectFields() throws Exception {
        List<Tweet> tweets = parser.parse("src/test/java/resources/sample_tweets.js");
        Tweet first = tweets.get(0);

        assertEquals("123456789", first.getId());
        assertEquals("This is a test tweet about crypto and NFTs", first.getFullText());
        assertNotNull(first.getCreatedAt());
    }

    @Test
    void shouldBuildTweetUrl() throws Exception {
        List<Tweet> tweets = parser.parse("src/test/java/resources/sample_tweets.js");
        assertEquals(
                "https://x.com/i/web/status/123456789",
                tweets.get(0).getTweetUrl()
        );
    }

    @Test
    void shouldIgnoreUnknowFields() throws Exception {
        assertDoesNotThrow(() ->
                parser.parse("src/test/java/resources/sample_tweets.js")
        );
    }
}