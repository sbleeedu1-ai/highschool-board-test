# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
`/to-tickets`로 명세 `.scratch/notice-board/spec.md`를 구현 티켓으로 쪼개 `.scratch/notice-board/issues/`에 만든다.

## 현재 상태
- 명세(to-spec 틀, `Status: ready-for-agent`)는 사용자 승인 후 PR로 `main`에 병합됨.
- 코드는 아직 없다.

## 막힌 점 / 확인이 필요한 결정
- 없음. (명세 작성 중 새로 구체화한 5가지 — API 경로·`PUT` 수정, 정렬 동률·수정일시 비움, 목록 복귀·휴대폰 대응, 성공 시 실패 횟수 초기화, 비밀 헤더 403·가짜 헤더 덮어쓰기 — 사용자 동의 완료)

## 마지막 검증
- 명세 문서 작업만 함. 코드·테스트 없음.
- 명세에 저장소 파일 경로·코드 조각이 없는지, 비밀값·`~~` 표기가 없는지 검색 확인

## 추천 스킬
- `/to-tickets`: 명세를 세로로 자른 구현 티켓으로 나눈다(백엔드 골격 → 공지 조회 → 관리자 인증 → 등록·수정·삭제 → 배포 순 권장).
- `/tdd`: 티켓마다 실패하는 테스트부터 쓰고 구현한다.

## 참고 문서
- 명세: `.scratch/notice-board/spec.md`
- 용어집: `CONTEXT.md`
- 결정: `docs/adr/0001-pages-functions-api-proxy.md`, `docs/adr/0002-admin-credentials-in-env.md`, `docs/adr/0003-proxy-secret-and-ip-lockout.md`
- 인계 규칙: `docs/agents/handoff.md`
