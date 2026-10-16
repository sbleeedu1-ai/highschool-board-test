import { readCookie } from '@/lib/cookie'

/** 공통 오류 형식(code·message·fieldErrors)을 담은 API 오류 */
export class ApiError extends Error {
  readonly status: number
  readonly code: string | null

  constructor(status: number, code: string | null, message: string) {
    super(message)
    this.status = status
    this.code = code
  }
}

/** 로그인이 만료되어 로그인 화면으로 보낼 때 붙이는 표시 */
export const EXPIRED_QUERY = 'expired=1'

/**
 * 변경 요청(POST·PUT·DELETE)을 보낸다. CSRF 쿠키 값을 X-XSRF-TOKEN 헤더로 붙인다.
 * - 401(로그인 안 됨·만료): 로그인 화면으로 이동한다. 작성 중 내용은 잃을 수 있다고 그 화면에서 안내한다.
 * - 그 밖의 실패: ApiError를 던진다. 403(CSRF)이면 화면이 "다시 시도해 주세요"를 보여 준다.
 */
export async function sendJson<T>(method: 'POST' | 'PUT' | 'DELETE', path: string, body?: unknown): Promise<T | null> {
  const headers: Record<string, string> = { 'Content-Type': 'application/json' }
  const token = readCookie(document.cookie, 'XSRF-TOKEN')
  if (token) headers['X-XSRF-TOKEN'] = token

  const res = await fetch(path, {
    method,
    headers,
    body: body === undefined ? undefined : JSON.stringify(body),
  })

  if (res.status === 401 && path !== '/api/auth/login') {
    const from = encodeURIComponent(window.location.pathname + window.location.search)
    window.location.assign(`/admin/login?${EXPIRED_QUERY}&from=${from}`)
    throw new ApiError(401, 'UNAUTHORIZED', '로그인이 필요합니다')
  }
  if (!res.ok) {
    const error = await res.json().catch(() => null)
    throw new ApiError(res.status, error?.code ?? null, error?.message ?? '잠시 후 다시 시도해 주세요')
  }
  return res.status === 204 ? null : res.json()
}
