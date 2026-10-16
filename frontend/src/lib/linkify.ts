/** 본문을 잘라 놓은 조각. 글은 그대로(줄바꿈 포함) 두고, 주소만 링크로 구분한다. */
export type Segment = { type: 'text'; value: string } | { type: 'link'; href: string }

// http:// 또는 https:// 로 시작해 공백·< > " 앞까지. 끝이 마침표·쉼표·닫는 괄호 같은 문장부호이면 링크에서 뺀다.
const URL_PATTERN = /https?:\/\/[^\s<>"]*[^\s<>".,;:!?)\]}']/g

/** 본문에서 http(s) 주소만 링크 조각으로 나눈다. HTML이나 다른 방식(javascript: 등)의 주소는 글로 남긴다. */
export function linkify(body: string): Segment[] {
  const segments: Segment[] = []
  let last = 0
  for (const match of body.matchAll(URL_PATTERN)) {
    if (match.index > last) segments.push({ type: 'text', value: body.slice(last, match.index) })
    segments.push({ type: 'link', href: match[0] })
    last = match.index + match[0].length
  }
  if (last < body.length) segments.push({ type: 'text', value: body.slice(last) })
  return segments
}
