// 등록·수정 화면의 입력 검사. 백엔드(NoticeInput.kt)와 같은 규칙·문구를 쓴다. 최종 판정은 백엔드가 한다.

export const TITLE_MAX = 100
export const BODY_MAX = 5000

/** 칸 이름 → 안내 문구. 비어 있으면 통과 */
export type FieldErrors = Partial<Record<'title' | 'body', string>>

export function validateNotice(title: string, body: string): FieldErrors {
  const errors: FieldErrors = {}
  const t = title.trim()
  const b = body.trim()
  if (t.length === 0) errors.title = '제목을 입력해 주세요'
  else if (t.length > TITLE_MAX) errors.title = `제목은 ${TITLE_MAX}자까지 쓸 수 있습니다`
  if (b.length === 0) errors.body = '본문을 입력해 주세요'
  else if (b.length > BODY_MAX) errors.body = `본문은 ${BODY_MAX.toLocaleString('en-US')}자까지 쓸 수 있습니다`
  return errors
}

/** 입력 중 보여 줄 글자 수(예: `37/100`). 앞뒤 공백은 세지 않는다 */
export function countLabel(value: string, max: number): string {
  return `${value.trim().length}/${max}`
}
