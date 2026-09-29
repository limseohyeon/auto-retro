# Frontend Module Rules

## Module Context

`frontend` is an independent npm package that renders the Auto Retro browser client. It communicates only with the Spring Boot `/api` surface.

- `src/app` owns application composition, providers, layout, and routing.
- `src/pages` owns route-level composition and should stay thin.
- `src/features` owns user-facing workflows, feature API functions, state, and UI.
- `src/shared` owns reusable API infrastructure, configuration, components, styles, and types without business ownership.

Dependencies flow from `app` and `pages` toward `features` and `shared`. `shared` must not import from `features`, `pages`, or `app`.

## Tech Stack & Constraints

- React 19.2, TypeScript 6 with strict mode, Vite 8, and npm lockfile resolution.
- React Router 8 owns navigation and route protection.
- TanStack Query 5 owns remote server state and cache lifecycle.
- React Hook Form plus Zod owns non-trivial form state and validation.
- Native `fetch` is wrapped by `src/shared/api/apiClient.ts`; do not add Axios or feature-local HTTP wrappers.
- CSS is colocated with components and consumes variables from `src/shared/styles/tokens.css`.
- Use the `@/` alias for imports from `src`.

## Implementation Patterns

### Components and Routing

- Name component files and exported components in PascalCase; name hooks with `use`.
- Keep page components focused on route concerns and composition. Put reusable workflow behavior in a feature.
- Register routes centrally in `src/app/router/router.tsx` and keep authenticated routes under `ProtectedRoute`.
- Preserve a valid post-login redirect and reject external, protocol-relative, or recursive login redirects.
- Prefer semantic HTML and native controls. Provide labels, keyboard focus, loading status, and actionable error text.

### State and Data Flow

- Use TanStack Query for server-owned data; define stable query keys per feature and invalidate or clear them after identity-changing mutations.
- Use Context only for truly application-wide client state such as the current authentication session.
- Keep transient form state in React Hook Form and derive its TypeScript type from the Zod schema.
- Abort obsolete requests when effects unmount or input changes.
- Store backend `long` identifiers as strings throughout browser code.

### API and Configuration

- Add endpoint functions under `src/features/<feature>/api` and call `apiClient`; do not call `fetch` directly outside shared API infrastructure.
- Match the backend `ApiResponse<T>` and `ErrorResponse` envelopes in `src/shared/api/apiTypes.ts`.
- Convert known `ApiError` codes into field or screen errors; preserve a safe fallback for network and unknown failures.
- URL-encode path segments derived from user input.
- `VITE_API_BASE_URL` is browser-visible runtime configuration consumed by `env.ts`.
- `API_PROXY_TARGET` configures only the Vite development proxy. Never place secrets in `VITE_*` variables.

### Styling

- Reuse design tokens before adding literal colors, spacing, radii, or stacking values.
- Use a stable component prefix and BEM-style element/modifier names in colocated CSS.
- Check narrow screens, keyboard focus, error states, empty states, loading states, and reduced motion.
- Do not use inline styles for reusable presentation or duplicate global status components.

## Testing Strategy

No frontend unit-test runner is currently configured. Do not claim unit-test coverage or add ad hoc test scripts without adding and documenting a supported test stack.

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

- For logic changes, verify success, validation failure, API error, network error, loading, and cancellation paths as applicable.
- For route changes, verify direct navigation, refresh, protected access, login redirect, and not-found behavior.
- For UI changes, manually verify keyboard operation, visible focus, accessible names, responsive layout, and reduced motion.
- Run root `verifyAll` before completion.

## Local Golden Rules

### Do's

- Keep API types explicit and use `unknown` at untrusted parsing boundaries.
- Reuse shared loading, empty, and error states where their semantics fit.
- Clear or invalidate user-scoped cache data when authentication identity changes.
- Keep schemas, types, submission behavior, and backend constraints aligned.
- Update routing, navigation, and access control together.

### Don'ts

- Do not access PostgreSQL, backend environment variables, or server-only secrets from browser code.
- Do not duplicate remote data into Context or local storage.
- Do not persist credentials or sensitive API payloads in local storage.
- Do not use non-null assertions for data that can legitimately be absent.
- Do not suppress TypeScript, ESLint, React Hook, or accessibility failures to pass checks.
- Do not add a new dependency when the current platform or installed stack already provides the capability.
