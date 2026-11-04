# 목표

`GET /api/dev-records`로 사용자의 개발 기록 목록을 오프셋 페이지 단위로 조회한다. 제목 키워드 검색과 난도 필터를 단독 또는 동시에 적용할 수 있다.

완료 조건:

- 요청 파라미터: `userId`(필수), `page`(기본값 0, 허용 범위 0~10,000), `keyword`, `difficulty`
- 페이지 크기 10 고정, `recordDate DESC, recordId DESC` 정렬, 요청한 사용자의 기록만 조회
- 응답: `ApiResponse`의 `data`에 `content`, `page`, `totalElements`, `totalPages`
  - `content[]` 항목: `recordId`(문자열), `title`, `difficulty`, `workPreview`, `tags`(항상 `[]`), `recordDate`
  - 결과가 없으면 200과 빈 목록을 반환한다. 허용 범위(0~10,000) 안에서 데이터가 없는 페이지는 `content`만 비우고 전체 개수와 페이지 수는 유지한다.
  - 400 `COMMON-001`: `userId` 누락 또는 long이 아님, `page < 0`, `page > 10,000`(`MAX_PAGE`), `page`가 정수가 아님, `difficulty`가 허용값이 아님
- `verifyAll` 통과

## 범위

- 포함: `devrecord` 모듈의 목록 조회(web, application, port, persistence), `common`의 `GlobalExceptionHandler` 400 처리 보강, 관련 테스트
- 제외:
  - 태그 조회와 Tag 엔티티(AI 태그 추출 기능에서 구현)
  - 인증(`userId` 파라미터는 임시 유지)
  - 프론트엔드

## 현재 상태와 근거

- 웹:
  - [DevRecordController](../../../src/main/java/com/devlog/auto_retro/devrecord/adapter/in/web/controller/DevRecordController.java)의 `getDevRecords`
  - [DevRecordListResponse](../../../src/main/java/com/devlog/auto_retro/devrecord/adapter/in/web/response/DevRecordListResponse.java)
  - [DevRecordSummaryResponse](../../../src/main/java/com/devlog/auto_retro/devrecord/adapter/in/web/response/DevRecordSummaryResponse.java)
- application:
  - [DevRecordSearchCondition](../../../src/main/java/com/devlog/auto_retro/devrecord/application/dto/DevRecordSearchCondition.java): 키워드 정규화, `PAGE_SIZE`, `MAX_PAGE`
  - [DevRecordSummary](../../../src/main/java/com/devlog/auto_retro/devrecord/application/dto/DevRecordSummary.java): `workPreview()`
  - [DevRecordSummaryPage](../../../src/main/java/com/devlog/auto_retro/devrecord/application/dto/DevRecordSummaryPage.java)
  - [DevRecordLoadPort](../../../src/main/java/com/devlog/auto_retro/devrecord/application/port/out/DevRecordLoadPort.java)의 `loadPage`
  - [DevRecordListService](../../../src/main/java/com/devlog/auto_retro/devrecord/application/service/DevRecordListService.java)
- persistence:
  - [DevRecordJpaRepository](../../../src/main/java/com/devlog/auto_retro/devrecord/adapter/out/persistence/repository/DevRecordJpaRepository.java)의 `findSummaries`(JPQL, 인터페이스 프로젝션, 명시적 `countQuery`)
  - [DevRecordSummaryView](../../../src/main/java/com/devlog/auto_retro/devrecord/adapter/out/persistence/repository/DevRecordSummaryView.java)
  - [DevRecordPersistenceAdapter](../../../src/main/java/com/devlog/auto_retro/devrecord/adapter/out/persistence/DevRecordPersistenceAdapter.java)의 `loadPage`(LIKE 이스케이프)
- 공통 에러: [GlobalExceptionHandler](../../../src/main/java/com/devlog/auto_retro/common/error/GlobalExceptionHandler.java)
  - `MissingServletRequestParameterException`과 `HandlerMethodValidationException`을 400 `COMMON-001`로 변환한다.
  - 변경 전에는 두 예외를 `Exception` 핸들러가 받아 500을 반환했고, 테스트로 확인했다.
- 테스트:
  - [DevRecordControllerTests](../../../src/test/java/com/devlog/auto_retro/devrecord/adapter/in/web/controller/DevRecordControllerTests.java)
  - [DevRecordSearchConditionTests](../../../src/test/java/com/devlog/auto_retro/devrecord/application/dto/DevRecordSearchConditionTests.java)
  - [DevRecordSummaryTests](../../../src/test/java/com/devlog/auto_retro/devrecord/application/dto/DevRecordSummaryTests.java)
  - [DevRecordListServiceTests](../../../src/test/java/com/devlog/auto_retro/devrecord/application/service/DevRecordListServiceTests.java)
  - [DevRecordPersistenceAdapterTests](../../../src/test/java/com/devlog/auto_retro/devrecord/adapter/out/persistence/DevRecordPersistenceAdapterTests.java)

### 위험

