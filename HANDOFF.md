# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
티켓 01(걷는 뼈대)부터 구현한다: `.scratch/notice-board/issues/01-walking-skeleton.md`. 01이 끝나면 02와 04는 동시에 진행할 수 있다.

## 현재 상태
- 명세 확정(`ready-for-agent`), 구현 티켓 10개 작성(`.scratch/notice-board/issues/`). 09만 `ready-for-human`.
- 순서: 01 → (02 → 03), (04 → 08) → 05 → (06, 07) → 09 → 10. 배포(09)는 기능과 잠금이 모두 끝난 뒤.
- 작업 브랜치: `docs/tickets` (PR은 사용자 확인 후). 코드는 아직 없다.

## 막힌 점 / 확인이 필요한 결정
- 없음.

## 마지막 검증
- 티켓의 "명세 사용자 이야기" 참조를 모아 1-77번이 모두 덮이는지 스크립트로 대조: 빠진 번호 없음
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
