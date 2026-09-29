# Auto Retro

개발 기록을 모아 요약하고 주간 회고와 발표 초안을 만드는 자동화 서비스입니다.

## 기술 구성

- Backend: Java 21, Spring Boot 4, Gradle, PostgreSQL, Flyway, Spring Modulith
- Frontend: React, TypeScript, Vite

## 검증

Node.js 의존성을 처음 한 번 설치한 뒤 저장소 루트에서 전체 검증을 실행합니다.

```powershell
cd frontend
npm ci
cd ..
.\gradlew.bat verifyAll
```

Linux와 CI에서는 `./gradlew verifyAll --no-daemon`을 사용합니다. 백엔드 통합 테스트에는 `application-test.yml`과 CI 워크플로에 정의된 PostgreSQL 접속 환경변수가 필요합니다.

## 프로젝트 문서

- [아키텍처 정의서](docs/architecture-definition.ko.md)
- [AI 개발 가이드](docs/ai-guidelines.ko.md)
- [AI 엔지니어링 로드맵](docs/ai-engineering-roadmap.ko.md)
- [실행 계획 운영법](docs/exec-plans/README.md)
- [에이전트 실행 규칙](AGENTS.ko.md)

## 에이전트 규칙 번역

`AGENTS.ko.md`가 사람이 편집하는 원본이며 `AGENTS.md`는 지원되는 coding agent가 읽는 영문 생성물입니다. 한글 규칙을 변경한 뒤 사용하는 도구에서 `agents-sync` skill을 명시적으로 호출하고 두 파일을 함께 커밋합니다. Codex에서는 `$agents-sync`, Claude Code에서는 `/agents-sync`를 사용합니다.

저장소를 처음 받은 뒤 버전 관리되는 pre-push hook을 한 번 등록합니다.

```powershell
.\gradlew.bat installGitHooks
```

동기화 상태만 확인하려면 다음 명령을 실행합니다.

```powershell
node scripts/agent-sync.mjs check
```

pre-push hook과 `verifyAll`은 번역을 수행하지 않으며, 동기화되지 않은 규칙이 있으면 실패합니다.
