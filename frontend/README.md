# Auto Retro Frontend

Auto Retro의 프론트엔드 애플리케이션입니다. 개발 기록, AI 요약 및 태그, 주간 리포트와 발표 초안을 웹 화면에서 관리하는 것을 목표로 합니다.

현재 React, TypeScript, Vite를 기반으로 초기 환경을 구성하고 있습니다.

## 기술 스택

- React 19
- TypeScript
- Vite
- ESLint
- Prettier
- npm

## 실행 환경

- Node.js 22 LTS 권장
- npm 11 이상 권장

설치된 버전은 다음 명령으로 확인할 수 있습니다.

```bash
node --version
npm --version
```

## 시작하기

프론트엔드 디렉터리로 이동합니다.

```bash
cd frontend
```

의존성을 설치합니다.

```bash
npm install
```

개발 서버를 실행합니다.

```bash
npm run dev
```

기본 개발 서버 주소는 `http://localhost:5173`입니다.

Windows PowerShell의 실행 정책 때문에 `npm.ps1` 실행 오류가 발생하면 다음처럼 `npm.cmd`를 사용합니다.

```powershell
npm.cmd run dev
```

## 주요 명령어

```bash
# 개발 서버 실행
npm run dev

# 코드 자동 정렬
npm run format

# 코드 정렬 상태 확인
npm run format:check

# ESLint 검사
npm run lint

# TypeScript 검사 및 배포 파일 생성
npm run build

# 빌드 결과 미리보기
npm run preview
```

코드를 변경한 뒤에는 다음 검사를 순서대로 실행합니다.

```bash
npm run format:check
npm run lint
npm run build
```

## 현재 디렉터리 구조

```text
frontend/
├─ public/             # 빌드 과정 없이 그대로 제공되는 정적 파일
├─ src/
│  ├─ App.tsx          # 현재 최상위 React 컴포넌트
│  ├─ index.css        # 전역 스타일
│  └─ main.tsx         # React 애플리케이션 진입점
├─ index.html          # 브라우저가 처음 읽는 HTML
├─ package.json        # 의존성과 npm 명령 정의
├─ tsconfig.app.json   # 프론트 TypeScript 설정
└─ vite.config.ts      # Vite 개발 및 빌드 설정
```

프로젝트가 확장되면 `src`를 다음 역할로 분리할 예정입니다.

- `app`: 라우터와 Provider 등 애플리케이션 전체 설정
- `pages`: URL에 대응하는 페이지
- `features`: 개발 기록, 리포트 등 업무 기능
- `shared`: 공통 컴포넌트, API 코드, 스타일과 타입

## 경로 별칭

`@`는 `src` 디렉터리를 가리킵니다.

```tsx
import App from '@/App'
```

이는 다음 상대 경로 import와 같은 의미입니다.

```tsx
import App from './App'
```

## 생성 파일

다음 디렉터리는 명령 실행으로 다시 생성할 수 있으므로 Git에 포함하지 않습니다.

- `node_modules`: `npm install`로 설치되는 의존성
- `dist`: `npm run build`로 생성되는 배포 파일

## 백엔드 연결

프론트엔드는 PostgreSQL에 직접 접근하지 않습니다. 브라우저에서 Spring Boot API로 요청하고, 백엔드가 데이터베이스를 처리합니다.

```text
React 프론트엔드 → Spring Boot API → PostgreSQL
```

개발 중 프론트엔드는 기본적으로 `5173` 포트, Spring Boot는 `8080` 포트를 사용합니다. API 프록시와 환경변수는 이후 초기 설정 단계에서 추가합니다.
