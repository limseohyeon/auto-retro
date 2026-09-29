# Backend Module Rules

## Module Context

This directory contains the Spring Boot modular monolith. Spring Modulith discovers explicitly annotated top-level packages as application modules.

- `user` owns user identity and the `users` table.
- `devrecord` owns development records and tags.
- `aigeneration` owns AI request execution and generated summary lifecycle.
- `weeklyreport` owns period-based retrospective generation.
- `presentation` owns presentation drafts based on record or report identifiers.
- `common` owns only cross-cutting HTTP envelopes and error infrastructure; it is not a dumping ground for business logic.

Cross-module data flow is `user -> devrecord -> aigeneration -> weeklyreport -> presentation`. Share identifiers, immutable DTOs, events, or deliberately exposed application APIs. Never share persistence internals.

## Tech Stack & Constraints

- Java 21 is selected by the Gradle toolchain.
- Spring Boot 4.1.0, Spring Modulith 2.1.0, Spring MVC, Validation, JPA, Batch, Flyway, PostgreSQL, and Lombok are managed in `build.gradle`.
- Constructor injection is required. Lombok `@RequiredArgsConstructor` is acceptable; field injection is not.
- Application and domain code must remain independent of JPA entities, repositories, controllers, and vendor SDKs.
- `spring.jpa.open-in-view` is disabled. Load or map required data inside the application transaction.
- Use `@Transactional(readOnly = true)` for read services and an explicit write transaction for state changes.

## Implementation Patterns

### Package and Dependency Direction

Use the following structure only as needed; do not create empty layers.

```text
<module>/
  adapter/in/web/controller
  adapter/in/web/request
  adapter/in/web/response
  adapter/out/persistence/entity
  adapter/out/persistence/repository
  application/dto
  application/port/in
  application/port/out
  application/service
  domain
  error
```

- Inbound adapters validate and translate transport data, then call an application use case.
- Application services coordinate business rules and depend on outbound port interfaces they own.
- Outbound adapters implement those ports and map persistence or provider models to application/domain types.
- Create an interface only at a real boundary or substitution point. Direct, module-local CRUD may remain simple.
- Keep `package-info.java` accurate when a module's responsibility or exposed contract changes.

### Web and Error Contracts

- Controllers return `ApiResponse<T>` for successful JSON responses.
- Map response records from application DTOs; never serialize a `JpaEntity`.
- Represent browser-facing `long` identifiers as decimal strings to avoid JavaScript precision loss.
- Validate request records with Jakarta Validation and activate validation at the controller boundary.
- Define business errors in the owning module as an `ErrorCode` implementation and throw `BusinessException`.
- Add only truly cross-module failures to `CommonErrorCode`.
- Keep unexpected exception details in server logs; never return stack traces or sensitive internals.

### Persistence and Consistency

- Name persistence classes `<Aggregate>JpaEntity`, `<Aggregate>JpaRepository`, and `<Capability>PersistenceAdapter`.
- Map database names, nullability, lengths, uniqueness, and enums explicitly and consistently with Flyway.
- Protect concurrency-sensitive invariants with database constraints or a documented locking strategy, not only a prior existence query.
- Keep associations inside the owning module. Cross-module references are scalar IDs rather than JPA relationships.
- Publish module events after durable state changes when downstream processing should be decoupled.

### External and AI Integrations

- Define the capability and provider-neutral DTOs in the owning application's port.
- Put SDK types, prompt transport, retries, timeouts, rate limits, and provider error mapping in an outbound adapter.
- Use the provider's official maintained SDK after explicit dependency review; pin versions through Gradle.
- Do not log prompts, responses, tokens, or user records unless a documented redaction policy permits it.
- Make retry and idempotency behavior explicit for AI, batch, and event-driven operations.

## Testing Strategy

Mirror production packages under `src/test/java` and name test classes `*Tests`. Name methods after observable behavior.

```powershell
# All backend and repository tests
.\gradlew.bat test

# One test class
.\gradlew.bat test --tests "com.devlog.auto_retro.AutoRetroApplicationTests"

# Module boundary verification
.\gradlew.bat test --tests "com.devlog.auto_retro.module.ModularStructureTests"

# Complete repository verification
.\gradlew.bat verifyAll
```

- Unit-test domain rules without a Spring context where possible.
- Use focused MVC tests for validation, status codes, and response envelopes.
- Use persistence or integration tests for mappings, constraints, transactions, and queries.
- Update `ModularStructureTests` expectations when adding or exposing a module.
- PostgreSQL-backed tests use the `test` profile and datasource environment variables from `.github/workflows/ci.yml`.

## Local Golden Rules

### Do's

- Let the owning module define business vocabulary, errors, ports, and invariants.
- Normalize input once at the boundary or use-case entry and test the observable result.
- Keep mapping explicit between web, application, domain, and persistence models.
- Add a Flyway migration before relying on a new column, constraint, or index.
- Test duplicate, missing, invalid, unauthorized, retry, and concurrent cases when relevant.

### Don'ts

- Do not inject a Spring Data repository directly into another module or into a controller.
- Do not annotate domain objects with web, persistence, or provider annotations.
- Do not return `null` where an empty collection, `Optional` at a port, or a defined error is the contract.
- Do not catch `Exception` in business services to hide failures; translate exceptions at the correct adapter boundary.
- Do not introduce bidirectional module dependencies.
- Do not change an API field or error code without updating its frontend consumer and contract tests.
