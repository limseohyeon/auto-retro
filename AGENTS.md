# Auto Retro Agent Control Plane

## Scope and Rule Precedence

- This file governs the entire repository.
- Before editing a path, read the nearest nested `AGENTS.md`; its local rules add to this file.
- If rules conflict, the rule closest to the edited file wins unless it weakens a root immutable rule.
- Keep every `AGENTS.md` below 500 lines. Remove duplication and route details to the owning nested file.

## Translation Source Policy

- `AGENTS.ko.md` files are the only authoring source for agent rules.
- `AGENTS.md` files are committed English generated artifacts and must not be edited directly.
- After changing a Korean source, explicitly invoke the `agents-sync` skill using the active agent's syntax (Codex: `$agents-sync`, Claude Code: `/agents-sync`), review the English diff, and commit both files with the sync manifest.
- Git hooks and CI verify synchronization but never translate or commit files.

## Project Context & Operations

Auto Retro stores development records and turns them into AI summaries, weekly retrospectives, and presentation drafts. It is a modular monolith with a browser client.

- Backend: Java 21, Spring Boot 4.1.0, Spring Modulith 2.1.0, Gradle, JPA, Spring Batch.
- Database: PostgreSQL 17 in CI, Flyway migrations.
- Frontend: React 19, TypeScript 6, Vite 8, React Router 8, TanStack Query 5, React Hook Form, Zod.
- Architecture: domain modules with application ports and inbound/outbound adapters; shared HTTP and error contracts in `common`.

### Operational Commands

Run commands from the repository root unless a command changes directory.

```powershell
# Install locked frontend dependencies
npm --prefix frontend ci

# Run backend with local profile
$env:SPRING_PROFILES_ACTIVE = "local"
.\gradlew.bat bootRun

# Run frontend development server
npm --prefix frontend run dev

# Run backend and repository tests
.\gradlew.bat test

# Run frontend checks
npm --prefix frontend run format:check
npm --prefix frontend run lint
npm --prefix frontend run build

# Run the complete CI-equivalent verification
.\gradlew.bat verifyAll
```

On Unix, use `./gradlew`; CI uses `./gradlew verifyAll --no-daemon`. Backend integration requires PostgreSQL and the datasource variables in `.github/workflows/ci.yml`.

## Golden Rules

### Immutable

- Preserve module ownership. A module must not import another module's JPA entity, repository, or internal adapter.
- Keep dependencies directed as `adapter/in -> application -> domain` and `adapter/out -> application port`; domain and application code must not depend on MVC, JPA, or an AI vendor SDK.
- Treat Flyway migrations as the source of truth for the PostgreSQL schema. Never use Hibernate schema generation as a replacement for a migration.
- Never commit credentials, access tokens, API keys, production data, or populated `.env` files. Read secrets from environment variables.
- Preserve the public API envelope: successful responses use `ApiResponse<T>` and failures use `ErrorResponse` through the common exception handler.
- Do not weaken, delete, skip, or disable tests, static checks, module verification, validation, or database constraints to make a change pass.
- Use the Gradle wrapper and the locked npm dependency graph. Do not commit generated `build/`, `dist/`, or `node_modules/` content.

### Do's

- Identify the owning module before adding behavior and expose only the smallest required public contract.
- Validate input at inbound boundaries and enforce critical invariants again in domain logic or the database as appropriate.
- Put external systems, authentication, persistence, and AI providers behind application-owned ports.
- Use an approved vendor's official maintained SDK when adding an external AI integration, and isolate it in an outbound adapter.
- Add focused tests beside the affected package, then run `verifyAll` before completion.
- Update architecture documentation and an execution plan when a change spans modules, migrations, external integrations, or multiple work sessions.

### Don'ts

- Do not bypass `apiClient` with feature-local HTTP code or let the frontend access PostgreSQL directly.
- Do not expose JPA entities as web responses or use request/response DTOs as domain models.
- Do not add speculative ports, abstractions, dependencies, or shared utilities without an active consumer.
- Do not rewrite an applied Flyway migration; add the next sequential migration.
- Do not hardcode environment-specific URLs, credentials, user data, or provider model secrets.
- Do not mix unrelated refactors with a feature or fix.

## Change Workflow

1. Read this file, the routed nested rules, and the relevant architecture documents.
2. Inspect existing code and tests; do not assume a package, command, or dependency exists.
3. For multi-module, migration, external integration, or multi-session work, create or update an execution plan.
4. Implement the smallest vertical change that preserves module boundaries.
5. Run the narrowest relevant checks, then `verifyAll` before declaring completion.
6. Report changed behavior, migrations or configuration, verification results, and any unresolved risk.

## Standards & References

- Follow [`.editorconfig`](./.editorconfig): UTF-8, final newline, LF except batch files, four spaces for Java/Gradle, and two spaces for YAML.
- Follow the [architecture definition](./docs/architecture-definition.ko.md) for module ownership and dependency direction.
- Follow the [AI development guide](./docs/ai-guidelines.ko.md) for agent workflow and verification policy.
- Follow the [execution-plan guide](./docs/exec-plans/README.md) for long-running or cross-cutting work.
- Java packages use lowercase; classes use PascalCase; methods use camelCase. Prefer `Controller`, `Service`, `Port`, `Adapter`, and `JpaEntity` suffixes where they express the role.
- TypeScript components use PascalCase filenames, hooks start with `use`, and ESLint plus Prettier are authoritative.

### Git and Pull Requests

- Use `type: concise subject` without a trailing period.
- Allowed common types are `feat`, `fix`, `docs`, `test`, `refactor`, `move`, and `chore`.
- Keep each commit to one logical change.
- Pull requests must describe behavior, link the issue when one exists, list verification, and call out migrations, configuration changes, screenshots, and reviewer focus areas.

### Maintenance Policy

- When repository behavior and these rules diverge, do not silently follow stale guidance. Verify the code and executable configuration, then propose or apply the smallest rules update in the same change.
- Promote repeated or high-cost failures into executable enforcement first: database constraints, types, tests, static analysis, then CI. Use `AGENTS.md` only for judgment that cannot be encoded reliably.
- Review rule files after major architecture changes. Remove obsolete guidance, duplicates, broken links, and routes to deleted paths.
- Any new or edited `AGENTS.md` must remain below 500 lines and contain no decorative text or emojis.

## Context Map (Action-Based Routing)

- **[Backend API, domain logic, persistence, batch, or module boundaries](./src/main/java/com/devlog/auto_retro/AGENTS.md)** — Read before changing Java production code or module contracts.
- **[Frontend pages, features, routing, state, API client, or styling](./frontend/AGENTS.md)** — Read before changing the React and TypeScript application or frontend tooling.
- **[Database schema or Flyway migration](./src/main/resources/db/migration/AGENTS.md)** — Read before adding or reviewing schema, constraints, indexes, or data migrations.
- **[Cross-module execution plan](./docs/exec-plans/AGENTS.md)** — Read when work spans modules, migrations, external integrations, or multiple sessions.

## Completion Gate

- The requested behavior and acceptance criteria are satisfied.
- Changed boundaries have focused automated coverage where a test harness exists.
- `verifyAll` passes, or the final report includes the exact failing command and a verified reason outside the change.
- Documentation, migration notes, configuration examples, and execution plans match the implementation.
- No secret, generated artifact, unrelated change, or weakened guardrail is included.
