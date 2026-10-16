# 명세: 고등학교 공지 게시판

Status: needs-triage

용어는 `CONTEXT.md`, 되돌리기 어려운 결정의 이유는 `docs/adr/0001-0003`을 따른다. 이 명세는 2026-10-16 grill-with-docs 세션(질문 Q1-Q29)에서 합의한 내용이다.

## 목표와 범위

- 방문자: 공지 목록과 공지 상세를 본다(로그인 없음).
- 관리자(1명): 공지를 등록·수정·삭제한다.
- **범위 제외:** 회원가입, 댓글, 첨부파일, 검색, 초안, 상단 고정, 조회수, 작성자 표시, 휴지통·복구, PR 미리보기 배포.

## 저장소 구조와 실행·배포 경로

| 위치 | 역할 | 로컬 실행 | 배포 |
|---|---|---|---|
| `backend/` | JSON API만 제공 | `./gradlew bootRun` (8080) | Railway (Root Directory `/backend`) |
| `frontend/` | 방문자·관리자 화면 전체 | `npm run dev` (5173) | Cloudflare Pages (`frontend/dist`) |
| `frontend/functions/` | Pages Functions: `/api/*`를 Railway로 전달 | Vite 프록시가 대신함 | Pages와 함께 |
| 루트 | 문서, `.github/workflows/` | - | - |

- 루트에 두 폴더를 묶는 빌드 도구는 두지 않는다.
- 로컬은 Vite 개발 서버가 `/api/*`를 `localhost:8080`으로 프록시해 운영과 같은 "주소 하나" 구조를 흉내 낸다.
- 로컬 관리자 계정은 `backend/.env.local`(git 제외)에 둔다.

## 기술 스택과 버전

- 백엔드: Spring Boot **4.1.1**, Kotlin(Spring Boot가 관리하는 버전, 2.2 이상), Java **21**, Gradle Kotlin DSL, Spring Data JPA, Spring Security, Flyway, Actuator.
- 프론트: Vite + React + TypeScript(`react-ts` 템플릿), shadcn/ui(Tailwind v4), Node **24 LTS**, npm, Vitest, Playwright(E2E).
- DB: 로컬 H2 파일, 테스트 H2 메모리, 운영 PostgreSQL(Railway).

## 공지

- 필드: 제목, 본문, 작성일시, 수정일시.
- 본문은 일반 텍스트. 줄바꿈만 보존하고, `http://`·`https://`로 시작하는 주소만 새 탭에서 열리는 링크로 바꾼다. HTML은 실행되지 않아야 한다.
- 입력 제한(백엔드가 최종 판정, 프론트는 입력 중 안내와 글자 수 표시):
  - 제목: 앞뒤 공백 제거 후 1-100자
  - 본문: 앞뒤 공백 제거 후 1-5,000자
  - 공백만 있으면 거부
- 삭제: 완전 삭제. 삭제 전 확인 창.
- 동시 수정: 나중에 저장한 쪽이 이긴다(별도 충돌 검사 없음).

## 공지 목록과 페이지 이동

- 페이지 번호 방식. 주소 `?page=N`(1부터), 한 페이지 10건, 작성일시 최신순(수정해도 순서 불변).
- `?page=`가 숫자가 아니거나 1보다 작으면 1페이지로 보여 준다.
- 마지막 페이지를 넘으면 "공지가 없습니다"와 [첫 페이지로] 버튼.

## 날짜와 언어

- 화면은 한국어만. 날짜는 한국 시간으로 표시하고 DB에는 UTC로 저장한다.
- 목록: `2026-10-16`. 상세: `작성 2026-10-16 14:30`, 수정된 공지만 `· 수정 2026-10-17 09:00` 추가.

## 권한과 인증

