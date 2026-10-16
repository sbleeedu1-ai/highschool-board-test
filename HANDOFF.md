# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
1. 사용자 확인 후 PR 두 개를 만들고 02 → 04 순서로 병합한다. 04 브랜치(`feat/04-admin-login`)는 02 브랜치(`feat/02-notice-list`) 위에 쌓여 있어, 02를 먼저 병합하면 04 PR에는 04 커밋만 남는다.
2. 병합 뒤 03(공지 상세)과 08(비밀 헤더·로그인 잠금)을 병렬로 진행할 수 있다(사용자는 사용량이 비슷하면 병렬로 시간을 아끼는 쪽을 택함).

## 현재 상태
- 티켓 01 병합 완료(PR #6).
- 티켓 02 구현 완료(`feat/02-notice-list`). Sonnet이 작성, Opus가 검토·화면 확인.
  - 백엔드: 공지 표(`V1__create_notice.sql`), `GET /api/notices?page=N`(응답 `items`·`page`·`totalPages`·`totalElements`)
  - 프론트: `react-router` v7, 공통 틀 `Layout`, 공지 목록 화면
  - CI: `postgres` 작업(실제 PostgreSQL 17로 마이그레이션·서버 기동 확인). GitHub에서는 아직 안 돌았다
- 티켓 04 구현 완료(`feat/04-admin-login`). 체크박스 하나(401 → 로그인 화면 안내)만 05·06에서 브라우저 확인이 남았다.
  - 백엔드: Spring Security. `POST /api/auth/login`·`logout`, `GET /api/auth/me`, `csrf.spa()`, 메모리 세션(30분), 관리자 계정은 환경변수 `ADMIN_USERNAME`·`ADMIN_PASSWORD_HASH`
  - 공개 읽기 주소는 허용 목록(`/api/notices`, `/api/notices/*`, `/api/version`, `/api/auth/me`). **새 공개 GET API를 만들면 `SecurityConfig`에 추가해야 한다**
  - 프론트: `/admin/login`, 관리자 화면 문지기(`RequireAdmin`, `/admin/notices/*`), 로그아웃 버튼, `src/lib/api.ts`의 `sendJson`(CSRF 헤더·401 처리)
- 실행·테스트 명령, 로컬 관리자 계정 만드는 법은 `AGENTS.md`의 "프로젝트" 칸에 있다.

## 막힌 점 / 확인이 필요한 결정
- 없음.

## 모르면 실수할 것
- **티켓 08에서 볼 것**: 서버는 CSRF가 아닌 403에도 "다시 시도해 주세요"를 준다. 비밀 헤더 403과 문구·코드가 겹치지 않게 정한다. 로그인 잠금이 생기기 전까지는 로그인 시도 횟수 제한이 없다.
- **티켓 09에서 볼 것**: XSRF-TOKEN 쿠키의 Secure는 요청이 HTTPS인지(`request.isSecure()`)를 따른다. Railway 프록시 뒤에서는 `server.forward-headers-strategy` 설정이 필요할 수 있다.
- 로그아웃 API는 로그인하지 않아도 204를 준다(Spring의 로그아웃 필터가 권한 검사보다 먼저 돈다). 명세 표에는 "관리자"로 적혀 있지만 해가 없어 그대로 두었다.
- 로그인 뒤 CSRF 토큰을 바꾸지 않는다. 이 앱은 토큰을 쿠키와 헤더로 맞춰 보는 방식이라, 쿠키를 심을 수 있는 공격자는 로그인 뒤에도 다시 심을 수 있어 바꿔도 이득이 거의 없다고 판단했다.
- `./gradlew hashPassword`는 Gradle 안에서 돌아 입력한 비밀번호가 화면에 보인다(원문을 저장·출력하지는 않는다). 해시를 복사한 뒤 화면을 지우라는 안내를 출력한다.
- 백엔드 실행 진입점이 둘(서버, hashPassword)이라 `build.gradle.kts`에 `springBoot { mainClass }`를 적어 두었다. 지우면 `bootJar`·`bootRun`이 실패한다.
- **Java 21(JDK)이 필요하다.** 강의실 PC에는 2026-10-16에 `winget install EclipseAdoptium.Temurin.21.JDK`로 설치했다. 집 PC에도 같은 명령으로 설치한다. 설치 직후 이미 열려 있던 터미널은 PATH를 모르므로 새로 연다.
- **prod 프로필이 조용히 H2로 뜰 수 있다**(티켓 09에서 막을 것): H2가 런타임 의존성이라 `application-prod.yml`에 DB 설정이 없으면 메모리 H2가 붙는다. 09에서 PostgreSQL 설정을 채울 때 H2로 뜨지 않는지 확인한다.
- 시계를 테스트에서 고정할 때는 `@TestBean`(또는 `@MockitoBean`)으로 `Clock` 빈을 바꿔 끼운다. 같은 이름의 빈을 새로 정의해 덮어쓰는 방식은 Spring Boot가 기본으로 막는다. 처음 쓰는 곳은 티켓 05.
- 오류 처리기는 `ResponseEntityExceptionHandler`를 상속한다. Spring이 아는 오류(404·405 등)는 상태 코드를 지키고 형식만 바꾼다. 새 상황별 오류(400·401·429 등)는 이 클래스에 덧붙인다.
- `tsconfig`에 `baseUrl`을 넣지 않는다. TypeScript 6에서 사용 중단 오류가 난다(`paths`만으로 `@/*` 별칭이 동작한다).
- GitHub `main`은 브랜치 보호가 켜져 있어 직접 push가 거절된다. 항상 브랜치 → PR.
- **집 PC는 아직 옛 설정이다.** claude·codex를 켜기 전에 `new-setup` 저장소를 받아 `install.cmd`(mac이면 `./install.sh`)를 실행한다. 절차는 그 저장소의 README.
- Matt Pocock 스킬 업데이트는 `npx skills update`가 아니라 `new-setup`의 `install.cmd`로 한다.

## 마지막 검증
- 04 브랜치(02 포함) 백엔드 `./gradlew clean test`: 19개 통과. 인증 테스트는 실제 서버를 띄워 쿠키 속성(HttpOnly·Secure·SameSite=Lax)까지 본다. `HttpOnly`를 끄면 테스트가 실패하는 것도 확인했다
- 프론트: Vitest 19개(쿠키 읽기, 돌아갈 주소 검사 포함), 타입 검사, 빌드 통과. lint는 경고 3개(오류 없음)
- 브라우저(로컬): 관리자 화면 → 로그인 화면 이동, 틀린 비밀번호 문구, 로그인 후 원래 화면 복귀, 로그인 상태에서만 로그아웃 버튼, 로그아웃 후 첫 화면
- 로컬 `bootRun`이 `backend/.env.local`의 관리자 계정을 읽고, 로컬 세션 쿠키에는 Secure가 빠지는 것 확인
- 02 화면: 가짜 공지 25건으로 표준 폭·390px 폭 확인(가로 스크롤 없음)
- 티켓 01: PR #6에서 GitHub CI 통과. 02·04의 CI는 아직 안 돌았다

## 추천 스킬
- `/tdd`: 티켓마다 HTTP API 접점에서 실패하는 테스트부터 쓴다.
- `/code-review`: PR 전에 명세·티켓 기준으로 검토한다.

## 참고 문서
- 명세: `.scratch/notice-board/spec.md`, 티켓: `.scratch/notice-board/issues/`
- 용어집: `CONTEXT.md`
- 결정: `docs/adr/0001-0003`
- 인계 규칙: `docs/agents/handoff.md`
