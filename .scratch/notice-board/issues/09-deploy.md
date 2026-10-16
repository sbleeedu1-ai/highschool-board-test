# 09: 배포

**What to build:** `main`에 병합하면 CI를 통과한 뒤 백엔드는 Railway, 프론트는 Cloudflare Pages에 자동 배포되고, 운영 주소 하나에서 공지 게시판 전체가 동작한다. 배포가 정말 끝났는지는 자동 확인 단계가 판정한다. 계정·서비스·비밀값 설정처럼 사람만 할 수 있는 일은, 에이전트가 만든 wizard 스크립트를 사람이 한 번 실행해서 끝낸다.

**Blocked by:** 06 (공지 수정), 07 (공지 삭제), 08 (비밀 헤더와 로그인 잠금)

**Status:** ready-for-human

명세 사용자 이야기: 62-63, 73-75 / 구현 결정 "CI/CD", "환경별 DB"

### 에이전트 작업

- [ ] `prod` 프로필: Railway PostgreSQL 변수(`PGHOST`·`PGPORT`·`PGUSER`·`PGPASSWORD`·`PGDATABASE`)로 연결하고 `PORT`로 listen한다
- [ ] Railway 설정 파일: 헬스체크는 Actuator 상태 확인 경로. 설정 파일은 Root Directory를 따르지 않으므로 절대경로로 지정한다
- [ ] 프론트 배포 워크플로: `main`에서 CI가 통과하면 빌드하고 `cloudflare/wrangler-action@v4`로 `pages deploy`(Functions 포함)
- [ ] 배포 완료 확인 단계: Pages 주소의 `/api/version`이 이번 push 커밋이 될 때까지 최대 10분 확인한 뒤 `/api/notices`가 200인지 본다
- [ ] `/wizard` 스킬로 사람 작업 스크립트를 만든다. 아래 사람 작업을 단계별로 안내하고, 비밀값은 가려서 입력받아 GitHub 시크릿 등 정해진 위치에만 저장한다

### 사람 작업 (wizard 실행)

- [ ] Railway: 프로젝트·백엔드 서비스·PostgreSQL 생성. Root Directory와 Watch Paths를 백엔드 폴더로 지정, "Wait for CI" 켜기, 인스턴스 1대
- [ ] Railway 환경변수: 관리자 아이디, 관리자 비밀번호 해시, 프록시 비밀값, `SPRING_PROFILES_ACTIVE=prod`, Postgres 참조
- [ ] Cloudflare: Pages 프로젝트 생성, API 토큰(Pages Edit 권한) 발급, Pages 환경변수(백엔드 주소, 프록시 비밀값)
- [ ] GitHub 시크릿: `CLOUDFLARE_API_TOKEN`, `CLOUDFLARE_ACCOUNT_ID`

### 완료 확인

- [ ] 배포 완료 기준: (a) Railway 헬스체크 200·새 배포 활성 (b) Pages 배포 성공 (c) 확인 단계 통과
- [ ] 비밀값이 저장소·로그·문서 어디에도 없다
