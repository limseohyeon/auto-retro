# Auto Retro 에이전트 관제 규칙

## 적용 범위와 규칙 우선순위

- 이 파일은 저장소 전체를 통제합니다.
- 경로를 수정하기 전에 가장 가까운 하위 `AGENTS.md`를 읽습니다. 하위 규칙은 이 파일에 추가로 적용됩니다.
- 규칙이 충돌하면 루트의 불변 규칙을 약화하지 않는 범위에서 수정 파일에 가장 가까운 규칙을 우선합니다.
- 모든 `AGENTS.md`는 500줄 미만으로 유지합니다. 중복을 제거하고 세부 지침은 담당 하위 파일로 위임합니다.

## 번역 원본 정책

- `AGENTS.ko.md` 파일만 에이전트 규칙의 편집 원본으로 사용합니다.
- `AGENTS.md` 파일은 Git에 커밋하는 영문 생성물이며 직접 수정하지 않습니다.
- 한글 원본을 변경한 뒤 사용 중인 에이전트의 방식으로 `agents-sync` skill을 명시적으로 호출하고(Codex: `$agents-sync`, Claude Code: `/agents-sync`) 영문 diff를 검토한 후 두 파일과 동기화 manifest를 함께 커밋합니다.
- Git hook과 CI는 동기화만 검증하며 파일을 번역하거나 커밋하지 않습니다.

## 프로젝트 컨텍스트와 운영

Auto Retro는 개발 기록을 저장하고 이를 AI 요약, 주간 회고, 발표 초안으로 변환합니다. 브라우저 클라이언트를 포함한 모듈러 모놀리스입니다.

- Backend: Java 21, Spring Boot 4.1.0, Spring Modulith 2.1.0, Gradle, JPA, Spring Batch.
- Database: CI에서는 PostgreSQL 17, Flyway migration.
- Frontend: React 19, TypeScript 6, Vite 8, React Router 8, TanStack Query 5, React Hook Form, Zod.
- Architecture: application port와 inbound/outbound adapter를 사용하는 domain module 구조이며 공통 HTTP 및 error contract는 `common`에 둡니다.

### 운영 명령어

명령 자체에서 디렉터리를 변경하지 않는 한 저장소 루트에서 실행합니다.

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

Unix에서는 `./gradlew`를 사용하고 CI에서는 `./gradlew verifyAll --no-daemon`을 사용합니다. Backend integration에는 PostgreSQL과 `.github/workflows/ci.yml`의 datasource 환경변수가 필요합니다.

## 핵심 규칙

### 불변 규칙

- 모듈 소유권을 지킵니다. 어떤 모듈도 다른 모듈의 JPA entity, repository 또는 내부 adapter를 import하면 안 됩니다.
- 의존성 방향을 `adapter/in -> application -> domain` 및 `adapter/out -> application port`로 유지합니다. domain과 application 코드는 MVC, JPA 또는 AI vendor SDK에 의존하면 안 됩니다.
- Flyway migration을 PostgreSQL schema의 유일한 기준으로 취급합니다. migration을 Hibernate schema generation으로 대체하지 않습니다.
- credential, access token, API key, production data 또는 값이 채워진 `.env` 파일을 커밋하지 않습니다. secret은 환경변수에서 읽습니다.
- 공개 API envelope를 유지합니다. 성공 응답은 `ApiResponse<T>`를 사용하고 실패 응답은 공통 exception handler를 통해 `ErrorResponse`를 사용합니다.
- 변경을 통과시키기 위해 test, static check, module verification, validation 또는 database constraint를 약화하거나 삭제, 생략, 비활성화하지 않습니다.
- Gradle wrapper와 lock된 npm dependency graph를 사용합니다. 생성된 `build/`, `dist/` 또는 `node_modules/` 내용을 커밋하지 않습니다.

### 해야 할 일

- 동작을 추가하기 전에 담당 모듈을 식별하고 필요한 최소 public contract만 노출합니다.
- inbound boundary에서 입력을 검증하고 필요에 따라 domain logic이나 database에서도 핵심 invariant를 다시 강제합니다.
- 외부 시스템, authentication, persistence, AI provider는 application이 소유한 port 뒤에 둡니다.
- 외부 AI integration을 추가할 때는 승인된 vendor의 공식 유지보수 SDK를 사용하고 outbound adapter에 격리합니다.
- 영향받은 package 옆에 focused test를 추가하고 완료 전에 `verifyAll`을 실행합니다.
- 변경이 여러 module, migration, external integration 또는 여러 작업 session에 걸치면 architecture documentation과 execution plan을 갱신합니다.

### 하지 말아야 할 일

