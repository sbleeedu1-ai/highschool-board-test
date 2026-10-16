/** 쿠키 문자열(document.cookie)에서 이름이 정확히 같은 쿠키 값을 꺼낸다. 없으면 null. */
export function readCookie(cookieString: string, name: string): string | null {
  for (const part of cookieString.split(';')) {
    const [key, ...rest] = part.trim().split('=')
    if (key === name) return decodeURIComponent(rest.join('='))
  }
  return null
}