- 해소: PostgreSQL NULL 파라미터 타입 추론(`could not determine data type of parameter $n`)
  - 2026-10-23 영속성 테스트에서 `keyword`와 `difficulty`가 모두 `null`, 하나만 `null`, 둘 다 값이 있는 조합이 PostgreSQL 17에서 모두 통과했다.
- 해소: 매우 큰 `page`(예: `2147483647`)
  - offset(`page * 10`)이 `Integer.MAX_VALUE`를 넘으면 Spring Data JPA가 예외를 던져 500이 될 수 있었다. 이 동작은 재현하지 않았다.
  - 2026-10-25에 `page` 상한 `MAX_PAGE = 10_000`을 두어, 그보다 큰 값은 DB에 닿기 전에 400으로 거절하도록 했다. offset 최대값은 100,000이다.

## 작업 순서

- [x] 1. `GlobalExceptionHandler`에 누락 파라미터와 메서드 Validation 실패 처리를 추가한다.
  - 검증: 상세 조회 `userId` 누락 테스트가 수정 전 500으로 실패하고 수정 후 400으로 통과했다(2026-10-23).
- [x] 2. application 계층을 추가한다. 검색 조건, 요약 DTO와 페이지 DTO, `loadPage` 포트, 읽기 전용 서비스.
  - 검증: `DevRecordSearchConditionTests` 4개, `DevRecordSummaryTests` 3개, `DevRecordListServiceTests` 1개가 통과했다(2026-10-23).
- [x] 3. persistence 계층을 구현한다. JPQL 프로젝션 페이지 쿼리, LIKE 이스케이프.
  - [x] 구현
  - [x] PostgreSQL 영속성 테스트 10개 통과(2026-10-23 15:45 KST, 임시 `postgres:17-alpine` 컨테이너, 포트 55432, 검증 절의 환경 변수 사용)
- [x] 4. web 계층을 추가한다. `getDevRecords`, 응답 레코드, `tags`는 `List.of()`로 고정하고 TODO를 남김.
  - 검증: `DevRecordControllerTests` 13개가 통과했다(목록 조회 8개 포함, 2026-10-23 핸들러 최종 수정 이후 실행).
- [x] 5. `package-info.java`를 확인했다. 변경할 필요가 없다(2026-10-24).
- [x] 6. `verifyAll`을 실행한다(`ModularStructureTests`, `AutoRetroApplicationTests` 포함).
  - 2026-10-25 3차: **통과.** `BUILD SUCCESSFUL in 1m 1s`, 테스트 41개 전부 통과(검증 절 참고).
  - 2026-10-26 커밋 상태(`690c26d`) 재실행: **통과.** `BUILD SUCCESSFUL in 1m 2s`, 테스트 43개 전부 통과.
- [x] 7. 수동 확인(Postman). 사용자가 2026-10-24에 완료했다.
  - IntelliJ로 실행한 서버에서 `page=-1` 요청 시 `fieldErrors[0].field`가 `"page"`로 반환됐다.
  - `keyword`, `difficulty`, `page=999` 조건의 응답 형식을 확인했다.
- [x] 8. `page` 상한(`MAX_PAGE = 10_000`)을 추가한다.
  - `DevRecordSearchCondition.MAX_PAGE` 상수를 추가하고, 컨트롤러 `page`에 `@Max(DevRecordSearchCondition.MAX_PAGE)`를 붙였다. 기존 `@Min(0)`과 같은 검증 경로를 쓴다.
  - 검증: 경계값 컨트롤러 테스트 2개를 추가했다(2026-10-25).
    - `page=10000`이면 200과 빈 `content`를 반환하고 전체 개수를 유지한다.
    - `page=10001`이면 400 `COMMON-001`, `field="page"`를 반환하고 서비스는 호출하지 않는다.
  - 컨트롤러 테스트 15개와 `verifyAll`이 통과했다.

## 결정 기록

- 2026-10-23: `recordId`는 응답에서 문자열로 반환하고, 내부 ID는 long을 유지한다.
  - 이유: 기존 응답과 백엔드 규칙에 맞추고, JavaScript의 정밀도 손실을 막는다.
