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

### Java 반복 코드 최소화

- 기계적으로 반복되는 생성자, accessor와 logger 선언은 Java 21 language feature 또는 Lombok으로 생성합니다.
- 불변 data carrier, command, response와 value object는 일반 class보다 Java `record`를 우선합니다.
- Spring component의 constructor injection은 `final` field와 `@RequiredArgsConstructor`를 사용합니다.
- 단순 불변 class와 enum의 constructor는 `@RequiredArgsConstructor`, 필요한 accessor는 `@Getter`를 사용합니다.
- Interface가 `status()`처럼 fluent accessor를 요구할 때만 `@Accessors(fluent = true)`를 함께 사용합니다.
- Logger는 직접 선언하지 않고 `@Slf4j`를 사용합니다.
- `@Data`, class-level `@Setter`와 무분별한 `@AllArgsConstructor`는 사용하지 않습니다.
- `@Builder`는 선택적 field가 많은 객체에만 사용하며 필수 생성 절차나 domain invariant를 우회하지 않습니다.
- Validation, normalization, defensive copy 또는 부가 동작이 필요한 constructor와 accessor는 직접 작성합니다.
- JPA entity에는 `@Data`, class-level `@Setter` 또는 모든 field를 포함하는 `equals`, `hashCode`, `toString` 생성을 적용하지 않습니다.

표준 불변 DTO:

```java
public record RawTag(String name, String category) {
}
```

표준 constructor injection:

```java
@Service
@RequiredArgsConstructor
public class TaggingService {

    private final TagGenerationPort generationPort;
    private final TagSavePort savePort;
}
```

Fluent contract를 구현하는 표준 enum:

```java
@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor
public enum ExampleErrorCode implements ErrorCode {

    RESOURCE_NOT_FOUND(404, "EXAMPLE-001", "요청한 리소스를 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;
}
```

표준 logger 선언:

```java
@Slf4j
@Component
public class AiGenerationAdapter {
}
```

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

- `in`과 `out`은 담당 모듈을 기준으로 정합니다. 먼저 호출자와 필요한 기능을 확인한 뒤 다음 위치를 선택합니다.
  - HTTP 요청을 받으면 `adapter/in/web`에서 입력을 검증·변환하고 유스케이스를 호출합니다.
  - 호출자에게 제공할 유스케이스 계약은 `application/port/in`에 둡니다. `application/service`에서 계약을 구현하고 업무 흐름을 조정합니다.
  - 서비스가 DB 조회나 AI 호출을 요청하면 서비스에 필요한 계약을 `application/port/out`에 둡니다. `adapter/out`에서 이를 구현하고 저장소·공급자 타입을 애플리케이션 타입으로 변환합니다.
  - 다른 모듈이 호출할 계약과 불변 DTO만 명시적으로 공개합니다. 하위 패키지를 공개할 때는 전용 패키지의 `package-info.java`에 `@NamedInterface`를 선언하고 내부 DTO, 출력 포트, 서비스 구현체는 내부에 유지합니다.
- 개발 기록 조회의 호출 예시:
  - HTTP 요청: `DevRecordController` → 상세 조회 서비스 → `DevRecordLoadPort` → `DevRecordPersistenceAdapter`.
  - 모듈 간 조회 설계 예시: 태그 생성 서비스 → 공개 입력 포트 `DevRecordSourceQuery` → 구현 서비스 → 내부 `DevRecordLoadPort`. 구현 서비스는 사용자 소유권을 확인한 뒤 공개 DTO를 반환합니다.
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
