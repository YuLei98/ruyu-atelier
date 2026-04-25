## ADDED Requirements

### Requirement: Actuator health endpoint SHALL return application status
The system SHALL expose a health endpoint at `/actuator/health` that returns the overall application status as JSON.

#### Scenario: Health endpoint returns UP when application is running
- **WHEN** a GET request is sent to `/actuator/health`
- **THEN** the response status is 200 with JSON body `{"status":"UP"}`

#### Scenario: Health endpoint includes component details for authenticated users
- **WHEN** an authenticated user sends GET to `/actuator/health`
- **THEN** the response includes `{"status":"UP","components":{"redis":{"status":"UP"},"db":{"status":"UP"},...}}`

### Requirement: Actuator health endpoint SHALL support Kubernetes probes
The system SHALL expose separate endpoints for Kubernetes liveness and readiness probes.

#### Scenario: Liveness probe returns UP when application is alive
- **WHEN** a GET request is sent to `/actuator/health/liveness`
- **THEN** the response status is 200 with `{"status":"UP"}`

#### Scenario: Readiness probe reflects if application can accept traffic
- **WHEN** a GET request is sent to `/actuator/health/readiness`
- **THEN** the response status is 200 with `{"status":"UP"}` when all components are ready

### Requirement: Redis connection SHALL be included in health check
The system SHALL report Redis connection status in the health endpoint.

#### Scenario: Health endpoint shows UP when Redis is reachable
- **WHEN** Redis is running and reachable
- **THEN** `/actuator/health` shows `"redis":{"status":"UP"}` in components

#### Scenario: Health endpoint shows DOWN when Redis is unreachable
- **WHEN** Redis is not reachable or connection fails
- **THEN** `/actuator/health` shows `"redis":{"status":"DOWN"}` and overall status becomes `"DOWN"`

### Requirement: Database connection SHALL be included in health check
The system SHALL report MySQL/Druid connection status in the health endpoint.

#### Scenario: Health endpoint shows UP when database is reachable
- **WHEN** MySQL database is running and reachable
- **THEN** `/actuator/health` shows `"db":{"status":"UP"}` in components

#### Scenario: Health endpoint shows DOWN when database is unreachable
- **WHEN** MySQL database is not reachable or connection fails
- **THEN** `/actuator/health` shows `"db":{"status":"DOWN"}` and overall status becomes `"DOWN"`

### Requirement: Prometheus metrics endpoint SHALL be exposed
The system SHALL expose `/actuator/prometheus` endpoint for Prometheus to scrape application metrics.

#### Scenario: Prometheus endpoint returns metrics in Prometheus format
- **WHEN** a GET request is sent to `/actuator/prometheus`
- **THEN** the response is 200 with `Content-Type: text/plain` containing Prometheus-formatted metrics

### Requirement: Actuator endpoints SHALL be accessible without authentication
The system SHALL allow unauthenticated access to all `/actuator/**` endpoints.

#### Scenario: Health endpoint is accessible without JWT token
- **WHEN** a GET request is sent to `/actuator/health` without any authentication
- **THEN** the response status is 200 (not 401 or 403)
