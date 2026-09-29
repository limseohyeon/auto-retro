# Flyway Migration Rules

## Module Context

This directory is the authoritative, append-only history of the PostgreSQL schema. JPA mappings, application assumptions, and test data must conform to these migrations.

Current ownership follows the backend modules: `users` belongs to `user`; development records and tags belong to `devrecord`; summaries and AI execution data belong to `aigeneration`; weekly reports belong to `weeklyreport`; presentations belong to `presentation`. `event_publication` supports Spring Modulith event delivery.

## Tech Stack & Constraints

- PostgreSQL is the target database; CI currently runs PostgreSQL 17.
- Flyway is started by Spring Boot and validates migration history.
- Use versioned SQL files named `V<number>__<lower_snake_case_description>.sql`.
- Determine the next version from the highest committed migration. Never reuse a version.
- Migrations must be forward-only and deterministic on the supported PostgreSQL version.

## Implementation Patterns

- Add a new migration for every schema change. Never edit a migration that may have been applied.
- Name primary keys `pk_<table>`, foreign keys `fk_<table>_<target>`, unique constraints `uq_<table>_<columns>`, checks `ck_<table>_<rule>`, and indexes `idx_<table>_<purpose>`.
- Declare nullability, lengths, defaults, checks, foreign-key actions, and uniqueness explicitly.
- Use `TIMESTAMPTZ` for instants and `DATE` for calendar dates.
- Use database constraints for invariants that must survive concurrent writers.
- Add indexes from demonstrated query and foreign-key access patterns; do not add speculative indexes.
- Keep cross-module relationships as scalar foreign keys or typed source IDs. Do not create cross-module persistence coupling in Java.
- Separate risky backfills into expand, populate, verify, and constrain phases when one transaction would lock or rewrite significant data.
- Document any non-transactional statement, required maintenance window, irreversible transformation, or deployment ordering in the execution plan.

## Testing Strategy

Use a disposable PostgreSQL database with the same major version as CI when possible.

```powershell
$env:SPRING_PROFILES_ACTIVE = "test"
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/auto_retro_test"
$env:SPRING_DATASOURCE_USERNAME = "auto_retro"
$env:SPRING_DATASOURCE_PASSWORD = "test-password"
$env:SPRING_BATCH_JDBC_INITIALIZE_SCHEMA = "always"
.\gradlew.bat test
```

- Verify migration from an empty database.
- For a data migration, verify representative existing rows, null or malformed legacy values, rerun behavior where relevant, and post-migration constraints.
- Verify corresponding JPA mappings and repository behavior.
- Run `verifyAll` after the focused database checks.

## Local Golden Rules

### Do's

- Inspect all existing versions before choosing a filename.
- Make destructive intent explicit and provide a preservation or recovery strategy in the execution plan.
- Align Java enum values and validation with database checks in the same change.
- Verify foreign-key delete behavior against aggregate ownership.
- Prefer additive, backward-compatible migrations when deployments may overlap.

### Don'ts

- Do not use `ddl-auto=create`, `update`, or `create-drop` as schema management.
- Do not drop or rename populated columns without a reviewed compatibility and recovery plan.
- Do not add `NOT NULL` to populated data before backfilling and validating it.
- Do not rely on application-level uniqueness checks without a database constraint.
- Do not put credentials, environment-specific owners, or production-only grants in migrations.
- Do not assume a polymorphic `source_id` has referential integrity; validate it in the owning application workflow.
