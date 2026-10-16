# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
1. 티켓 01(걷는 뼈대)부터 구현한다: `.scratch/notice-board/issues/01-walking-skeleton.md`. 01이 끝나면 02와 04는 동시에 진행할 수 있다.

## 현재 상태
- 명세·티켓 10개, 프로젝트 규칙·`HISTORY.md`·강제 확인 설정은 모두 `main`에 있다. 코드는 아직 없다.
- 개인 설정(개인설정·handoff 스킬·설치 스크립트)은 비공개 저장소 `sbleeedu1-ai/new-setup`으로 관리한다.

## 막힌 점 / 확인이 필요한 결정
- 없음.

## 모르면 실수할 것
- GitHub `main`은 브랜치 보호가 켜져 있어 직접 push가 거절된다. 항상 브랜치 → PR.
- **집 PC는 아직 옛 설정이다.** claude·codex를 켜기 전에 터미널에서 `git clone https://github.com/sbleeedu1-ai/new-setup`(비공개, 처음 한 번) → 그 폴더에서 `install.cmd` 실행(mac이면 `./install.sh`). 설치 절차는 그 저장소의 README.
- Matt Pocock 스킬 업데이트는 `npx skills update`가 아니라 `new-setup`의 `install.cmd`로 한다(그냥 업데이트하면 handoff 스킬이 Matt 버전으로 덮어써짐).

## 마지막 검증
- `.claude/settings.json` JSON 문법 확인, GitHub 보호 설정 조회(PR 필수·관리자 포함·강제 push 금지)
- 강의실 PC에서 `new-setup`의 `install.cmd`를 실제로 실행해 통과(Matt 스킬 재설치 → Matt handoff 제거 → 내 handoff를 `~/.claude/skills`·`~/.agents/skills`에 복사 → 검사), 두 번째 실행도 통과
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