- 관리자 아이디·비밀번호 해시는 Railway 환경변수(ADR-0002). 해시는 로컬 `./gradlew hashPassword`(비밀번호를 입력받아 해시만 출력, 원문 저장 안 함).
- 로그인 유지: 서버 메모리 세션. 쿠키는 HttpOnly + Secure, 30분 무활동 시 만료, "로그인 유지" 없음. 재배포하면 로그아웃된다(감수). Railway 인스턴스는 1대로 고정.
- CSRF: Spring Security `csrf.spa()`.
- 로그인 실패: "아이디 또는 비밀번호가 올바르지 않습니다"만 표시. **IP별 5회 연속 실패 시 10분 로그인 잠금**(메모리).
- 프록시 비밀 헤더(ADR-0003): Functions가 `X-Client-IP`, `X-Proxy-Secret`을 붙인다. 비밀값이 없거나 틀린 요청은 거절(`/actuator/health` 예외).
- `local` 프로필에서는 Secure 쿠키와 비밀 헤더 검사를 끈다.

## 화면

| 경로 | 누가 | 내용 |
|---|---|---|
| `/` (`?page=N`) | 모두 | 공지 목록. 관리자 로그인 시 [새 공지] 버튼 |
| `/notices/:id` | 모두 | 공지 상세. 관리자 로그인 시 [수정][삭제] 버튼 |
| `/admin/login` | 관리자 | 로그인. 방문자 화면에 링크를 두지 않는다 |
| `/admin/notices/new` | 관리자 | 공지 등록 |
| `/admin/notices/:id/edit` | 관리자 | 공지 수정 |

- 로그인하지 않고 `/admin/notices/*`에 들어오면 로그인 화면으로 보낸다.
- 로그아웃 버튼은 로그인 상태에서만 보인다.

## 오류 처리

API 오류 응답 형식: `{ "code": "NOTICE_NOT_FOUND", "message": "공지를 찾을 수 없습니다", "fieldErrors": [] }`

| 상황 | 상태 코드 | 화면 처리 |
|---|---|---|
| 입력 오류 | 400 | 해당 입력칸 아래 빨간 안내 |
| 로그인 안 됨 / 세션 만료 | 401 | 로그인 화면으로 이동, 작성 중 내용 유실 가능 안내 |
| CSRF 토큰 오류 | 403 | "다시 시도해 주세요" |
| 없는 공지(삭제된 공지 조작 포함) | 404 | "공지를 찾을 수 없습니다" + [목록으로] |
| 로그인 잠금 | 429 | "잠시 후 다시 시도해 주세요" |
| 서버·네트워크 오류 | 500 등 | "잠시 후 다시 시도해 주세요" + [다시 시도] |

## DB 전환

| 프로필 | DB |
|---|---|
| `local` | H2 파일 `backend/data/` (git 제외) |
| `test` | H2 메모리 |
| `prod` | PostgreSQL. Railway Postgres 서비스의 `PGHOST`·`PGPORT`·`PGUSER`·`PGPASSWORD`·`PGDATABASE`를 `${{Postgres.…}}`로 참조 |

- 표 구조는 Flyway 마이그레이션으로만 바꾼다. JPA는 `ddl-auto=validate`.
- H2는 PostgreSQL 호환 모드로 띄운다.
- H2와 PostgreSQL 차이로 인한 운영 실패를 막기 위해 CI에서 실제 PostgreSQL 컨테이너로 Flyway 실행과 서버 기동을 확인한다.

## CI/CD

- **CI** (PR과 `main` push): 백엔드 테스트, PostgreSQL 컨테이너 확인, 프론트 lint·타입 검사·Vitest·빌드.
- **백엔드 배포**: Railway GitHub 자동배포. Root Directory `/backend`, Watch Paths `/backend/**`, "Wait for CI" 켬. 설정 파일은 `/backend/railway.toml`(절대경로). 헬스체크 경로 `/actuator/health`.
- **프론트 배포**: `main`에서 CI 통과 후 GitHub Actions가 `frontend/`를 빌드해 `cloudflare/wrangler-action@v4`로 `pages deploy`. GitHub 시크릿 `CLOUDFLARE_API_TOKEN`(Pages Edit 권한), `CLOUDFLARE_ACCOUNT_ID`.
- **필요한 비밀값(값은 문서에 적지 않는다)**: Railway — 관리자 아이디, 관리자 비밀번호 해시, 프록시 비밀값, `SPRING_PROFILES_ACTIVE=prod`, Postgres 참조. Pages — 백엔드 주소, 프록시 비밀값.
- **배포 완료 기준** (모두 만족):
  - (a) Railway `/actuator/health`가 200이고 새 배포가 활성화됨
  - (b) Pages 배포 단계 성공
  - (c) Actions가 `https://<pages주소>/api/version`의 커밋 번호(`RAILWAY_GIT_COMMIT_SHA`)가 이번 push 커밋과 같아질 때까지 최대 10분 확인한 뒤 `/api/notices`가 200

