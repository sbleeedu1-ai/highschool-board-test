# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
1. 티켓 01(걷는 뼈대)부터 구현한다: `.scratch/notice-board/issues/01-walking-skeleton.md`. 01이 끝나면 02와 04는 동시에 진행할 수 있다.

## 현재 상태
- 명세·티켓 10개는 `main`에 있다. 코드는 아직 없다.
- 작업 브랜치 `docs/project-rules`: AGENTS.md 프로젝트 칸·공개 규칙, `.claude/settings.json`(강제 확인), `HISTORY.md`, 인계 규칙 정리. PR은 사용자 확인 후.

## 막힌 점 / 확인이 필요한 결정
- 없음.

## 모르면 실수할 것
- GitHub `main`은 브랜치 보호가 켜져 있어 직접 push가 거절된다. 항상 브랜치 → PR.
- **집 PC는 아직 옛 설정이다.** `C:\sample\새프로젝트_설정\`의 최신 파일로 네 곳을 다시 복사해야 한다: `개인설정.md` → `~/.claude/CLAUDE.md`와 `~/.codex/AGENTS.md`, `skills/handoff/` → `~/.claude/skills/handoff/`와 `~/.codex/skills/handoff/`. (그 폴더는 git이 아니므로 집 PC로 옮기는 방법도 필요)
- Matt Pocock 스킬을 다시 설치하면 Claude 쪽 handoff 스킬이 덮어써진다. 설치 뒤 설명이 한국어인지 확인.

## 마지막 검증
- `.claude/settings.json` JSON 문법 확인, GitHub 보호 설정 조회(PR 필수·관리자 포함·강제 push 금지)
- 합친 handoff 스킬이 Claude·Codex 두 곳에서 같은 파일인지 `cmp`로 확인
- 코드·테스트는 아직 없음

## 추천 스킬
- `/tdd`: 티켓마다 HTTP API 접점에서 실패하는 테스트부터 쓰고 구현한다.
- `/code-review`: 티켓 하나를 마치면 PR 전에 명세·티켓 기준으로 검토한다.
- `/wizard`: 티켓 09에서 사람이 할 배포 설정 단계를 스크립트로 만든다.

## 참고 문서
- 명세: `.scratch/notice-board/spec.md`
- 용어집: `CONTEXT.md`
- 결정: `docs/adr/0001-pages-functions-api-proxy.md`, `docs/adr/0002-admin-credentials-in-env.md`, `docs/adr/0003-proxy-secret-and-ip-lockout.md`
- 인계 규칙: `docs/agents/handoff.md`
