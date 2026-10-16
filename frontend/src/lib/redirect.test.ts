import { describe, expect, it } from 'vitest'
import { loginPath, safeReturnPath } from './redirect'

const ORIGIN = 'https://board.example.test'

describe('safeReturnPath', () => {
  it('같은 사이트 안의 주소는 그대로 돌려준다', () => {
    expect(safeReturnPath('/admin/notices/new', ORIGIN)).toBe('/admin/notices/new')
    expect(safeReturnPath('/?page=2', ORIGIN)).toBe('/?page=2')
  })

  it('다른 사이트로 가는 주소는 첫 화면으로 바꾼다', () => {
    expect(safeReturnPath('https://evil.test/x', ORIGIN)).toBe('/')
    expect(safeReturnPath('//evil.test/x', ORIGIN)).toBe('/')
    expect(safeReturnPath('/\\evil.test/x', ORIGIN)).toBe('/')
    expect(safeReturnPath('javascript:alert(1)', ORIGIN)).toBe('/')
  })

  it('없으면 첫 화면이다', () => {
    expect(safeReturnPath(null, ORIGIN)).toBe('/')
    expect(safeReturnPath('', ORIGIN)).toBe('/')
  })
})

describe('loginPath', () => {
  it('돌아올 주소를 붙인 로그인 화면 주소를 만든다', () => {
    expect(loginPath('/admin/notices/new?x=1')).toBe('/admin/login?from=%2Fadmin%2Fnotices%2Fnew%3Fx%3D1')
  })

  it('만료로 보낼 때는 만료 표시를 붙인다', () => {
    expect(loginPath('/admin/notices/3/edit', { expired: true })).toBe(
      '/admin/login?expired=1&from=%2Fadmin%2Fnotices%2F3%2Fedit',
    )
  })
})
