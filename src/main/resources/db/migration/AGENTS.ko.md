# Flyway Migration 규칙

## 모듈 컨텍스트

이 디렉터리는 PostgreSQL schema의 권위 있는 append-only history입니다. JPA mapping, application assumption, test data는 이 migration을 따라야 합니다.

현재 ownership은 backend module을 따릅니다. `users`는 `user`, development record와 tag는 `devrecord`, summary와 AI execution data는 `aigeneration`, weekly report는 `weeklyreport`, presentation은 `presentation`이 소유합니다. `event_publication`은 Spring Modulith event delivery를 지원합니다.

## 기술 스택과 제약조건

- Target database는 PostgreSQL이며 현재 CI는 PostgreSQL 17을 실행합니다.
- Flyway는 Spring Boot가 시작하며 migration history를 validate합니다.
- Versioned SQL file은 `V<number>__<lower_snake_case_description>.sql`로 이름을 정합니다.
- Committed migration 중 가장 높은 version을 확인해 다음 version을 결정합니다. Version을 재사용하지 않습니다.
- Migration은 지원하는 PostgreSQL version에서 forward-only이며 deterministic해야 합니다.

## 구현 패턴

- 모든 schema change에는 새 migration을 추가합니다. 이미 적용되었을 수 있는 migration을 수정하지 않습니다.
- Primary key는 `pk_<table>`, foreign key는 `fk_<table>_<target>`, unique constraint는 `uq_<table>_<columns>`, check는 `ck_<table>_<rule>`, index는 `idx_<table>_<purpose>`로 이름을 정합니다.
- Nullability, length, default, check, foreign-key action, uniqueness를 명시합니다.
- Instant에는 `TIMESTAMPTZ`, calendar date에는 `DATE`를 사용합니다.
- Concurrent writer에서도 유지되어야 하는 invariant는 database constraint로 강제합니다.
- 확인된 query와 foreign-key access pattern을 근거로 index를 추가합니다. 추측성 index를 추가하지 않습니다.
- Cross-module relationship은 scalar foreign key 또는 typed source ID로 유지합니다. Java에 cross-module persistence coupling을 만들지 않습니다.
- 하나의 transaction이 상당한 data를 lock하거나 rewrite한다면 위험한 backfill을 expand, populate, verify, constrain 단계로 나눕니다.
- Non-transactional statement, 필요한 maintenance window, irreversible transformation 또는 deployment ordering을 execution plan에 문서화합니다.

## 테스트 전략

가능하면 CI와 같은 major version의 disposable PostgreSQL database를 사용합니다.

```powershell
$env:SPRING_PROFILES_ACTIVE = "test"
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/auto_retro_test"
$env:SPRING_DATASOURCE_USERNAME = "auto_retro"
$env:SPRING_DATASOURCE_PASSWORD = "test-password"
$env:SPRING_BATCH_JDBC_INITIALIZE_SCHEMA = "always"
.\gradlew.bat test
```

- Empty database에서 migration을 검증합니다.
- Data migration은 대표적인 existing row, null 또는 malformed legacy value, 필요한 경우 rerun behavior, post-migration constraint를 검증합니다.
- 대응하는 JPA mapping과 repository behavior를 검증합니다.
- Focused database check 뒤에 `verifyAll`을 실행합니다.

## 로컬 핵심 규칙

### 해야 할 일

- Filename을 정하기 전에 existing version 전체를 확인합니다.
- Destructive intent를 명시하고 execution plan에 preservation 또는 recovery strategy를 제공합니다.
- 같은 변경에서 Java enum value와 validation을 database check에 맞춥니다.
- Aggregate ownership에 맞춰 foreign-key delete behavior를 검증합니다.
- Deployment가 겹칠 수 있으면 additive하고 backward-compatible한 migration을 우선합니다.

### 하지 말아야 할 일

- Schema management에 `ddl-auto=create`, `update` 또는 `create-drop`을 사용하지 않습니다.
- 검토된 compatibility 및 recovery plan 없이 populated column을 drop하거나 rename하지 않습니다.
- Populated data를 backfill하고 validate하기 전에 `NOT NULL`을 추가하지 않습니다.
- Database constraint 없이 application-level uniqueness check에 의존하지 않습니다.
- Migration에 credential, environment-specific owner 또는 production-only grant를 넣지 않습니다.
- Polymorphic `source_id`에 referential integrity가 있다고 가정하지 않습니다. 담당 application workflow에서 검증합니다.
