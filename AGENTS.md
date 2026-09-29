# Project Instructions

## Technology

- Use Java 21, Spring Boot 4, Gradle, PostgreSQL, and Flyway.
- Use Spring Modulith to verify module boundaries when modules are introduced.
- Add a new production dependency only when the requirement justifies it.

## Build and Verification

- On Windows, run tests with `.\gradlew.bat test`.
- In Linux and CI, run tests with `./gradlew test --no-daemon`.
- Run the relevant tests after changing production code.
- Keep the application context test passing.

## Architecture

- Build the application as a modular monolith organized by business domain.
- Keep domain and application logic independent from JPA, Spring MVC, Spring AI, and external APIs.
- Define ports in the application layer and implement external integrations in adapters.
- Do not access another module's internal repository, entity, or implementation class directly.
- Reuse the same application use case from REST controllers and batch entry points.
- Keep simple CRUD code pragmatic; do not create interfaces without a meaningful boundary.

## Persistence

- Manage schema changes with versioned Flyway migrations.
- Do not use Hibernate schema generation as the source of truth.
- Keep JPA entities and repositories inside persistence adapters.
- Enforce concurrency-sensitive invariants with database constraints when appropriate.

## API and Configuration

- Validate external input at the application boundary.
- Return errors through the shared exception and error-response format.
- Keep environment-specific values in Spring profiles.
- Read passwords, API keys, and other secrets from environment variables.
- Never commit `.env` files or real credentials.

## Git

- Follow the repository commit template and Conventional Commits types.
- Keep commits focused and avoid modifying unrelated files.
- Do not overwrite or revert existing user changes unless explicitly requested.

