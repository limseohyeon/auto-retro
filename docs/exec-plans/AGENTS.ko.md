# 실행 계획 규칙

## 모듈 컨텍스트

Execution plan은 여러 module에 걸치거나 schema migration 또는 external integration을 포함하거나 여러 작업 session 동안 계속되는 변경의 결정과 진행 상태를 보존합니다. 작은 local edit에는 plan이 필요하지 않습니다.

- 진행 중인 plan은 `active/`에 둡니다.
- Implementation과 verification이 끝난 뒤에만 완료된 plan을 `completed/`로 이동합니다.
- `YYYY-MM-DD-short-topic.ko.md` filename을 사용합니다.
- `README.md`의 필수 section을 따릅니다.

## 기술 스택과 제약조건

- Plan은 Markdown이며 시작한 conversation에 접근하지 않아도 이해할 수 있어야 합니다.
- Repository file은 plan directory 기준으로 resolve되는 relative Markdown link로 연결합니다.
- 검증된 fact를 assumption 및 pending decision과 구분해 기록합니다.
- Credential, token, personal data, raw production payload 또는 민감한 log를 포함하지 않습니다.

## 구현 패턴

각 plan에는 다음을 정의합니다.

1. User-visible goal과 측정 가능한 completion criteria.
2. 포함하거나 제외하는 scope.
3. 관련 code, test, schema, documentation link를 포함한 current state.
4. 독립적으로 검증할 수 있는 작고 순서가 있는 task.
5. 관련된 경우 rationale과 rejected alternative를 포함한 날짜별 decision.
6. 정확한 automated command와 필요한 manual scenario.
7. Completed work, failure, discovery, 다음 starting point를 포함한 날짜별 progress log.

Implementation 중에 plan을 갱신하며 마지막에만 갱신하지 않습니다. Evidence로 design이 바뀌면 task list를 수정하고 decision을 기록합니다.

## 테스트 전략

- 모든 implementation step에는 focused verification을 명시합니다.
- Final gate에는 root `verifyAll` command를 포함합니다.
- Database change에는 empty-schema migration과 upgrade-path check를 포함합니다.
- API change에는 contract, validation, error case를 포함합니다.
- UI change에는 관련 route, state, accessibility, responsive check를 포함합니다.
- Check를 실행할 수 없으면 정확한 command, failure, impact, 필요한 follow-up을 기록합니다.

## 로컬 핵심 규칙

### 해야 할 일

- 다른 engineer가 다음 unchecked item부터 재개할 수 있을 만큼 plan을 최신 상태로 유지합니다.
- 큰 excerpt를 복사하지 말고 claim을 code 또는 executable configuration에 link합니다.
- Migration, configuration change, rollout order, recovery, reviewer focus를 명시적으로 기록합니다.
- Current plan을 완료하기 전에 follow-up debt를 별도 issue 또는 plan으로 분리합니다.

### 하지 말아야 할 일

- Plan을 speculative backlog나 daily activity log로 사용하지 않습니다.
- 명시한 verification이 통과하기 전에 work를 complete로 표시하지 않습니다.
- Future work에 필요한 failed approach나 decision history를 지우지 않습니다.
- Required work 또는 알려진 blocking risk가 남아 있는 동안 plan을 `completed/`로 이동하지 않습니다.
