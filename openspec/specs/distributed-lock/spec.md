# distributed-lock Specification

## Purpose
TBD - created by archiving change cache-client-enhancement. Update Purpose after archive.
## Requirements
### Requirement: DistributedLock SHALL provide tryLock with timeout
DistributedLock SHALL provide tryLock method that attempts to acquire lock with timeout.

#### Scenario: Acquire lock successfully
- **WHEN** client calls `tryLock("resource1", ONE_MINUTE, 3)` when lock is available
- **THEN** lock is acquired and returns true within timeout

#### Scenario: Fail to acquire lock within timeout
- **WHEN** client calls `tryLock("resource1", ONE_MINUTE, 1)` when lock is held by another
- **THEN** returns false after 1 second timeout

### Requirement: DistributedLock SHALL provide lock/unlock operations
DistributedLock SHALL provide lock (blocking) and unlock methods.

#### Scenario: Unlock by owner
- **WHEN** owner calls `unlock("resource1")` after acquiring lock
- **THEN** lock is released and returns true

#### Scenario: Unlock by non-owner
- **WHEN** non-owner calls `unlock("resource1")` without owning lock
- **THEN** lock remains held and returns false

### Requirement: DistributedLock SHALL ensure only owner can unlock
DistributedLock SHALL use unique identifier (UUID) as lock value to ensure only owner can release.

#### Scenario: Owner verification on unlock
- **WHEN** owner calls `unlock("resource1")`
- **THEN** only releases if current lock value matches owner's UUID

