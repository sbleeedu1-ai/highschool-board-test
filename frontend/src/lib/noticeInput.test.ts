import { describe, expect, it } from 'vitest'
import { countLabel, validateNotice } from './noticeInput'

describe('validateNotice', () => {
  it('제목 100자·본문 5,000자까지는 통과한다', () => {
    expect(validateNotice('가'.repeat(100), '가'.repeat(5000))).toEqual({})
  })

  it('제목 101자, 본문 5,001자는 칸별로 안내한다', () => {
    expect(validateNotice('가'.repeat(101), '가'.repeat(5001))).toEqual({
      title: '제목은 100자까지 쓸 수 있습니다',
      body: '본문은 5,000자까지 쓸 수 있습니다',
    })
  })

  it('공백만 있으면 비어 있는 것으로 본다', () => {
    expect(validateNotice('   ', '\n\t ')).toEqual({ title: '제목을 입력해 주세요', body: '본문을 입력해 주세요' })
  })

  it('앞뒤 공백은 글자 수에 넣지 않는다', () => {
    expect(validateNotice('  ' + '가'.repeat(100) + '  ', ' 본문 ')).toEqual({})
  })
})

describe('countLabel', () => {
  it('앞뒤 공백을 뺀 글자 수와 최대 글자 수를 보여 준다', () => {
    expect(countLabel('  가나다  ', 100)).toBe('3/100')
  })
})
