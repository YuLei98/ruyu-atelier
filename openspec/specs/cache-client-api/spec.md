# cache-client-api Specification

## Purpose
TBD - created by archiving change cache-client-enhancement. Update Purpose after archive.
## Requirements
### Requirement: CacheClient SHALL provide basic CRUD operations
CacheClient SHALL provide get, set, delete, exists methods for Redis cache operations.

#### Scenario: Set and get value
- **WHEN** client calls `set("key", "value", ONE_DAY)`
- **THEN** Redis stores the key-value pair with 1 day expiration

#### Scenario: Get existing value
- **WHEN** client calls `get("key")` on existing key
- **THEN** returns the stored value

#### Scenario: Get non-existent value
- **WHEN** client calls `get("nonexistent")` on non-existent key
- **THEN** returns null

#### Scenario: Delete existing key
- **WHEN** client calls `delete("key")` on existing key
- **THEN** key is removed and returns true

#### Scenario: Delete non-existent key
- **WHEN** client calls `delete("nonexistent")` on non-existent key
- **THEN** returns false

#### Scenario: Check key exists
- **WHEN** client calls `exists("key")` on existing key
- **THEN** returns true

#### Scenario: Check non-existent key
- **WHEN** client calls `exists("nonexistent")` on non-existent key
- **THEN** returns false

### Requirement: CacheClient SHALL provide atomic setIfAbsent operation
CacheClient SHALL provide setIfAbsent method that only sets value when key does not exist.

#### Scenario: Set when key not exists
- **WHEN** client calls `setIfAbsent("newkey", "value", ONE_HOUR)`
- **THEN** key is set and returns true

#### Scenario: Set when key already exists
- **WHEN** client calls `setIfAbsent("existingkey", "value", ONE_HOUR)` on existing key
- **THEN** value is unchanged and returns false

### Requirement: CacheClient SHALL provide TTL management
CacheClient SHALL provide expire and getExpire methods for TTL management.

#### Scenario: Set expiration on existing key
- **WHEN** client calls `expire("key", ONE_HOUR)` on existing key
- **THEN** key's expiration is updated

#### Scenario: Get remaining TTL
- **WHEN** client calls `getExpire("key")` on key with 30 minutes remaining
- **THEN** returns duration approximately equal to 30 minutes

### Requirement: ExpireEnum SHALL provide common expiration options
ExpireEnum SHALL provide FIVE_MINUTES, TEN_MINUTES, THIRTY_MINUTES, ONE_HOUR, ONE_DAY, ONE_WEEK.

#### Scenario: Use FIVE_MINUTES
- **WHEN** ExpireEnum.FIVE_MINUTES is used as expire parameter
- **THEN** cache expires in 5 minutes

