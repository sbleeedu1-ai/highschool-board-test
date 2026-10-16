# highschool_board_test

## 프로젝트

- 무엇: 고등학교 공지 게시판(방문자 열람, 관리자 1명이 등록·수정·삭제)
- 실행·테스트 명령: 티켓 01에서 채운다
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
