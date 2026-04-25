## ADDED Requirements

### Requirement: SignUpReq validation
The system SHALL validate SignUpReq fields using Bean Validation annotations.

#### Scenario: Username too short
- **WHEN** user submits register request with username "ab" (less than 3 characters)
- **THEN** system returns 500 with message "用户名长度需在 3-20 个字符之间"

#### Scenario: Username missing
- **WHEN** user submits register request with missing username
- **THEN** system returns 500 with message "用户名不能为空"

#### Scenario: Password too short
- **WHEN** user submits register request with password "12345" (less than 6 characters)
- **THEN** system returns 500 with message "密码长度需在 6-30 个字符之间"

#### Scenario: Valid registration
- **WHEN** user submits register request with valid username and password
- **THEN** system proceeds with registration and returns success message

### Requirement: SignInReq validation
The system SHALL validate SignInReq fields using Bean Validation annotations.

#### Scenario: Username missing on login
- **WHEN** user submits login request with missing username
- **THEN** system returns 500 with message "用户名不能为空"

#### Scenario: Password missing on login
- **WHEN** user submits login request with missing password
- **THEN** system returns 500 with message "密码不能为空"

### Requirement: Validation errors return CommonResult
The system SHALL return validation errors wrapped in CommonResult format.

#### Scenario: Multiple validation errors
- **WHEN** user submits register request with both username and password missing
- **THEN** system returns single error message containing both validation failures
