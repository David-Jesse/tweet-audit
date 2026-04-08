## Architecture Choice: Layered Service Architecture

I chose a layered service architecture with four single-responsibility services
-- ArchiveParserService, GeminiService, AuditService, CsvExportService -
coordinated by a REST controller. Each service has exactly one reason to change.
If the X archive format changes, only the parser is affected. If Gemini changes
their API, only GeminiService changes. This separation made the code independently testable and easy to
follow the reasoning

## Concurrency Strategy: Sequential Processing

I chose sequential processing - one tweet at a time - intentionally. For a first implementation, sequential
processing is predictable, easy to debug, and correct. The trade-off is speed; processing 2, 871 tweets
sequentially is slow. The natural next step would be refactoring AuditService to use a bounded thread pool via
ExecutorService, submitting each tweet evaluation as a task. I chose not to introduce concurrency prematurely
because it adds complexity without changing correctness.

## Error Handling: Log and Continue

When Gemini fails for a specific tweet - whether a 503 (service unavailable) or 429 (rate limit exceeded) -
the system logs the error and returns an unflagged result rather than crashing the entire process. The reasoning:
processing 2,800 of 2,871 tweets is far more valuable than processing zero. Retryable errors (503, 429, 500)
trigger exponential backoff with up to 3 attempts before giving up on that tweet. Non-retryable errors (400, 404)
fail immediately since retrying won't help.

## Rate Limiting: Known Limitation

The free Gemini API tier allows 20 requests per day. For a 2,871 tweet archive this is a hard constraint. The
current retry logic uses hardcoded backoff intervals (2s -> 4s -> 8s), but Gemini's 429 response actually includes
the exact retry delay in the response body ("retryDelay": "30s"). A better implementation would parse that value
and wait precisely that long rather than using a fixed backoff. This is a know improvement left out for simplicity.

## Performance vs Safety

Sequential processing with retry logic prioritises correctness and safety over speed. Every tweet is accounted
for - either flagged, kept, or logged as failed. No tweet is silently dropped. The trade-off is that large archives
are slow to process. Batching with a thread pool would improve throughput but introduces concerns around Gemini
rate limits being hit faster, requiring more sophisticated backpressure handling

## Why Spring Boot + Java

Spring Boot provided a familiar, structured foundation after completing a prior REST API project. RestClient gave
clean HTTP integration with Gemini. The layered architecture maps naturally to Spring's dependency injection model -
each service is a Spring bean wired together by the container, making the system easy to extend and test with
Mockito mocks.