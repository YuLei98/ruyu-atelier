## 1. Fix Log4j2 Configuration

- [x] 1.1 Comment out or remove the `druid.sql.Statement` logger in `src/main/resources/log4j2.xml` (lines 96-99) that references the non-existent `druidSqlRollingFile` appender

## 2. Verification

- [x] 2.1 Run `mvn spring-boot:run` and verify no "Unable to locate appender" errors appear
