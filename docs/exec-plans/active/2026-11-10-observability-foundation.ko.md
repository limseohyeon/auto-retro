# 목표

Auto Retro의 서버 상태와 Gemini 기반 AI 생성 실행을 운영 중에 추적할 수 있는 관측성 기반을 구축한다.

완료 조건:

- Elastic에서 HTTP, JVM, 데이터베이스, 외부 API의 핵심 로그·메트릭·Trace를 확인할 수 있다.
- Langfuse에서 Gemini 호출의 모델, 프롬프트 버전, 토큰, 비용, 지연시간, 상태와 출력 검증 결과를 확인할 수 있다.
- 한 AI 생성 요청을 공통 `trace_id`로 Elastic과 Langfuse에서 연결할 수 있다.
- 기본 설정에서는 프롬프트, 응답, 개발 기록과 비밀값 원문이 전송되지 않는다.
- test profile은 외부 관측 시스템에 연결하지 않으며 전체 `verifyAll`이 통과한다.

## 범위

- 포함:
  - OpenTelemetry 기반 애플리케이션 계측과 Collector 라우팅
  - Elastic 서버 애플리케이션 관측성
  - Langfuse 제품 LLM 관측성
  - `aigeneration` 모듈의 Gemini 호출과 출력 검증 계측
  - 환경별 exporter, sampling, 마스킹과 보관 정책
  - 핵심 대시보드, 알림 후보와 검증 절차
  - 제품 동작에 필요한 AI 실행 이력의 데이터 모델 결정
- 제외:
  - 저장소 개발에 사용하는 coding agent 활동 추적
  - 다중 Agent 오케스트레이션
  - 원문 프롬프트와 응답의 무조건 수집
  - 관측 데이터를 PostgreSQL에 복제하는 자체 분석 시스템
  - 기능과 무관한 애플리케이션 리팩터링
  - `README.md` 변경

## 현재 상태와 근거

- [Gradle 구성](../../../build.gradle)에 Actuator와 Spring Modulith observability 의존성이 있지만 Micrometer OpenTelemetry bridge, OTLP exporter와 관측 백엔드 연결은 없다.
- [애플리케이션 설정](../../../src/main/resources/application.yml)은 `health`, `info` endpoint만 노출한다. metrics, tracing과 exporter 설정은 없다.
- [`aigeneration` 모듈 설명](../../../src/main/java/com/devlog/auto_retro/aigeneration/package-info.java)은 AI 요청 유형, 성공 여부, 응답 시간과 오류 이력을 책임으로 정의한다.
- [초기 Flyway migration](../../../src/main/resources/db/migration/V1__create_initial_tables.sql)에는 생성 결과인 `summaries`가 있지만 AI 실행 이력 테이블은 없다.
- 현재 `aigeneration` 구현은 응답 검증 로직 중심이며 Gemini outbound adapter와 실제 생성 workflow는 아직 없다.
- [아키텍처 정의](../../architecture-definition.ko.md)와 [관측성 설계](../../observability.ko.md)는 Elastic과 Langfuse를 OpenTelemetry와 공통 `trace_id`로 연결하는 목표 구조를 정의한다.

### 확인된 제약

- 애플리케이션과 도메인 계층은 공급자 SDK, Elastic과 Langfuse 타입에 의존하지 않는다.
- 비밀값은 환경변수 또는 배포 secret으로 주입하고 저장소에 커밋하지 않는다.
- 프롬프트, 응답, 토큰과 사용자 기록은 승인된 redaction 정책 없이는 로그로 남기지 않는다.
- 관측 전송 실패가 제품 요청의 성공 여부를 바꾸지 않아야 한다.
- AI 실행 이력 스키마가 필요하면 기존 migration을 수정하지 않고 다음 Flyway migration을 추가한다.

### 미결정 사항

- Elastic과 Langfuse의 Cloud 또는 자가 호스팅 방식
- 운영 sampling 비율과 보관기간
- 원문 프롬프트·응답을 허용할 환경과 마스킹 규칙
- 초기 Gemini 모델과 가격 정보 관리 위치
- 첫 관측 대상 AI 유스케이스와 사용자에게 노출할 실행 상태

## 작업 순서

- [x] 1. 서버 관측성과 제품 LLM 관측성의 역할, 데이터 경계와 완료 기준을 문서화한다.
  - 검증: 아키텍처 정의, 관측성 설계, AI 엔지니어링 로드맵의 용어와 링크를 확인한다.
- [ ] 2. Cloud/자가 호스팅, 보관기간, sampling과 원문 수집 정책을 결정하고 결정 기록에 남긴다.
  - 검증: 배포 환경별 데이터 흐름, 비밀값과 개인정보 처리 방식이 모순 없이 정의되어야 한다.
- [ ] 3. Spring Boot, Micrometer와 OpenTelemetry 의존성 조합을 검증하고 test profile에서 exporter가 비활성화되는 최소 구성을 추가한다.
  - 검증: 애플리케이션 context 테스트와 관련 설정 테스트를 실행한다.
