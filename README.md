# tweet-audit

Analyse your X (Twitter) archive using Gemini AI and flag tweets for deletion 
based on custom criteria.

## What It Does

Processes your X archive, evaluates each tweet against your alignment criteria 
using Google's Gemini AI, and generates a CSV of flagged tweet URLs for manual 
deletion.

## How It Works
X Archive (tweet.js) → Parse → Evaluate with Gemini AI → Export flagged tweets to CSV
## Requirements

- Java 21+
- Maven
- Google Gemini API key ([get one here](https://aistudio.google.com/app/apikey))
- Your X archive ([how to download](https://help.twitter.com/en/managing-your-account/how-to-download-your-twitter-archive))

## Setup

**1. Clone the repository**
```bash
git clone https://github.com/David-Jesse/tweet-audit.git
cd tweet-audit
```

**2. Add your Gemini API key**

Create `src/main/resources/application.properties`:
```properties
gemini.api.key=your_api_key_here
gemini.api.url=https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent
```

**3. Add your X archive**

Copy `tweet.js` from your downloaded X archive into an `archive/` folder 
at the project root.

**4. Run the application**
```bash
mvn spring-boot:run
```

## Usage

Send a POST request to `/audit` with your archive path and alignment criteria:

```bash
curl -X POST http://localhost:8080/audit \
  -H "Content-Type: application/json" \
  -d '{
    "archivePath": "archive/tweet.js",
    "criteria": {
      "forbiddenWords": ["crypto", "NFT"],
      "professionalCheck": true,
      "tone": "respectful and thoughtful",
      "excludePolitics": false
    }
  }'
```

## Output

A CSV file at `output/flagged_tweets.csv`:
tweet_url,deleted
https://x.com/i/web/status/1234567890,false
https://x.com/i/web/status/9876543210,false

You can then manually delete flagged tweets or use the X API for automated deletion.

## Project Structure

tweet-audit/
├── src/
│   ├── main/java/com/djio/tweet_audit/
│   │   ├── config/          # Gemini REST client configuration
│   │   ├── controllers/     # HTTP entry point
│   │   ├── domain/          # Data models (Tweet, AuditResult, etc.)
│   │   └── services/        # Core pipeline logic
│   └── test/                # Unit tests
├── archive/                 # Your tweet.js goes here (gitignored)
├── output/                  # Generated CSV files (gitignored)
├── TRADEOFFS.md             # Architecture decisions
└── config.example.json      # Example alignment criteria

## Architecture

Four single-responsibility services coordinated by a REST controller:

- **ArchiveParserService** — reads and parses `tweet.js`
- **GeminiService** — evaluates tweets via Gemini AI with retry/backoff
- **AuditService** — orchestrates the pipeline
- **CsvExportService** — writes flagged results to CSV

See [TRADEOFFS.md](TRADEOFFS.md) for architectural decisions and trade-offs.

## Notes

- The free Gemini API tier allows 20 requests/day. For large archives, 
  a paid tier is recommended.
- Your archive files and API keys are gitignored by default. 
  Never commit them.
