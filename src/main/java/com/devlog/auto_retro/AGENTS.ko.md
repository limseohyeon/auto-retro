# Backend 모듈 규칙

## 모듈 컨텍스트

이 디렉터리는 Spring Boot modular monolith를 포함합니다. Spring Modulith는 명시적으로 annotation된 top-level package를 application module로 탐지합니다.

- `user`는 user identity와 `users` table을 소유합니다.
- `devrecord`는 development record와 tag를 소유합니다.
- `aigeneration`은 AI request execution과 generated summary lifecycle을 소유합니다.
- `weeklyreport`는 period-based retrospective generation을 소유합니다.
- `presentation`은 record 또는 report identifier 기반 presentation draft를 소유합니다.
- `common`은 cross-cutting HTTP envelope와 error infrastructure만 소유하며 business logic을 쌓아 두는 곳이 아닙니다.

Cross-module data flow는 `user -> devrecord -> aigeneration -> weeklyreport -> presentation`입니다. identifier, immutable DTO, event 또는 의도적으로 공개한 application API를 공유합니다. persistence 내부 구현을 공유하지 않습니다.

## 기술 스택과 제약조건

- Java 21은 Gradle toolchain에서 선택합니다.
- Spring Boot 4.1.0, Spring Modulith 2.1.0, Spring MVC, Validation, JPA, Batch, Flyway, PostgreSQL, Lombok은 `build.gradle`에서 관리합니다.
- Constructor injection이 필수입니다. Lombok `@RequiredArgsConstructor`는 허용하지만 field injection은 허용하지 않습니다.
- Application과 domain 코드는 JPA entity, repository, controller, vendor SDK와 독립적이어야 합니다.
- `spring.jpa.open-in-view`는 비활성화되어 있습니다. 필요한 data는 application transaction 안에서 load하거나 mapping합니다.
- Read service에는 `@Transactional(readOnly = true)`를 사용하고 state change에는 명시적인 write transaction을 사용합니다.

## 구현 패턴

### Package와 의존성 방향

다음 구조는 필요한 경우에만 사용하며 빈 layer를 만들지 않습니다.

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

- Inbound adapter는 transport data를 검증하고 변환한 뒤 application use case를 호출합니다.
- Application service는 business rule을 조정하고 자신이 소유한 outbound port interface에 의존합니다.
- Outbound adapter는 port를 구현하고 persistence 또는 provider model을 application/domain type으로 mapping합니다.
- 실제 boundary 또는 substitution point에만 interface를 만듭니다. 단순한 module-local CRUD는 간결하게 유지할 수 있습니다.
- module responsibility나 exposed contract가 바뀌면 `package-info.java`를 정확하게 갱신합니다.

### Web과 Error Contract

- Controller는 성공한 JSON response에 `ApiResponse<T>`를 반환합니다.
- Application DTO에서 response record로 mapping하며 `JpaEntity`를 serialize하지 않습니다.
- Browser에 노출되는 `long` identifier는 JavaScript precision loss를 피하기 위해 decimal string으로 표현합니다.
- Jakarta Validation으로 request record를 검증하고 controller boundary에서 validation을 활성화합니다.
- 담당 module에 business error를 `ErrorCode` implementation으로 정의하고 `BusinessException`을 throw합니다.
- 실제로 cross-module인 failure만 `CommonErrorCode`에 추가합니다.
- 예상하지 못한 exception detail은 server log에 남기고 stack trace나 민감한 내부 정보를 반환하지 않습니다.

### Persistence와 일관성

- Persistence class는 `<Aggregate>JpaEntity`, `<Aggregate>JpaRepository`, `<Capability>PersistenceAdapter`로 이름을 정합니다.
- Database name, nullability, length, uniqueness, enum을 Flyway와 명시적으로 일치하도록 mapping합니다.
- Concurrency-sensitive invariant는 사전 existence query만 사용하지 말고 database constraint 또는 문서화된 locking strategy로 보호합니다.
- Association은 담당 module 내부에 둡니다. Cross-module reference는 JPA relationship이 아닌 scalar ID로 표현합니다.
- Downstream processing을 분리해야 할 때는 durable state change 뒤에 module event를 publish합니다.

### 외부 및 AI Integration

- 담당 application port에 capability와 provider-neutral DTO를 정의합니다.
- SDK type, prompt transport, retry, timeout, rate limit, provider error mapping을 outbound adapter에 둡니다.
- 명시적인 dependency review 뒤에 provider의 공식 maintained SDK를 사용하고 Gradle에서 version을 고정합니다.
- 문서화된 redaction policy가 허용하지 않으면 prompt, response, token 또는 user record를 log에 남기지 않습니다.
- AI, batch, event-driven operation의 retry와 idempotency 동작을 명시합니다.

## 테스트 전략

Production package를 `src/test/java` 아래에 동일하게 구성하고 test class 이름은 `*Tests`로 정합니다. Method 이름은 observable behavior를 나타냅니다.

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

- 가능한 경우 Spring context 없이 domain rule을 unit test합니다.
- Validation, status code, response envelope에는 focused MVC test를 사용합니다.
- Mapping, constraint, transaction, query에는 persistence 또는 integration test를 사용합니다.
- Module을 추가하거나 공개할 때 `ModularStructureTests` expectation을 갱신합니다.
- PostgreSQL 기반 test는 `test` profile과 `.github/workflows/ci.yml`의 datasource 환경변수를 사용합니다.

## 로컬 핵심 규칙

### 해야 할 일

- 담당 module이 business vocabulary, error, port, invariant를 정의하게 합니다.
- Boundary 또는 use-case entry에서 input을 한 번 normalize하고 observable result를 test합니다.
- Web, application, domain, persistence model 사이의 mapping을 명시적으로 유지합니다.
- 새 column, constraint 또는 index에 의존하기 전에 Flyway migration을 추가합니다.
- 관련된 경우 duplicate, missing, invalid, unauthorized, retry, concurrent case를 test합니다.

### 하지 말아야 할 일

- Spring Data repository를 다른 module이나 controller에 직접 inject하지 않습니다.
- Domain object에 web, persistence 또는 provider annotation을 붙이지 않습니다.
- Empty collection, `null`이 아닌 port의 `Optional` 또는 정의된 error가 contract인 곳에서 반환 규칙을 지킵니다.
- Failure를 숨기기 위해 business service에서 `Exception`을 catch하지 않습니다. 올바른 adapter boundary에서 exception을 변환합니다.
- Bidirectional module dependency를 도입하지 않습니다.
- Frontend consumer와 contract test를 갱신하지 않고 API field 또는 error code를 변경하지 않습니다.
