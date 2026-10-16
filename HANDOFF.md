# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
명세 `.scratch/notice-board/spec.md`를 사용자가 검토하고, 승인되면 `/to-tickets`로 구현 티켓을 `.scratch/notice-board/issues/`에 쪼갠다.

## 현재 상태
- 설계 합의 완료(grill-with-docs, Q1-Q29). 코드는 아직 없다.
- 작업 브랜치: `docs/notice-board-design` (PR은 아직 만들지 않음, 사용자 확인 필요)

## 막힌 점 / 확인이 필요한 결정
명세에 들어갔지만 세션에서 명시적으로 묻지 않은 항목(검토 때 확인):
- 방문자 화면 경로: 목록 `/`, 상세 `/notices/:id`
- 로그인 잠금 시 상태 코드 429(명세에 "구현 시 확정"으로 표시)
- 로그아웃 버튼은 로그인 상태에서만 표시

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
