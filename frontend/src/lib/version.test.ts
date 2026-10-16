import { describe, expect, it } from 'vitest'
import { versionLabel } from './version'

describe('versionLabel', () => {
  it('커밋 번호는 앞 7자리만 보여 준다', () => {
    expect(versionLabel('0123456789abcdef')).toBe('버전 0123456')
  })

  it('로컬 버전은 그대로 보여 준다', () => {
    expect(versionLabel('local')).toBe('버전 local')
  })

  it('버전을 받지 못했으면 확인 불가로 보여 준다', () => {
    expect(versionLabel(null)).toBe('버전 확인 불가')
  })
})
