# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
`/to-spec` 진행 중: 테스트 접점(seam)을 사용자에게 확인받은 뒤 `.scratch/notice-board/spec.md`를 to-spec 틀로 다시 쓰고 `Status: ready-for-agent`로 바꾼다. 그다음 `/to-tickets`.

## 현재 상태
- 설계 합의·명세(주제별 정리판)는 PR #1로 `main`에 병합됨.
- 작업 브랜치: `docs/to-spec` (인계 규칙에 병합 브랜치 삭제 추가 커밋됨, PR 없음)

## 막힌 점 / 확인이 필요한 결정
- 테스트 접점 확인 대기(사용자 답변 필요)

## 마지막 검증
- `git grep GLOSSARY`: 남은 것은 `docs/agents/domain.md`의 "GLOSSARY.md 대신 CONTEXT.md" 안내 2곳뿐(의도된 것)
- 코드·테스트는 아직 없음

## 추천 스킬
- `/to-spec`: 이어서 명세를 틀에 맞춰 다시 쓴다.
- `/to-tickets`: 명세를 세로로 자른 구현 티켓으로 나눈다(백엔드 골격 → 공지 조회 → 관리자 인증 → 등록·수정·삭제 → 배포 순 권장).
- `/tdd`: 티켓마다 실패하는 테스트부터 쓰고 구현한다.

## 참고 문서
- 명세: `.scratch/notice-board/spec.md`
- 용어집: `CONTEXT.md`
- 결정: `docs/adr/0001-pages-functions-api-proxy.md`, `docs/adr/0002-admin-credentials-in-env.md`, `docs/adr/0003-proxy-secret-and-ip-lockout.md`
- 인계 규칙: `docs/agents/handoff.md`