- feature 내부 HTTP 코드로 `apiClient`를 우회하거나 frontend가 PostgreSQL에 직접 접근하게 하지 않습니다.
- JPA entity를 web response로 노출하거나 request/response DTO를 domain model로 사용하지 않습니다.
- 실제 consumer가 없는 추측성 port, abstraction, dependency 또는 shared utility를 추가하지 않습니다.
- 적용된 Flyway migration을 다시 작성하지 않고 다음 순번의 migration을 추가합니다.
- 환경별 URL, credential, user data 또는 provider model secret을 하드코딩하지 않습니다.
- feature나 fix에 관련 없는 refactor를 섞지 않습니다.

## 변경 작업 흐름

1. 이 파일, 연결된 하위 규칙, 관련 architecture document를 읽습니다.
2. 기존 code와 test를 조사합니다. package, command 또는 dependency가 있다고 추측하지 않습니다.
3. 여러 module, migration, external integration 또는 여러 session에 걸친 작업은 execution plan을 생성하거나 갱신합니다.
4. module boundary를 보존하는 가장 작은 vertical change를 구현합니다.
5. 가장 좁은 관련 check를 먼저 실행한 뒤 완료를 선언하기 전에 `verifyAll`을 실행합니다.
6. 변경된 동작, migration 또는 configuration, 검증 결과와 해결되지 않은 risk를 보고합니다.

## 표준과 참고 문서

- [`.editorconfig`](./.editorconfig)를 따릅니다. UTF-8, batch file 외에는 LF, final newline, Java/Gradle은 four spaces, YAML은 two spaces를 사용합니다.
- module ownership과 dependency direction은 [architecture definition](./docs/architecture-definition.ko.md)을 따릅니다.
- agent workflow와 verification policy는 [AI development guide](./docs/ai-guidelines.ko.md)를 따릅니다.
- 장기 또는 cross-cutting 작업은 [execution-plan guide](./docs/exec-plans/README.md)를 따릅니다.
- Java package는 lowercase, class는 PascalCase, method는 camelCase를 사용합니다. 역할을 명확히 할 때 `Controller`, `Service`, `Port`, `Adapter`, `JpaEntity` suffix를 사용합니다.
- TypeScript component filename은 PascalCase, hook은 `use`로 시작하며 ESLint와 Prettier를 기준으로 삼습니다.

### Git과 Pull Request

- 끝에 마침표 없이 `type: concise subject` 형식을 사용합니다.
- 일반적으로 허용하는 type은 `feat`, `fix`, `docs`, `test`, `refactor`, `move`, `chore`입니다.
- 각 commit에는 하나의 논리적 변경만 포함합니다.
- Pull request에는 동작 설명, issue가 있으면 link, 실행한 verification을 포함하고 migration, configuration change, screenshot, reviewer focus area를 명시합니다.

### 유지보수 정책

- 저장소 동작과 이 규칙이 어긋나면 오래된 지침을 조용히 따르지 않습니다. code와 executable configuration을 검증하고 같은 변경에서 가장 작은 규칙 수정을 제안하거나 적용합니다.
- 반복되거나 비용이 큰 실패는 database constraint, type, test, static analysis, CI 순으로 executable enforcement로 승격합니다. 안정적으로 코드화할 수 없는 판단에만 `AGENTS.md`를 사용합니다.
- 주요 architecture change 뒤에는 규칙 파일을 검토합니다. 오래된 지침, 중복, 깨진 link, 삭제된 path를 가리키는 route를 제거합니다.
- 새로 만들거나 수정하는 모든 `AGENTS.md`는 500줄 미만이고 불필요한 서술과 emoji가 없어야 합니다.

## 컨텍스트 지도

- **[Backend API, domain logic, persistence, batch 또는 module boundary](./src/main/java/com/devlog/auto_retro/AGENTS.md)** — Java production code 또는 module contract를 수정하기 전에 읽습니다.
- **[Frontend page, feature, routing, state, API client 또는 styling](./frontend/AGENTS.md)** — React와 TypeScript application 또는 frontend tooling을 수정하기 전에 읽습니다.
- **[Database schema 또는 Flyway migration](./src/main/resources/db/migration/AGENTS.md)** — schema, constraint, index 또는 data migration을 추가하거나 검토하기 전에 읽습니다.
- **[Cross-module execution plan](./docs/exec-plans/AGENTS.md)** — 작업이 여러 module, migration, external integration 또는 여러 session에 걸칠 때 읽습니다.

## 완료 기준

- 요청한 동작과 acceptance criteria를 충족합니다.
- test harness가 있는 변경 boundary에는 focused automated coverage가 있습니다.
- `verifyAll`이 통과하거나 최종 보고서에 실패한 정확한 command와 변경 외부의 검증된 원인을 포함합니다.
- documentation, migration note, configuration example, execution plan이 implementation과 일치합니다.
- secret, generated artifact, unrelated change 또는 약화된 guardrail이 포함되지 않습니다.
