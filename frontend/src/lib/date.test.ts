import { describe, expect, it } from 'vitest'
import { formatListDate } from './date'

describe('formatListDate', () => {
  it('UTC 시각을 한국 날짜로 바꾼다', () => {
    expect(formatListDate('2026-10-16T05:30:00Z')).toBe('2026-10-16')
  })

  it('UTC 15시부터는 한국에서 다음 날이다', () => {
    expect(formatListDate('2026-10-16T14:59:59Z')).toBe('2026-10-16')
    expect(formatListDate('2026-10-16T15:00:00Z')).toBe('2026-10-17')
    expect(formatListDate('2026-10-16T15:30:00Z')).toBe('2026-10-17')
  })

  it('해가 바뀌는 경계도 한국 시간 기준이다', () => {
    expect(formatListDate('2026-12-31T15:00:00Z')).toBe('2027-01-01')
  })
})
