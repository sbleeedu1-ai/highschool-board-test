export type NoticeSummary = { id: number; title: string; createdAt: string }

export type NoticeList = {
  items: NoticeSummary[]
  page: number
  totalPages: number
  totalElements: number
}

/** 공지 목록을 받아 온다. 서버·네트워크 오류는 예외로 던진다. */
export async function fetchNotices(page: number): Promise<NoticeList> {
  const res = await fetch(`/api/notices?page=${page}`)
  if (!res.ok) throw new Error(`공지 목록 요청 실패: ${res.status}`)
  return res.json()
}
