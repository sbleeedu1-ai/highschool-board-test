import { describe, expect, it } from 'vitest'
import { linkify } from './linkify'

describe('linkify', () => {
  it('주소가 없는 글은 그대로 글 하나다', () => {
    expect(linkify('내일 급식은 비빔밥입니다')).toEqual([{ type: 'text', value: '내일 급식은 비빔밥입니다' }])
  })

  it('빈 글은 조각이 없다', () => {
    expect(linkify('')).toEqual([])
  })

  it('http 주소를 링크로 바꾼다', () => {
    expect(linkify('안내 http://example.com/a 참고')).toEqual([
      { type: 'text', value: '안내 ' },
      { type: 'link', href: 'http://example.com/a' },
      { type: 'text', value: ' 참고' },
    ])
  })

  it('https 주소를 링크로 바꾼다', () => {
    expect(linkify('https://example.com/a?b=1&c=2')).toEqual([{ type: 'link', href: 'https://example.com/a?b=1&c=2' }])
  })

  it('javascript: 와 ftp:// 는 링크로 바꾸지 않는다', () => {
    expect(linkify('javascript:alert(1)')).toEqual([{ type: 'text', value: 'javascript:alert(1)' }])
    expect(linkify('ftp://x')).toEqual([{ type: 'text', value: 'ftp://x' }])
  })

  it('HTML 태그는 글 그대로 둔다', () => {
    expect(linkify('<script>alert(1)</script>')).toEqual([{ type: 'text', value: '<script>alert(1)</script>' }])
  })

  it('주소 바로 뒤의 마침표, 쉼표, 닫는 괄호는 링크에서 뺀다', () => {
    expect(linkify('http://example.com.')).toEqual([
      { type: 'link', href: 'http://example.com' },
      { type: 'text', value: '.' },
    ])
    expect(linkify('(https://example.com/a), 끝')).toEqual([
      { type: 'text', value: '(' },
      { type: 'link', href: 'https://example.com/a' },
      { type: 'text', value: '), 끝' },
    ])
  })

  it('한 줄에 주소가 여러 개여도 모두 바꾼다', () => {
    expect(linkify('http://a.com 그리고 https://b.com')).toEqual([
      { type: 'link', href: 'http://a.com' },
      { type: 'text', value: ' 그리고 ' },
      { type: 'link', href: 'https://b.com' },
    ])
  })

  it('줄바꿈은 글 조각 안에 그대로 남는다', () => {
    expect(linkify('첫 줄\nhttps://a.com\n\n셋째 줄')).toEqual([
      { type: 'text', value: '첫 줄\n' },
      { type: 'link', href: 'https://a.com' },
      { type: 'text', value: '\n\n셋째 줄' },
    ])
  })
})
