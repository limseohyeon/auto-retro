# AI 개발 가이드

## 문서 목적

이 문서는 프로젝트에서 사용하는 AI 개발 규칙을 개발자가 이해하기 쉽도록 한글로 설명한다.

- AI가 항상 읽는 공통 규칙의 원본은 저장소 루트의 `AGENTS.md`다.
- 이 문서는 `AGENTS.md`, `CLAUDE.md`, Cursor MDC 규칙에서 자동으로 가져오지 않는다.
- 자세한 배경 설명이 필요할 때 개발자가 직접 읽거나 AI에게 명시적으로 참고하도록 요청한다.

## 공통 원칙

AI 도구는 Cursor, Codex, Claude Code 중 무엇을 사용해도 된다. 도구별 응답 차이보다 저장소에 기록된 아키텍처 규칙, 테스트, 코드 형식과 CI 결과를 공통 기준으로 사용한다.

```text
AI 도구별 구현
→ 프로젝트 규칙 적용
→ Gradle 테스트
→ GitHub Actions 검증
```

AI 규칙은 실수를 줄이기 위한 안내이며, 테스트와 CI를 대신하지 않는다.

## 기술 구성

프로젝트의 기본 기술 구성은 다음과 같다.

- Java 21
- Spring Boot 4
- Gradle
- PostgreSQL
- Spring Data JPA
- Flyway
- Spring Batch
- Spring Modulith

새로운 운영 의존성은 실제 요구사항이 생겼을 때 추가한다. 확장 가능성만을 이유로 메시지 브로커, 검색엔진 또는 별도의 데이터 저장소를 미리 추가하지 않는다.

## 아키텍처

프로젝트는 하나의 애플리케이션으로 배포하는 모듈러 모놀리스 구조를 사용한다. 코드는 Controller, Service, Repository 같은 기술 종류만으로 묶지 않고 개발 기록, AI 생성, 주간 리포트, 발표 초안처럼 업무 기능을 기준으로 나눈다.

```text
애플리케이션
├─ 개발 기록 모듈
├─ AI 생성 모듈
├─ 주간 리포트 모듈
├─ 발표 초안 모듈
└─ 사용자 모듈
```

한 모듈은 다른 모듈의 내부 Repository, JPA Entity 또는 구현 클래스를 직접 사용하지 않는다. 다른 모듈이 공개한 애플리케이션 기능이나 명시적인 인터페이스를 통해 접근한다.

## 포트와 어댑터

DB, AI 모델, 외부 API처럼 애플리케이션 밖의 기술과 연결되는 경계에는 포트와 어댑터를 사용한다.

```text
Controller 또는 Batch
→ 입력 포트
→ 애플리케이션 서비스
→ 출력 포트
→ JPA·Spring AI·외부 API 어댑터
```

예를 들어 애플리케이션 서비스가 `JpaRepository`를 직접 사용하는 대신 저장 기능을 포트로 정의하고, JPA 어댑터가 그 포트를 구현한다.

모든 단순 CRUD에 인터페이스를 추가하지는 않는다. 외부 기술 교체, 실패 처리, 독립 테스트 또는 모듈 경계처럼 분리할 실질적인 이유가 있을 때 적용한다.

## 데이터베이스

DB 스키마의 기준은 Flyway 마이그레이션이다. 테이블이나 제약조건을 변경할 때는 버전이 있는 마이그레이션 파일을 추가한다.

```text
src/main/resources/db/migration
├─ V1__create_initial_tables.sql
└─ V2__add_weekly_report_constraint.sql
```

중복 생성처럼 동시 실행에 영향을 받는 규칙은 애플리케이션 조회에만 의존하지 않고 유니크 제약조건 등 DB 수준의 보호도 함께 적용한다.

## API와 환경설정

외부 요청값은 Controller 같은 입력 경계에서 검증하고, 비즈니스 오류는 공통 예외와 오류 응답 형식을 통해 반환한다.

환경별 값은 Spring Profile로 분리한다.

```text
application.yml
application-local.yml
application-test.yml
```

DB 비밀번호와 AI API Key는 환경변수로 전달한다. 실제 비밀값, `.env` 파일과 개인 인증 설정은 Git에 커밋하지 않는다.

## 테스트와 검증

Windows 로컬 환경에서는 다음 명령을 사용한다.

```powershell
.\gradlew.bat test
```

Linux와 GitHub Actions에서는 다음 명령을 사용한다.

```bash
./gradlew test --no-daemon
```

기능을 변경하면 관련 단위 테스트와 통합 테스트를 실행한다. REST와 Batch가 동일한 유스케이스를 호출하는 경우 핵심 비즈니스 규칙은 애플리케이션 서비스 테스트에서 검증한다.

## Git 작업 규칙

저장소의 Commit Template과 Conventional Commits 유형을 따른다. 하나의 Commit에는 하나의 목적을 담고, 작업과 관련 없는 파일을 함께 수정하지 않는다.

AI가 기존 변경사항을 발견하면 임의로 되돌리거나 덮어쓰지 않고 현재 작업과 충돌하는지 먼저 확인한다.

