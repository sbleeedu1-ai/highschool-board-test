# highschool_board_test

## 작업 인계 (HANDOFF)

- 시작: `git fetch` 후 `gh pr list`로 최신 작업 브랜치를 찾고, 그 브랜치의 `HANDOFF.md`를 먼저 읽는다.
- 커밋할 때마다 `HANDOFF.md`를 최신 상태로 다시 써서 함께 커밋하고, 작업 브랜치에 push한다.
- 비밀값은 적지 않고, 다른 문서에 있는 내용은 링크만 남긴다.
- 세부 규칙: `docs/agents/handoff.md` (handoff 스킬의 OS 임시 폴더 대신 이 파일을 쓴다).

## Agent skills

### Issue tracker

이슈와 명세는 `.scratch/` 아래 로컬 마크다운 파일로 관리한다(GitHub Issues는 쓰지 않는다). See `docs/agents/issue-tracker.md`.

### Triage labels

기본 5개 라벨을 그대로 쓰고, 이슈 파일의 `Status:` 줄에 적는다. See `docs/agents/triage-labels.md`.

### Domain docs

single-context: 루트의 `CONTEXT.md`(용어집) 하나와 `docs/adr/`. See `docs/agents/domain.md`.
