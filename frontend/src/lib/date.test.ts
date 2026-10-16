import { describe, expect, it } from 'vitest'
import { formatDetailDate, formatListDate } from './date'

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

describe('formatDetailDate', () => {
  it('작성일시를 한국 시간 24시간제로 보여 준다', () => {
    expect(formatDetailDate('2026-10-16T05:30:00Z', null)).toBe('작성 2026-10-16 14:30')
  })

  it('UTC 15시 30분은 한국에서 다음 날 0시 30분이다', () => {
    expect(formatDetailDate('2026-10-16T15:30:00Z', null)).toBe('작성 2026-10-17 00:30')
  })

  it('오후 시각도 24시간제로 보여 준다', () => {
    expect(formatDetailDate('2026-10-16T13:05:00Z', null)).toBe('작성 2026-10-16 22:05')
  })

  it('수정일시가 있을 때만 수정 표시를 덧붙인다', () => {
    expect(formatDetailDate('2026-10-16T05:30:00Z', '2026-10-17T01:00:00Z')).toBe(
      '작성 2026-10-16 14:30 · 수정 2026-10-17 10:00',
    )
  })
})
