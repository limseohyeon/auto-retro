# Frontend 모듈 규칙

## 모듈 컨텍스트

`frontend`는 Auto Retro browser client를 render하는 독립 npm package입니다. Spring Boot의 `/api` surface와만 통신합니다.

- `src/app`은 application composition, provider, layout, routing을 소유합니다.
- `src/pages`는 route-level composition을 소유하며 얇게 유지합니다.
- `src/features`는 user-facing workflow, feature API function, state, UI를 소유합니다.
- `src/shared`는 business ownership이 없는 reusable API infrastructure, configuration, component, style, type을 소유합니다.

Dependency는 `app`과 `pages`에서 `features`와 `shared` 방향으로 흐릅니다. `shared`는 `features`, `pages` 또는 `app`에서 import하면 안 됩니다.

## 기술 스택과 제약조건

- React 19.2, strict mode의 TypeScript 6, Vite 8, npm lockfile resolution을 사용합니다.
- React Router 8이 navigation과 route protection을 담당합니다.
- TanStack Query 5가 remote server state와 cache lifecycle을 담당합니다.
- 중요 form state와 validation은 React Hook Form과 Zod가 담당합니다.
- Native `fetch`는 `src/shared/api/apiClient.ts`로 감쌉니다. Axios나 feature-local HTTP wrapper를 추가하지 않습니다.
- CSS는 component와 같은 위치에 두고 `src/shared/styles/tokens.css`의 variable을 사용합니다.
- `@/` alias를 사용해 `src`에서 import합니다.

## 구현 패턴

### Component와 Routing

- Component file과 exported component는 PascalCase로 이름을 정하고 hook은 `use`로 시작합니다.
- Page component는 route concern과 composition에 집중합니다. 재사용 가능한 workflow behavior는 feature에 둡니다.
- Route는 `src/app/router/router.tsx`에 중앙 등록하고 authenticated route는 `ProtectedRoute` 아래에 둡니다.
- 유효한 post-login redirect를 유지하고 external, protocol-relative 또는 recursive login redirect를 거부합니다.
- Semantic HTML과 native control을 우선합니다. Label, keyboard focus, loading status, 실행 가능한 error text를 제공합니다.

### State와 Data Flow

- Server-owned data에는 TanStack Query를 사용합니다. Feature별 stable query key를 정의하고 identity-changing mutation 뒤에는 invalidate하거나 clear합니다.
- Current authentication session처럼 실제로 application-wide인 client state에만 Context를 사용합니다.
- Transient form state는 React Hook Form에 두고 TypeScript type은 Zod schema에서 derive합니다.
- Effect가 unmount되거나 input이 변경되면 더 이상 필요하지 않은 request를 abort합니다.
- Browser code 전체에서 backend `long` identifier를 string으로 저장합니다.

### API와 Configuration

- Endpoint function은 `src/features/<feature>/api` 아래에 추가하고 `apiClient`를 호출합니다. Shared API infrastructure 밖에서 `fetch`를 직접 호출하지 않습니다.
- Backend `ApiResponse<T>` 및 `ErrorResponse` envelope를 `src/shared/api/apiTypes.ts`에서 일치시킵니다.
- 알고 있는 `ApiError` code는 field 또는 screen error로 변환하고 network와 unknown failure에는 안전한 fallback을 유지합니다.
- User input에서 파생된 path segment는 URL-encode합니다.
- `VITE_API_BASE_URL`은 `env.ts`가 사용하는 browser-visible runtime configuration입니다.
- `API_PROXY_TARGET`은 Vite development proxy만 설정합니다. `VITE_*` variable에 secret을 넣지 않습니다.

### Styling

- Literal color, spacing, radius 또는 stacking value를 추가하기 전에 design token을 재사용합니다.
- 함께 위치한 CSS에서 stable component prefix와 BEM-style element/modifier name을 사용합니다.
- Narrow screen, keyboard focus, error state, empty state, loading state, reduced motion을 확인합니다.
- 재사용 가능한 presentation에 inline style을 사용하거나 global status component를 중복 구현하지 않습니다.

## 테스트 전략

현재 frontend unit-test runner가 설정되어 있지 않습니다. 지원되는 test stack을 추가하고 문서화하지 않은 상태에서 unit-test coverage를 주장하거나 임시 test script를 추가하지 않습니다.

```powershell
cd frontend

# Locked dependency installation
npm ci

# Formatting and lint checks
npm run format:check
npm run lint

# TypeScript compilation and production bundle
npm run build

# Local behavior verification
npm run dev
```

- Logic change에는 관련된 success, validation failure, API error, network error, loading, cancellation path를 검증합니다.
- Route change에는 direct navigation, refresh, protected access, login redirect, not-found behavior를 검증합니다.
- UI change에는 keyboard operation, visible focus, accessible name, responsive layout, reduced motion을 수동 검증합니다.
- 완료 전에 root `verifyAll`을 실행합니다.

## 로컬 핵심 규칙

### 해야 할 일

- API type을 명시적으로 유지하고 신뢰할 수 없는 parsing boundary에서는 `unknown`을 사용합니다.
- Semantic이 맞는 경우 shared loading, empty, error state를 재사용합니다.
- Authentication identity가 변경되면 user-scoped cache data를 clear하거나 invalidate합니다.
- Schema, type, submission behavior, backend constraint를 일치시킵니다.
- Routing, navigation, access control을 함께 갱신합니다.

### 하지 말아야 할 일

- Browser code에서 PostgreSQL, backend 환경변수 또는 server-only secret에 접근하지 않습니다.
- Remote data를 Context나 local storage에 중복 저장하지 않습니다.
- Credential이나 민감한 API payload를 local storage에 보관하지 않습니다.
- 실제로 없을 수 있는 data에 non-null assertion을 사용하지 않습니다.
- Check를 통과시키기 위해 TypeScript, ESLint, React Hook 또는 accessibility failure를 suppress하지 않습니다.
- 현재 platform이나 설치된 stack이 이미 capability를 제공하면 새 dependency를 추가하지 않습니다.
