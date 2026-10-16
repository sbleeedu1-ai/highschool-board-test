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
