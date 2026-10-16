# 01: 걷는 뼈대

**What to build:** 개발자가 백엔드와 프론트를 각각 명령 하나로 로컬에서 켜면, 프론트 화면 하단에 백엔드가 알려 준 버전(커밋 번호, 로컬에서는 `local`)이 보인다. 요청은 Vite 개발 서버의 `/api` 프록시를 거쳐 운영과 같은 "주소 하나" 구조로 흐른다. 이후 티켓들이 쓸 공통 바탕(교체 가능한 시계, 공통 오류 응답 형식, 프로필 구분)과 PR용 CI가 생긴다.

**Blocked by:** None (can start immediately)

**Status:** ready-for-agent

명세 사용자 이야기: 64-65, 71 / 구현 결정 "구조", API 계약의 `/api/version`

- [ ] 백엔드: Spring Boot 4.1.1 + Kotlin + Java 21 + Gradle Kotlin DSL 프로젝트가 백엔드 폴더에 있고, 로컬 실행 명령 하나로 8080에서 뜬다
- [ ] 백엔드: `GET /api/version`이 배포 커밋 번호(`RAILWAY_GIT_COMMIT_SHA`, 없으면 `local`)를 JSON으로 돌려준다
- [ ] 백엔드: 현재 시각을 교체 가능한 시계 하나에서 읽는 구조가 있다(테스트에서 고정 가능)
- [ ] 백엔드: 공통 오류 응답 형식(`code`·`message`·`fieldErrors`)의 기본 처리기가 있고, 알 수 없는 경로는 이 형식의 404를 돌려준다
- [ ] 백엔드: `local`·`test`·`prod` 프로필이 구분되어 있다(`prod`의 DB 연결은 09에서 채운다)
- [ ] 백엔드: HTTP API 접점 통합 테스트로 `/api/version`과 공통 404 형식을 확인한다
- [ ] 프론트: Vite + React + TypeScript + shadcn/ui(Tailwind v4) 프로젝트가 프론트 폴더에 있고, Node 24·npm으로 5173에서 뜬다
- [ ] 프론트: 개발 서버가 `/api/*`를 로컬 백엔드로 프록시하고, 화면 하단에 버전이 보인다
- [ ] 프론트: Vitest가 설정되어 있고 최소 1개 테스트가 돈다
- [ ] CI: PR과 `main` push에서 백엔드 테스트, 프론트 lint·타입 검사·Vitest·빌드가 돈다
- [ ] 로컬 전용 파일(H2 데이터, `.env.local` 등)이 git에서 제외된다
- [ ] `AGENTS.md`의 "프로젝트" 칸에 백엔드·프론트 실행·테스트 명령을 채운다
