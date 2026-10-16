// Cloudflare Pages Functions: 방문자의 /api/* 요청을 Railway 백엔드로 전달한다(ADR-0001).
// 바꾸는 규칙은 src/proxy/backendRequest.ts의 순수 함수에 있고, 여기서는 보내고 받은 응답을 그대로 돌려줄 뿐이다.
// 백엔드 응답(상태 코드·쿠키 포함)은 손대지 않는다.
import { buildBackendRequest, type ProxyEnv } from '../../src/proxy/backendRequest'

export const onRequest = async (context: { request: Request; env: ProxyEnv }): Promise<Response> =>
  fetch(await buildBackendRequest(context.request, context.env))
