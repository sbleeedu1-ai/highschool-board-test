/** 주소의 page 값을 페이지 번호로 바꾼다. 없거나 숫자가 아니거나 1보다 작으면 1페이지. */
export function parsePage(value: string | null): number {
  if (value === null || !/^\d+$/.test(value)) return 1
  const n = Number(value)
  return Number.isSafeInteger(n) && n >= 1 ? n : 1
}
