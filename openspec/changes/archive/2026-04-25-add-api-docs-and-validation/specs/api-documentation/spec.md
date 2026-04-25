## ADDED Requirements

### Requirement: Swagger UI accessible
The system SHALL provide Swagger UI at `/swagger-ui.html` for interactive API documentation.

#### Scenario: Access Swagger UI
- **WHEN** user navigates to `http://localhost:8000/swagger-ui.html`
- **THEN** system returns Swagger UI page with list of all registered endpoints

### Requirement: OpenAPI JSON endpoint
The system SHALL provide OpenAPI 3.0 specification JSON at `/v3/api-docs`.

#### Scenario: Access OpenAPI JSON
- **WHEN** user requests `GET /v3/api-docs`
- **THEN** system returns JSON with OpenAPI 3.0 specification containing all API paths

### Requirement: API metadata configuration
The system SHALL allow customization of OpenAPI metadata (title, description, version).

#### Scenario: Custom metadata displayed
- **WHEN** user opens Swagger UI
- **THEN** page displays configured title "Framework API" and version "1.0.0"
