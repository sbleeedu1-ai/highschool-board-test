import { describe, expect, it } from 'vitest'
import { parsePage } from './page'

describe('parsePage', () => {
  it('정상적인 숫자는 그대로 쓴다', () => {
    expect(parsePage('1')).toBe(1)
    expect(parsePage('7')).toBe(7)
  })

  it('주소에 page가 없으면 1이다', () => {
    expect(parsePage(null)).toBe(1)
  })

  it('숫자가 아니면 1이다', () => {
    expect(parsePage('abc')).toBe(1)
    expect(parsePage('')).toBe(1)
    expect(parsePage('1.5')).toBe(1)
    expect(parsePage('3abc')).toBe(1)
  })

  it('1보다 작으면 1이다', () => {
    expect(parsePage('0')).toBe(1)
    expect(parsePage('-1')).toBe(1)
  })
})
