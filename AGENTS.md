# highschool_board_test

## 프로젝트

- 무엇: 고등학교 공지 게시판(방문자 열람, 관리자 1명이 등록·수정·삭제)
- 구조: `backend/`(Spring Boot API), `frontend/`(Vite + React 화면). 루트 빌드 도구는 없다.
- 준비물: Java 21(JDK), Node 24
- 백엔드(`backend/`에서): 실행 `./gradlew bootRun`(8080, `local` 프로필), 테스트 `./gradlew test`
- 프론트(`frontend/`에서): 처음 한 번 `npm install`, 실행 `npm run dev`(5173, `/api`는 8080으로 프록시), 테스트 `npm test`, 검사 `npm run lint`·`npm run typecheck`, 빌드 `npm run build`
- CI: `.github/workflows/ci.yml`(PR과 `main` push)
- 로컬 관리자 계정: `backend/.env.example`을 `backend/.env.local`로 복사하고, `backend/`에서 `./gradlew hashPassword -q --console=plain`으로 만든 해시를 넣는다(`.env.local`은 git 제외). 로그인 화면은 `/admin/login`
- 테스트는 외부 계정·비밀값 없이 돌게 만들고 늘 통과 상태로 둔다
- 공개 저장소다. 비밀값, 실제 학생·교직원 개인정보, 남의 저작물(학교 로고 등)은 커밋하지 않는다. 예시·테스트용 공지는 가짜 내용으로 쓴다.

## 작업 인계 (HANDOFF)

- 시작: `git fetch` 후 `gh pr list`로 최신 작업 브랜치를 찾고, 그 브랜치의 `HANDOFF.md`를 먼저 읽는다.
- 커밋할 때마다 `HANDOFF.md`를 최신 상태로 다시 써서 함께 커밋하고(결정·변경이 있었으면 `HISTORY.md`에도 덧붙인다), 작업 브랜치에 push한다.
- 비밀값은 적지 않고, 다른 문서에 있는 내용은 링크만 남긴다.
- 절차는 `handoff` 스킬을 따르고, 이 저장소만의 규칙은 `docs/agents/handoff.md`에 있다(스킬보다 우선).

## Agent skills

### Issue tracker

이슈와 명세는 `.scratch/` 아래 로컬 마크다운 파일로 관리한다(GitHub Issues는 쓰지 않는다). See `docs/agents/issue-tracker.md`.

### Triage labels

기본 5개 라벨을 그대로 쓰고, 이슈 파일의 `Status:` 줄에 적는다. See `docs/agents/triage-labels.md`.

### Domain docs

single-context: 루트의 `CONTEXT.md`(용어집) 하나와 `docs/adr/`. See `docs/agents/domain.md`.
