const KOREA_TIME_ZONE = 'Asia/Seoul'

const dateFormat = new Intl.DateTimeFormat('en-CA', {
  timeZone: KOREA_TIME_ZONE,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
})

/** UTC 시각(ISO 문자열)을 한국 시간 날짜 `YYYY-MM-DD`로 바꾼다. 목록에서 쓴다. */
export function formatListDate(isoUtc: string): string {
  const parts = dateFormat.formatToParts(new Date(isoUtc))
  const get = (type: string) => parts.find((p) => p.type === type)!.value
  return `${get('year')}-${get('month')}-${get('day')}`
}

const dateTimeFormat = new Intl.DateTimeFormat('en-CA', {
  timeZone: KOREA_TIME_ZONE,
  year: 'numeric',
  month: '2-digit',
  day: '2-digit',
  hour: '2-digit',
  minute: '2-digit',
  hourCycle: 'h23',
})

/** UTC 시각(ISO 문자열)을 한국 시간 `YYYY-MM-DD HH:mm`(24시간제)로 바꾼다. */
function formatDateTime(isoUtc: string): string {
  const parts = dateTimeFormat.formatToParts(new Date(isoUtc))
  const get = (type: string) => parts.find((p) => p.type === type)!.value
  return `${get('year')}-${get('month')}-${get('day')} ${get('hour')}:${get('minute')}`
}

/** 상세 화면의 날짜 줄. 수정한 적이 있을 때만 ` · 수정 …`을 덧붙인다. */
export function formatDetailDate(createdAt: string, updatedAt: string | null): string {
  const created = `작성 ${formatDateTime(createdAt)}`
  return updatedAt === null ? created : `${created} · 수정 ${formatDateTime(updatedAt)}`
}
