import { describe, expect, it } from 'vitest'
import { readCookie } from './cookie'

describe('readCookie', () => {
  it('여러 쿠키 중 이름이 같은 값을 꺼낸다', () => {
    expect(readCookie('a=1; XSRF-TOKEN=abc-123; b=2', 'XSRF-TOKEN')).toBe('abc-123')
  })

  it('이름이 비슷하기만 한 쿠키는 고르지 않는다', () => {
    expect(readCookie('MY-XSRF-TOKEN=nope', 'XSRF-TOKEN')).toBeNull()
  })

  it('없으면 null이다', () => {
    expect(readCookie('', 'XSRF-TOKEN')).toBeNull()
  })

  it('URL 인코딩된 값은 풀어서 돌려준다', () => {
    expect(readCookie('XSRF-TOKEN=a%2Fb%3D', 'XSRF-TOKEN')).toBe('a/b=')
  })
})
