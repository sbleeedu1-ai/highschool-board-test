import { describe, expect, it } from 'vitest'
import { buildBackendRequest } from './backendRequest'

const ENV = { BACKEND_URL: 'https://backend.example.test', PROXY_SECRET: 'real-secret' }

function visitorRequest(init: RequestInit & { url?: string } = {}) {
  const headers = new Headers(init.headers)
  headers.set('CF-Connecting-IP', '203.0.113.9')
  return new Request(init.url ?? 'https://board.example.test/api/notices?page=2', { ...init, headers })
}

describe('buildBackendRequest', () => {
  it('경로와 쿼리를 그대로 두고 백엔드 주소로 보낸다', async () => {
    const req = await buildBackendRequest(visitorRequest(), ENV)
    expect(req.url).toBe('https://backend.example.test/api/notices?page=2')
    expect(req.method).toBe('GET')
  })

  it('방문자 IP와 비밀값을 붙인다', async () => {
    const req = await buildBackendRequest(visitorRequest(), ENV)
    expect(req.headers.get('X-Client-IP')).toBe('203.0.113.9')
    expect(req.headers.get('X-Proxy-Secret')).toBe('real-secret')
  })

  it('방문자가 보낸 가짜 비밀값·IP는 덮어쓴다', async () => {
    const req = await buildBackendRequest(
      visitorRequest({ headers: { 'X-Proxy-Secret': 'guess', 'X-Client-IP': '198.51.100.1' } }),
      ENV,
    )
    expect(req.headers.get('X-Proxy-Secret')).toBe('real-secret')
    expect(req.headers.get('X-Client-IP')).toBe('203.0.113.9')
  })

  it('메서드·본문·쿠키·CSRF 헤더를 그대로 옮긴다', async () => {
    const req = await buildBackendRequest(
      visitorRequest({
        url: 'https://board.example.test/api/auth/login',
        method: 'POST',
        body: '{"username":"a","password":"b"}',
        headers: { Cookie: 'JSESSIONID=s1; XSRF-TOKEN=t1', 'X-XSRF-TOKEN': 't1', 'Content-Type': 'application/json' },
      }),
      ENV,
    )
    expect(req.method).toBe('POST')
    expect(await req.text()).toBe('{"username":"a","password":"b"}')
    expect(req.headers.get('Cookie')).toBe('JSESSIONID=s1; XSRF-TOKEN=t1')
    expect(req.headers.get('X-XSRF-TOKEN')).toBe('t1')
    expect(req.headers.get('Content-Type')).toBe('application/json')
  })

  it('백엔드 주소 끝에 / 가 있어도 주소가 두 번 꺾이지 않는다', async () => {
    const req = await buildBackendRequest(visitorRequest(), { ...ENV, BACKEND_URL: 'https://backend.example.test/' })
    expect(req.url).toBe('https://backend.example.test/api/notices?page=2')
  })
})
