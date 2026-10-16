# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
1. 티켓 02(이 브랜치 `feat/02-notice-list`)와 04(`feat/04-admin-login`, 02 위에 쌓음)를 사용자 확인 후 PR로 만들고, 02 → 04 순서로 병합한다.
2. 그다음 03(공지 상세)과 08(비밀 헤더·로그인 잠금)을 병렬로 진행할 수 있다.

## 현재 상태
- 티켓 01 병합 완료(PR #6).
- 티켓 02 구현 완료(이 브랜치). Sonnet이 작성하고 Opus가 검토했다.
  - 백엔드: 공지 표(`V1__create_notice.sql`), `GET /api/notices?page=N`(응답 `items`·`page`·`totalPages`·`totalElements`)
  - 프론트: `react-router` v7, 공통 틀 `Layout`, 공지 목록 화면, `src/lib/page.ts`·`date.ts`
  - CI: `postgres` 작업(실제 PostgreSQL 17로 마이그레이션·서버 기동 확인). GitHub에서는 아직 안 돌았다
- 티켓 04는 다른 폴더(`highschool_board_test`, 브랜치 `feat/04-admin-login`)에서 진행 중이다.
- 실행·테스트 명령은 `AGENTS.md`의 "프로젝트" 칸에 있다.

## 막힌 점 / 확인이 필요한 결정
- 없음.

## 모르면 실수할 것
- **Java 21(JDK)이 필요하다.** 강의실 PC에는 2026-10-16에 `winget install EclipseAdoptium.Temurin.21.JDK`로 설치했다. 집 PC에도 같은 명령으로 설치한다. 설치 직후 이미 열려 있던 터미널은 PATH를 모르므로 새로 연다.
- **prod 프로필이 조용히 H2로 뜰 수 있다**(티켓 09에서 막을 것): H2가 런타임 의존성이라 `application-prod.yml`에 DB 설정이 없으면 메모리 H2가 붙는다. 09에서 PostgreSQL 설정을 채울 때 H2로 뜨지 않는지 확인한다.
- 시계를 테스트에서 고정할 때는 `@TestBean`(또는 `@MockitoBean`)으로 `Clock` 빈을 바꿔 끼운다. 같은 이름의 빈을 새로 정의해 덮어쓰는 방식은 Spring Boot가 기본으로 막는다. 처음 쓰는 곳은 티켓 05.
- 오류 처리기는 `ResponseEntityExceptionHandler`를 상속한다. Spring이 아는 오류(404·405 등)는 상태 코드를 지키고 형식만 바꾼다. 새 상황별 오류(400·401·429 등)는 이 클래스에 덧붙인다.
- `tsconfig`에 `baseUrl`을 넣지 않는다. TypeScript 6에서 사용 중단 오류가 난다(`paths`만으로 `@/*` 별칭이 동작한다).
- GitHub `main`은 브랜치 보호가 켜져 있어 직접 push가 거절된다. 항상 브랜치 → PR.
- **집 PC는 아직 옛 설정이다.** claude·codex를 켜기 전에 `new-setup` 저장소를 받아 `install.cmd`(mac이면 `./install.sh`)를 실행한다. 절차는 그 저장소의 README.
- Matt Pocock 스킬 업데이트는 `npx skills update`가 아니라 `new-setup`의 `install.cmd`로 한다.

## 마지막 검증
- 티켓 02: 백엔드 테스트 11개 통과(목록 7개 추가), 프론트 Vitest 10개·lint·타입 검사·빌드 통과
- 티켓 02 화면: 가짜 공지 25건을 메모리 DB에 넣고 브라우저로 확인. 표준 폭에서 10건·페이지 번호 1·2·3, 390px 폭에서 제목 아래 날짜, 가로 스크롤 없음
- 티켓 01: 백엔드 `./gradlew clean test`: 테스트 4개 통과(버전 local, 버전 커밋 번호, 404 형식, 405 형식)
- 프론트: `npm run lint`(shadcn이 만든 `button.tsx` 경고 1개, 오류 없음), `npm run typecheck`, `npm test`(3개 통과), `npm run build` 모두 통과
- 직접 실행: `bootRun` 후 `curl localhost:8080/api/version` → `{"version":"local"}`, 없는 주소 → 404 공통 형식, `/actuator/health` → UP. Vite 개발 서버(5173)의 `/api/version`도 같은 응답(프록시 확인). 브라우저에서 화면 맨 아래에 "버전 local" 표시 확인
- 티켓 01: PR #6에서 GitHub CI 첫 실행 통과(backend 1분 11초, frontend 21초)

## 추천 스킬
- `/tdd`: 티켓마다 HTTP API 접점에서 실패하는 테스트부터 쓴다.
- `/code-review`: PR 전에 명세·티켓 기준으로 검토한다.

## 참고 문서
- 명세: `.scratch/notice-board/spec.md`, 티켓: `.scratch/notice-board/issues/`
- 용어집: `CONTEXT.md`
- 결정: `docs/adr/0001-0003`
- 인계 규칙: `docs/agents/handoff.md`