- 2026-10-23: `keyword`는 제목 부분 일치, 대소문자 무시로 검색한다. `\`, `%`, `_`는 `\`로 이스케이프한다.
  - 공백 정규화는 application(`DevRecordSearchCondition`)이, LIKE 이스케이프는 persistence adapter가 맡는다.
- 2026-10-23: `tags`는 web 응답에서만 `[]`로 채운다. 태그 조회와 엔티티는 만들지 않는다.
  - 이유: AI 태그 추출 기능에서 구현할 예정
- 2026-10-23: 새 포트를 만들지 않고 기존 `DevRecordLoadPort`에 `loadPage`를 추가한다.
  - 이유: 같은 조회 책임이고 구현체도 같다.
- 2026-10-23: 미리보기는 code point 기준으로 150자까지 자르고 `…`를 붙인다. DB에서는 `substring(…, 1, 151)`만 가져온다.
  - 이유: 이모지 같은 문자가 중간에 잘리지 않도록 하고, PostgreSQL과 Java에서 글자 수를 일관되게 처리하기 위해서다.
- 2026-10-23: 동적 조건은 QueryDSL이나 Specification이 아니라 JPQL의 `(:param is null or …)`로 처리한다.
  - 이유: 조건 2개, 정렬 고정, 조인 없음이라 충분하고, 새 의존성이 필요 없다.
  - 대가: `countQuery`에 조건이 중복된다.
  - 다시 검토할 시점: 태그 조인이나 검색 조건 추가가 확정될 때.
- 2026-10-23: `page < 0`은 `@Min(0)`으로 400을 반환한다.
- 2026-10-23: `HandlerMethodValidationException`의 `field`는 `@RequestParam`/`@PathVariable`에 명시한 이름을 우선 쓰고, 없을 때 `getParameterName()`을 쓴다.
  - 이유: IntelliJ 자체 빌드(`delegatedBuild=false`)에는 `-parameters`가 없어 Postman 응답의 `field`가 `null`이었다.
  - 한계: Gradle 테스트는 항상 `-parameters`로 컴파일되어 이 상황을 재현할 수 없다.
  - 권장 환경 조치: IntelliJ에서 *Build and run using: Gradle* 설정.
- 2026-10-25: page의 허용 범위를 0~10,000으로 제한하고, 범위를 벗어나면 400 COMMON-001을 반환한다.
  - 이유: 과도한 OFFSET 조회와 오버플로 가능성을 방지한다. 범위 초과를 빈 목록으로 처리하는 대신 요청값 검증으로 통일한다.
  - 허용 범위 안에서 데이터가 없는 페이지는 기존대로 200과 빈 목록을 반환한다.

## 검증

### 테스트 환경 및 실행 명령어

- 환경: JDK 21, Node.js 24, PostgreSQL 17
- 테스트 DB 접속 값: [CI 워크플로](../../../.github/workflows/ci.yml)의 `SPRING_PROFILES_ACTIVE`, `SPRING_DATASOURCE_*`, `SPRING_BATCH_JDBC_INITIALIZE_SCHEMA` 환경 변수를 참고한다.
- 로컬 검증: Docker `postgres:17-alpine`을 호스트 포트 55432로 실행했다.
```powershell
.\gradlew.bat verifyAll
```

### 검증 결과

**2026-10-26: 커밋 상태(`690c26d`)에서 `verifyAll` 통과 (`BUILD SUCCESSFUL in 1m 2s`)**

- 백엔드 테스트 43개 전체 통과(12개 테스트 클래스, skip·실패·오류 0건)
- Spring Modulith 모듈 경계 및 애플리케이션 컨텍스트 테스트 통과
- 프론트엔드 빌드, 포맷 검사, Lint 통과
- AGENTS 번역 동기화 검사 통과

**주요 테스트 항목**

| 구분 | 검증 내용 | 결과 |
|---|---|---|
| 목록 조회 | 사용자별 조회, 정렬, 페이지네이션, 빈 목록 | 통과 |
| 검색 및 필터 | 제목 키워드, 난도, 복합 조건, 특수문자 이스케이프 | 통과 |
| 응답 형식 | 문자열 `recordId`, `tags=[]`, 페이지 정보, 150자 미리보기 | 통과 |
| 예외 처리 | 잘못된 `userId`, `page`, `difficulty`에 대한 400 `COMMON-001` | 통과 |
| 페이지 상한 | `page=10000`이면 200과 빈 목록, `page=10001`이면 400과 `field="page"` | 통과 |
| 영속성 | PostgreSQL 환경에서 조회 조건 및 NULL 조합 | 통과 |
| 수동 검증 | Postman으로 검색·필터 및 페이지네이션 확인. `page=10000`은 200과 빈 목록, `page=10001`은 400 응답 확인 | 완료 |

## 진행 기록

- **2026-10-23**
    - API 명세를 검토하고 설계 방향을 확정했다.
    - 목록 조회 API와 관련 테스트를 구현했다.
    - 요청 파라미터 검증 실패 시 500을 반환하는 문제를 발견하고 `GlobalExceptionHandler`를 수정했다.
    - PostgreSQL 영속성 테스트 10개를 통과했다.

- **2026-10-24**
    - IntelliJ 환경에서 `page=-1` 요청 시 오류 응답의 `field`가 `null`로 반환되는 문제를 수정했다.
    - `DevRecordPersistenceAdapterTests`를 복구하고 10개 테스트가 통과하는 것을 확인했다.
    - Postman으로 페이지네이션 및 검색·필터 기능을 검증했다.

- **2026-10-25**
    - 로컬 실행환경 문제를 해결하고 `verifyAll`을 통과했다(전체 테스트 41개 성공).
    - `page` 상한 `MAX_PAGE = 10_000`을 추가하고 경계값 컨트롤러 테스트 2개를 추가했다. `verifyAll`을 다시 통과했다(전체 테스트 43개 성공).