## 테스트와 구현 완료 기준

- **백엔드 통합 테스트**: 목록·상세·등록·수정·삭제, 미로그인 401, CSRF 없음 403, 경계값(제목 100/101자, 본문 5,000/5,001자), 페이지 규칙, 로그인 잠금, 비밀 헤더 검사.
- **프론트 Vitest**: 페이지 번호 해석, 링크 자동 변환(`<script>` 무력화 포함), 날짜 형식.
- **E2E**: Playwright 테스트를 `frontend/e2e/`에 두고 첫 배포 직후 운영 주소로 1회 실행(CI 상시 실행 아님). 흐름: 목록·상세 → 로그인 → 등록·수정·삭제 → 로그아웃. 테스트 공지 제목은 `[E2E 테스트]`로 시작하고 끝에 반드시 삭제.
- **구현 완료** = CI 통과 + 배포 완료 기준 (a)(b)(c) + E2E 1회 통과 + 사람 점검표 확인.

## 작업 방식

- 기능 브랜치 → PR → 사용자 확인 후 `main` 병합. `main` 직접 push 금지.

## 부록: 공식 문서로 확인한 사실 (2026-10-16)

- Spring Boot 최신 안정 4.1.1(2026-08-20), Java 17-26 지원, Gradle 8.14+/9.x. Kotlin 2.2 이상, 버전은 Boot가 관리. kotlin-spring 플러그인과 `-Xannotation-default-target=param-property` 권장.
- Railpack: 루트 디렉터리에 `gradlew`가 있으면 Java로 인식, JDK 기본 21(`RAILPACK_JDK_VERSION`으로 변경).
- Railway: 헬스체크는 2xx 응답 시 새 배포 활성화, 기본 300초. `PORT` 환경변수로 listen. "Wait for CI"는 push 트리거 워크플로가 있어야 켜지며 실패 시 배포 SKIPPED(커뮤니티: Redeploy 시 설정이 꺼진다는 보고 있음).
- Cloudflare: `pages-action`은 보관(archived) → `wrangler-action@v4`. Functions 폴더는 Pages 프로젝트 루트(`dist` 밖). 최상위 `404.html`이 없으면 SPA로 처리. Worker가 외부로 보내는 요청에 방문자 IP가 자동으로 전달되지 않음.
- Vite 8.3.x, Node 20.19+/22.12+ 필요. shadcn/ui는 Tailwind v4와 `@/*` 경로 별칭 사용.
- Spring Security 7.1: SPA용 `csrf.spa()` 공식 권장.

출처:
- https://docs.spring.io/spring-boot/system-requirements.html
- https://docs.spring.io/spring-boot/reference/features/kotlin.html
- https://docs.spring.io/spring-security/reference/servlet/exploits/csrf.html
- https://railpack.com/languages/java
- https://docs.railway.com/guides/monorepo
- https://docs.railway.com/reference/healthchecks
- https://docs.railway.com/guides/github-autodeploys
- https://docs.railway.com/reference/variables
- https://docs.railway.com/databases/postgresql
- https://developers.cloudflare.com/pages/how-to/use-direct-upload-with-continuous-integration/
- https://developers.cloudflare.com/pages/functions/get-started/
- https://developers.cloudflare.com/pages/functions/pricing/
- https://developers.cloudflare.com/pages/configuration/serving-pages/
- https://developers.cloudflare.com/fundamentals/reference/http-headers/
- https://vite.dev/guide/
- https://ui.shadcn.com/docs/installation/vite