- [ ] 4. OpenTelemetry Collector에서 로그·메트릭·전체 Trace는 Elastic으로, LLM Trace는 Langfuse로 라우팅한다.
  - 검증: 로컬 검증 요청 하나가 의도한 목적지에만 전달되고 exporter 장애가 제품 요청을 실패시키지 않는지 확인한다.
- [ ] 5. 서버 관측 신호와 대시보드 초안을 구성한다.
  - 검증: HTTP 오류·지연, JVM, DB와 외부 API 실패를 재현하여 각 신호와 `trace_id`를 확인한다.
- [ ] 6. 첫 Gemini 생성 유스케이스의 outbound port와 adapter에 LLM span과 정규화 속성을 추가한다.
  - 검증: 성공, timeout, 공급자 오류, 파싱 실패와 도메인 검증 실패를 자동 테스트한다.
- [ ] 7. 제품 상태에 필요한 AI 실행 이력과 telemetry의 경계를 확정한다. 필요한 경우 다음 Flyway migration과 persistence adapter를 추가한다.
  - 검증: empty-schema migration, JPA mapping, 중복 실행과 재시도 시나리오를 PostgreSQL 17에서 확인한다.
- [ ] 8. Langfuse에서 LLM Trace, 토큰, 비용, 지연시간, 상태와 검증 결과를 확인하고 Elastic Trace와 연결한다.
  - 검증: 동일 요청의 `trace_id`를 두 시스템에서 조회하고 원문 민감정보가 없는지 검사한다.
- [ ] 9. 고정 입력과 평가 기준으로 첫 회귀 평가셋을 만들고 모델 또는 프롬프트 변경 비교 절차를 추가한다.
  - 검증: 의도적으로 품질을 낮춘 응답 또는 프롬프트가 기준 미달로 탐지되는지 확인한다.
- [ ] 10. 집중 검증과 전체 검증을 실행하고 운영 기준선 수집 후 알림 임계값을 확정한다.
  - 검증: 아래 명령과 수동 시나리오를 모두 실행하고 결과를 진행 기록에 남긴다.

## 결정 기록

- 2026-11-10: 서버 관측성은 Elastic, 제품 LLM 관측성은 Langfuse가 담당하고 OpenTelemetry Collector와 공통 `trace_id`로 연결한다.
  - 이유: Langfuse는 LLM 실행과 평가에 집중하고 서버 로그·메트릭 운영은 Elastic의 책임으로 유지하기 위해서다.
- 2026-11-10: Langfuse 관측은 다중 Agent 도입을 기다리지 않고 첫 Gemini 호출부터 적용한다.
  - 이유: 단일 호출에서도 지연시간, 토큰, 비용, 출력 검증과 프롬프트 회귀를 확인할 필요가 있다.
- 2026-11-10: 프롬프트와 응답 원문은 기본 비수집으로 시작한다.
  - 이유: 개발 기록과 사용자 데이터가 포함될 수 있으며 디버깅 편의보다 최소 수집 원칙을 우선한다.
- 2026-11-10: PostgreSQL에는 제품 복구와 상태 표시에 필요한 실행 이력만 저장하고 상세 telemetry는 중복 저장하지 않는다.
  - 이유: 제품 데이터와 운영 관측 데이터의 수명주기와 접근 목적이 다르기 때문이다.
- 2026-11-10: `README.md`는 이번 관측성 문서 변경 범위에서 제외한다.

## 검증

### 자동 검증

구현 단계에서 변경 범위에 맞는 집중 테스트를 먼저 실행한 후 전체 검증을 실행한다.

```powershell
.\gradlew.bat test
.\gradlew.bat verifyAll
```

추가할 자동 검증 항목:

- test profile에서 OTLP exporter가 외부 연결을 시도하지 않는다.
- 비밀값, 프롬프트, 응답과 개발 기록 원문이 span 속성이나 로그에 포함되지 않는다.
- 성공, timeout, 공급자 오류, 응답 파싱 실패와 도메인 검증 실패가 정규화된 상태로 기록된다.
- 관측 exporter가 실패해도 정의된 제품 응답과 AI 실행 상태가 유지된다.
- migration이 추가되면 PostgreSQL 17의 빈 데이터베이스와 JPA mapping에서 통과한다.

### 수동 검증

- 정상 AI 생성 요청을 실행하고 Elastic과 Langfuse에서 동일한 `trace_id`를 조회한다.
- Gemini timeout과 잘못된 구조화 응답을 재현하여 서버 오류와 LLM 검증 실패가 구분되는지 확인한다.
- Langfuse 화면에서 모델, 프롬프트 버전, 토큰, 비용, 지연시간과 결과 상태를 확인한다.
- Elastic에서 해당 요청의 HTTP, 애플리케이션, DB와 외부 API 실행 흐름을 확인한다.
- 관측 백엔드 또는 Collector를 중지한 상태에서 제품 요청이 정책대로 처리되는지 확인한다.

## 진행 기록

- 2026-11-10: 현재 문서와 설정, `aigeneration` 모듈, Flyway schema를 조사했다. 서버 관측성과 제품 LLM 관측성의 역할, 데이터 경계, 보안 기본값과 완료 기준을 문서화했다. 다음 시작점은 호스팅 방식, sampling, 보관기간과 원문 수집 정책 결정이다.
