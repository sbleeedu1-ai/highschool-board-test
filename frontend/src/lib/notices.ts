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

export type NoticeDetail = {
  id: number
  title: string
  body: string
  createdAt: string
  /** 수정한 적이 없으면 null */
  updatedAt: string | null
}

/** 공지 상세를 받아 온다. 없는 공지(404)는 null, 그 밖의 서버·네트워크 오류는 예외로 던진다. */
export async function fetchNotice(id: string): Promise<NoticeDetail | null> {
  const res = await fetch(`/api/notices/${encodeURIComponent(id)}`)
  if (res.status === 404) return null
  if (!res.ok) throw new Error(`공지 상세 요청 실패: ${res.status}`)
  return res.json()
}
