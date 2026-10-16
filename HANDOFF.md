# HANDOFF

마지막 갱신: 2026-10-16 · Claude · 강의실 PC

## 다음 할 일
사용자가 명세의 "새로 구체화한 항목"(아래)을 확인하면 PR 생성·병합 → `/to-tickets`로 `.scratch/notice-board/issues/`에 티켓을 만든다.

## 현재 상태
- `.scratch/notice-board/spec.md`를 to-spec 틀로 다시 씀(`Status: ready-for-agent`, 사용자 이야기 77개, 테스트 접점 5개는 Q30-Q34 합의).
- 작업 브랜치: `docs/to-spec` (PR 없음, 사용자 확인 필요)

## 막힌 점 / 확인이 필요한 결정
명세를 쓰며 새로 구체화한 항목(사용자 확인 대기):
- API 경로: `/api/auth/login`·`logout`·`me`, 수정은 `PUT`, 목록 응답에 현재 페이지·전체 페이지 수·전체 건수 포함
- 정렬 동률 시 식별자 내림차순, 수정한 적 없으면 수정일시 비움
- 상세에서 목록으로 돌아가면 보던 페이지로(이야기 19), 휴대폰 화면 대응(이야기 22)
- 로그인 성공 시 그 IP의 실패 횟수 초기화(이야기 38)
- 비밀 헤더 없음·틀림은 403, Functions는 방문자가 보낸 가짜 `X-Proxy-Secret`·`X-Client-IP`를 덮어씀

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
