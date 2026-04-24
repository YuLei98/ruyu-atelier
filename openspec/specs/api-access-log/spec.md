# api-access-log Specification

## Purpose
TBD - created by archiving change api-access-log. Update Purpose after archive.
## Requirements
### Requirement: ApiAccessLogFilter SHALL intercept all HTTP requests
ApiAccessLogFilter SHALL log all HTTP requests with method, path, query, userId, ip, userAgent, status, latency.

#### Scenario: Log request with authenticated user
- **WHEN** user "ruiyu" sends GET /user/info/admin with valid JWT
- **THEN** logs timestamp|traceId|GET|/user/info/admin|null|ruiyu|192.168.1.100|...|200|15|null

#### Scenario: Log request without authentication
- **WHEN** anonymous user sends POST /user/register
- **THEN** logs timestamp|traceId|POST|/user/register|null|null|192.168.1.100|...|200|45|null

#### Scenario: Log request with query parameters
- **WHEN** user sends GET /user/info/admin?format=json
- **THEN** logs query field contains "format=json"

#### Scenario: Log request with traceId from header
- **WHEN** request contains X-Trace-Id header "my-trace-123"
- **THEN** logs traceId field as "my-trace-123"

#### Scenario: Log request with generated traceId
- **WHEN** request does not contain X-Trace-Id header
- **THEN** logs traceId field as a generated UUID

### Requirement: ApiAccessLogFilter SHALL measure request latency
ApiAccessLogFilter SHALL record the time from request start to response completion in milliseconds.

#### Scenario: Record latency for slow request
- **WHEN** request takes 500ms to process
- **THEN** logs latency field as 500

### Requirement: ApiAccessLogFilter SHALL log response status
ApiAccessLogFilter SHALL record the HTTP response status code.

#### Scenario: Log 200 OK response
- **WHEN** controller returns 200 OK
- **THEN** logs status field as 200

#### Scenario: Log 401 Unauthorized response
- **WHEN** request triggers 401 Unauthorized
- **THEN** logs status field as 401

