# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
1. 사용자 확인 후 05(`feat/05-notice-create`) PR을 만들고 병합한다.
2. 그다음 06(공지 수정)과 07(공지 삭제)을 병렬로 진행할 수 있다(둘 다 05에만 의존). 06은 `NoticeForm`·`NoticeInput`·`NoticeNotFoundException`을 그대로 쓴다.
3. 09(배포)는 사람이 할 일이 있는 티켓이다(`ready-for-human`, `/wizard` 추천). 10(운영 E2E)은 09 뒤.

## 현재 상태
- 티켓 01·02·03·04·08 병합 완료(PR #6-#10).
- 티켓 05 구현 완료(이 브랜치). Opus 단독.
  - 백엔드: `POST /api/notices`(관리자만, 201 + Location). 입력 검사는 `notice/NoticeInput.kt`(앞뒤 공백 제거 후 제목 1-100자·본문 1-5,000자, 틀리면 400 `INVALID_INPUT` + `fieldErrors`). 작성일시는 시계 값을 마이크로초까지 자른 값
  - 프론트: 목록의 [새 공지](로그인 시에만, 빈 목록에서도), `/admin/notices/new` 등록 화면, 공용 입력 칸 `components/NoticeForm.tsx`(글자 수, 칸별 안내, 서버 안내 표시), `src/lib/noticeInput.ts`
- 실행·테스트 명령, 로컬 관리자 계정 만드는 법은 `AGENTS.md`의 "프로젝트" 칸에 있다.

## 막힌 점 / 확인이 필요한 결정
- 없음.

## 모르면 실수할 것
- 글자 수는 화면·서버 모두 UTF-16 기준이라 이모지는 2자로 센다. 테스트 DB(H2)가 이 기준으로 길이를 재기 때문에 맞췄다. 운영 PostgreSQL은 더 너그러우므로 넘칠 일은 없다.
- 앞뒤 공백 기준은 화면(JavaScript `trim`)에 맞췄다. 서버는 `NoticeInput.isTrimmable`을 쓴다(Kotlin 기본 `trim`과 다르다).
- 테스트용 시계(`support/MutableClock.kt`)는 같은 설정의 테스트끼리 함께 쓴다. 시계를 돌리는 테스트는 `@BeforeEach`에서 `reset()`한다.
- 비밀 헤더 403은 코드 `PROXY_SECRET_INVALID`·문구 "허용되지 않은 접근입니다"로 CSRF 403(`CSRF_INVALID`·"다시 시도해 주세요")과 구분했다.
- 로그인 잠금 테스트는 실제 서버 + 바꿔 끼울 수 있는 시계(`support/MutableClock.kt`)로 돈다. 잠금 기록이 메모리에 남으므로 테스트마다 서버를 새로 띄운다(`@DirtiesContext`, 느린 편).
- 로그인 잠금은 IP 한 개 단위다. IPv6는 한 사람이 주소를 많이 가질 수 있어 묶어서 세면 더 단단하지만 명세 밖이라 하지 않았다. Cloudflare가 `CF-Connecting-IP`를 안 붙이는 경우 모든 방문자가 프록시 주소 하나로 묶인다(보통은 붙는다).
- **티켓 09에서 볼 것**: XSRF-TOKEN 쿠키의 Secure는 요청이 HTTPS인지(`request.isSecure()`)를 따른다. Railway 프록시 뒤에서는 `server.forward-headers-strategy` 설정이 필요할 수 있다. Pages 환경변수는 `BACKEND_URL`·`PROXY_SECRET`, Railway는 `PROXY_SECRET`·`ADMIN_USERNAME`·`ADMIN_PASSWORD_HASH`. Functions는 방문자의 `X-Forwarded-*`를 지우고 보낸다.
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
- 티켓 05: 백엔드 44개, 프론트 Vitest 43개·타입 검사·빌드 통과. 경계값(100/101자)을 일부러 101로 바꾸면 테스트가 실패하는 것 확인
- 티켓 05 브라우저(메모리 DB): 로그인 시 [새 공지], 빈 칸 등록 시 두 칸 안내, 글자 수 `19/100`(앞뒤 공백 제외), 등록 후 새 상세로 이동, 작성 중 로그아웃 → 만료 안내가 있는 로그인 화면, CSRF 쿠키를 지우고 등록 → "다시 시도해 주세요"
- 03+08 합친 상태: 백엔드 35개, 프론트 Vitest 38개·타입 검사(Functions 파일 포함)·빌드 통과. lint 경고 3개(오류 없음). 잠금 테스트는 두 번 연달아 돌려 흔들리지 않음을 확인
- 티켓 03: 백엔드 23개, 프론트 Vitest 32개·타입 검사·빌드 통과. 브라우저로 줄바꿈·링크 2개(`noopener`)·`<script>`는 글자로만·한국 시간 날짜(`작성 2026-10-16 14:30 · 수정 2026-10-17 09:00`)·없는 공지 화면·390px 폭 확인
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
