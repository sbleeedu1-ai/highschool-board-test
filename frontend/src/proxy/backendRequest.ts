/** Pages 환경변수(대시보드에서 넣는 값). 저장소에는 값을 적지 않는다 */
export type ProxyEnv = {
  /** Railway 백엔드 주소. 예: https://xxx.up.railway.app */
  BACKEND_URL: string
  /** 백엔드와 같은 값을 넣는 비밀값(ADR-0003) */
  PROXY_SECRET: string
}

/**
 * 방문자가 보낸 /api 요청을 Railway 백엔드로 보낼 요청으로 바꾼다(ADR-0001, ADR-0003).
 * - 경로·쿼리·메서드·본문·쿠키·CSRF 헤더는 그대로 옮긴다.
 * - 방문자 IP(Cloudflare가 알려 주는 CF-Connecting-IP)를 X-Client-IP로, 비밀값을 X-Proxy-Secret으로 붙인다.
 *   방문자가 같은 이름의 헤더를 보냈어도 덮어쓴다.
 */
export async function buildBackendRequest(request: Request, env: ProxyEnv): Promise<Request> {
  const incoming = new URL(request.url)
  const target = new URL(incoming.pathname + incoming.search, env.BACKEND_URL)

  const headers = new Headers(request.headers)
  headers.delete('Host')
  headers.delete('X-Client-IP')
  const visitorIp = request.headers.get('CF-Connecting-IP')
  if (visitorIp) headers.set('X-Client-IP', visitorIp)
  headers.set('X-Proxy-Secret', env.PROXY_SECRET)

  const hasBody = request.method !== 'GET' && request.method !== 'HEAD'
  return new Request(target, {
    method: request.method,
    headers,
    body: hasBody ? await request.arrayBuffer() : undefined,
    // 백엔드의 리다이렉트를 따라가지 않고 그대로 방문자에게 돌려준다
    redirect: 'manual',
  })
}
