# Sleep Tracking API

A Spring Boot REST API for tracking sleep data and calculating sleep statistics.

## Features

- Log daily sleep data (bed time, wake time, morning feeling)
- Retrieve last night's sleep information
- Calculate 30-day sleep averages including:
  - Average time in bed
  - Average bed time and wake time
  - Morning feeling frequency distribution

## Prerequisites

- Java 11 or higher
- Docker and Docker Compose (for PostgreSQL database)
- Gradle (or use the included Gradle wrapper)

## Getting Started

### 1. Start the Database

```bash
docker-compose up -d
```

This starts a PostgreSQL database on port 5432.

### 2. Run the Application

```bash
./gradlew bootRun
```

The API will be available at `http://localhost:8080`

### 3. Verify the Application is Running

```bash
curl http://localhost:8080/test
```

Expected response:
```json
{"testMessage": "Hello world!"}
```

## API Endpoints

All endpoints require an `X-User-Id` header to identify the user.

### Create/Update Sleep Log

```
POST /api/sleep
```

**Headers:**
- `X-User-Id`: User identifier (required)
- `Content-Type`: application/json

**Request Body:**
```json
{
    "sleepDate": "2026-03-04",
    "bedTime": "2026-03-03T23:00:00",
    "wakeTime": "2026-03-04T07:00:00",
    "morningFeeling": "GOOD"
}
```

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| sleepDate | date | No | The date of the sleep log (defaults to today) |
| bedTime | datetime | Yes | When you went to bed |
| wakeTime | datetime | Yes | When you woke up |
| morningFeeling | enum | Yes | How you felt: `BAD`, `OK`, or `GOOD` |

**Validation Rules:**
- Wake time must be after bed time
- Sleep duration must be between 30 minutes and 24 hours
- If sleepDate is provided, wake time date must match sleepDate
- Bed time must be on sleepDate or the day before

### Get Last Night's Sleep

```
GET /api/sleep/last-night
```

**Headers:**
- `X-User-Id`: User identifier (required)

**Response:** Returns the sleep log for today, or 404 if none exists.

### Get 30-Day Averages

```
GET /api/sleep/averages
```

**Headers:**
- `X-User-Id`: User identifier (required)

**Response:**
```json
{
    "startDate": "2026-02-03",
    "endDate": "2026-03-04",
    "totalNights": 10,
    "averageTotalTimeInBedMinutes": 456,
    "averageBedTime": "22:52:30",
    "averageWakeTime": "06:42:00",
    "morningFeelingFrequencies": {
        "GOOD": 4,
        "OK": 4,
        "BAD": 2
    }
}
```

## Testing with Postman

A Postman collection is included for easy API testing.

### Import the Collection

1. Open Postman
2. Click **Import** button
3. Select the file: `postman/Sleep_API.postman_collection.json`

### Collection Contents

The collection includes:

**Sleep Logs:**
- Create Sleep Log (today)
- Create Sleep Log - Past Date
- Create Sleep Log - Feeling OK
- Create Sleep Log - Feeling BAD
- Get Last Night's Sleep
- Get 30-Day Averages

**Validation Errors:**
- Missing X-User-Id Header
- Wake Time Before Bed Time
- Missing Bed Time
- Missing Morning Feeling
- Invalid Morning Feeling Value
- Sleep Duration Too Short
- Sleep Duration Too Long
- Wake Time Date Mismatch with Sleep Date
- Bed Time Date Too Early for Sleep Date

**Health Check:**
- Test Endpoint

### Collection Variables

The collection uses variables that can be customized:

| Variable | Default | Description |
|----------|---------|-------------|
| `baseUrl` | `http://localhost:8080` | API base URL |
| `userId` | `1` | User ID for requests |

To modify variables: Click on the collection → Variables tab → Edit values.

## Running Tests

```bash
./gradlew test
```

## Project Structure

```
sleep/
├── src/main/java/com/noom/interview/fullstack/sleep/
│   ├── controller/
│   │   └── SleepLogController.java      # REST API endpoints
│   ├── service/
│   │   └── SleepLogService.java         # Business logic
│   ├── repository/
│   │   └── SleepLogRepository.java      # Data access
│   ├── model/
│   │   ├── SleepLog.java                # JPA entity
│   │   └── MorningFeeling.java          # Enum (BAD, OK, GOOD)
│   ├── dto/
│   │   ├── CreateSleepLogRequest.java   # Request DTO
│   │   └── SleepAveragesResponse.java   # Response DTO
│   ├── validation/
│   │   ├── ValidSleepInterval.java      # Validation annotation
│   │   └── SleepIntervalValidator.java  # Validation logic
│   └── exception/
│       └── GlobalExceptionHandler.java  # Error handling
├── src/main/resources/
│   ├── application.properties           # Configuration
│   └── db/migration/                    # Flyway migrations
├── src/test/                            # Unit tests
├── postman/
│   └── Sleep_API.postman_collection.json
└── docker-compose.yml                   # PostgreSQL setup
```

## Error Handling

The API returns structured error responses:

**Validation Error:**
```json
{
    "timestamp": "2026-03-04T12:00:00",
    "status": 400,
    "error": "Validation Failed",
    "messages": [
        "Sleep duration must be at least 30 minutes (got 20 minutes)"
    ]
}
```

**Missing Header:**
```json
{
    "timestamp": "2026-03-04T12:00:00",
    "status": 400,
    "error": "Missing Required Header",
    "message": "Required header 'X-User-Id' is missing"
}
```

**Invalid Enum Value:**
```json
{
    "timestamp": "2026-03-04T12:00:00",
    "status": 400,
    "error": "Invalid Request",
    "message": "Invalid value 'EXCELLENT' for morningFeeling. Must be one of: BAD, OK, GOOD"
}
```

## Example: Creating Test Data

```bash
# Create multiple sleep logs for testing averages
curl -X POST http://localhost:8080/api/sleep \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 1" \
  -d '{
    "sleepDate": "2026-03-04",
    "bedTime": "2026-03-03T23:00:00",
    "wakeTime": "2026-03-04T07:00:00",
    "morningFeeling": "GOOD"
  }'

# Get the 30-day averages
curl -X GET http://localhost:8080/api/sleep/averages \
  -H "X-User-Id: 1"
```

## Technologies

- **Spring Boot 2.7** - Application framework
- **Spring Data JPA** - Data persistence
- **PostgreSQL** - Database
- **Flyway** - Database migrations
- **Lombok** - Reduce boilerplate code
- **JUnit 5** - Testing framework
