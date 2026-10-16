# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
`/to-tickets`로 명세 `.scratch/notice-board/spec.md`를 구현 티켓으로 쪼개 `.scratch/notice-board/issues/`에 만든다.

## 현재 상태
- 설계 합의 완료(grill-with-docs, Q1-Q29), 명세 승인됨. 코드는 아직 없다.
- 설계 문서는 PR로 `main`에 병합됨.

## 막힌 점 / 확인이 필요한 결정
- 없음. (세션 뒤 추가 확인한 3가지 — 방문자 경로 `/`·`/notices/:id`, 잠금 시 429, 로그아웃 버튼은 로그인 상태에서만 — 사용자 동의 완료, 명세에 반영)

## 마지막 검증
- `git grep GLOSSARY`: 남은 것은 `docs/agents/domain.md`의 "GLOSSARY.md 대신 CONTEXT.md" 안내 2곳뿐(의도된 것)
- 코드·테스트는 아직 없음

## 추천 스킬
- `/to-tickets`: 명세를 세로로 자른 구현 티켓으로 나눈다(백엔드 골격 → 공지 조회 → 관리자 인증 → 등록·수정·삭제 → 배포 순 권장).
- `/tdd`: 티켓마다 실패하는 테스트부터 쓰고 구현한다.

## 참고 문서
- 명세: `.scratch/notice-board/spec.md`
- 용어집: `CONTEXT.md`
- 결정: `docs/adr/0001-pages-functions-api-proxy.md`, `docs/adr/0002-admin-credentials-in-env.md`, `docs/adr/0003-proxy-secret-and-ip-lockout.md`
- 인계 규칙: `docs/agents/handoff.md`
