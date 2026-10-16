# API는 Cloudflare Pages Functions가 Railway로 전달한다

프론트(Cloudflare Pages)와 백엔드(Railway)는 서로 다른 사이트 주소에 올라가므로, 백엔드가 준 로그인 쿠키를 Safari 등 브라우저가 교차 사이트 쿠키로 막을 수 있다. 그래서 방문자는 Pages 주소 하나만 쓰고, `frontend/functions/`의 Pages Functions가 `/api/*` 요청을 Railway로 전달해 브라우저 입장에서 같은 출처가 되게 했다. 덕분에 CORS 설정도 필요 없다.

## Considered Options

- **내 도메인 구매**(`school.example.kr` + `api.school.example.kr`): 같은 사이트가 되어 쿠키 문제는 풀리지만 도메인 구매·관리 비용이 든다.
- **토큰을 프론트가 직접 보관**: 쿠키 문제는 피하지만 브라우저 저장소의 토큰은 XSS로 탈취될 위험이 있다.

## Consequences

- Functions 요청은 Cloudflare 무료 플랜에서 Workers 한도(하루 10만, UTC 자정 초기화)에 포함된다. 정적 파일 요청은 무제한이다.
- Railway 백엔드는 공개 주소를 갖지만, 직접 접근은 ADR-0003의 비밀 헤더로 막는다.
