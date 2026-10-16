/**
 * 로그인 뒤 돌아갈 주소를 고른다. 같은 사이트 안의 주소만 허락하고, 그 밖에는 첫 화면('/')으로 보낸다.
 * `/\evil.test`처럼 브라우저가 다른 사이트로 해석하는 주소도 실제로 풀어 본 뒤 사이트가 같은지로 판단한다.
 */
export function safeReturnPath(from: string | null, origin: string): string {
  if (!from || !from.startsWith('/')) return '/'
  try {
    const url = new URL(from, origin)
    if (url.origin !== origin) return '/'
    return url.pathname + url.search + url.hash
  } catch {
    return '/'
  }
}

/** 로그인 화면 주소. 로그인 뒤 돌아올 주소(from)를 붙이고, 만료로 보낼 때는 만료 표시를 붙인다 */
export function loginPath(from: string, options: { expired?: boolean } = {}): string {
  const params = new URLSearchParams()
  if (options.expired) params.set('expired', '1')
  params.set('from', from)
  return `/admin/login?${params.toString()}`
}
