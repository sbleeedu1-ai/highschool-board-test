import { useEffect, useState } from 'react'
import { Link, useLocation, useParams } from 'react-router'
import { Button } from '@/components/ui/button'
import { formatDetailDate } from '@/lib/date'
import { linkify } from '@/lib/linkify'
import { fetchNotice, type NoticeDetail } from '@/lib/notices'
import { parsePage } from '@/lib/page'

// key: 어떤 요청의 결과인지(공지 번호와 재시도 횟수). notice가 undefined면 오류, null이면 없는 공지.
type Result = { key: string; notice: NoticeDetail | null | undefined }

export default function NoticeDetailPage() {
  const { id = '' } = useParams()
  const location = useLocation()
  // 목록에서 눌러 들어왔으면 보던 페이지를 state로 받는다. 주소로 바로 들어오면 없으므로 1페이지(`/`)로 보낸다.
  const fromPage = parsePage(String((location.state as { page?: number } | null)?.page ?? ''))
  const listPath = fromPage === 1 ? '/' : `/?page=${fromPage}`

  const [result, setResult] = useState<Result | null>(null)
  const [retry, setRetry] = useState(0)
  const key = `${id}:${retry}`

  useEffect(() => {
    let cancelled = false
    fetchNotice(id)
      .then((notice) => !cancelled && setResult({ key, notice }))
      .catch(() => !cancelled && setResult({ key, notice: undefined }))
    return () => {
      cancelled = true
    }
  }, [id, key])

  if (result?.key !== key) {
    return <p className="py-12 text-center text-muted-foreground">불러오는 중입니다</p>
  }

  if (result.notice === undefined) {
    return (
      <div className="flex flex-col items-center gap-4 py-12">
        <p>잠시 후 다시 시도해 주세요</p>
        <Button onClick={() => setRetry((n) => n + 1)}>다시 시도</Button>
      </div>
    )
  }

  if (result.notice === null) {
    return (
      <div className="flex flex-col items-center gap-4 py-12">
        <p>공지를 찾을 수 없습니다</p>
        <Button asChild>
          <Link to={listPath}>목록으로</Link>
        </Button>
      </div>
    )
  }

  const { title, body, createdAt, updatedAt } = result.notice

  return (
    <article className="flex flex-col gap-6">
      <header className="flex flex-col gap-2 border-b pb-4">
        <h2 className="break-words text-xl font-bold">{title}</h2>
        <p className="text-sm text-muted-foreground">{formatDetailDate(createdAt, updatedAt)}</p>
      </header>
      {/* 본문은 글로만 그린다(HTML로 넣지 않음). 줄바꿈은 그대로, 주소만 새 탭 링크 */}
      <div className="whitespace-pre-wrap break-words">
        {linkify(body).map((segment, i) =>
          segment.type === 'link' ? (
            <a
              key={i}
              href={segment.href}
              target="_blank"
              rel="noopener noreferrer"
              className="text-primary underline"
            >
              {segment.href}
            </a>
          ) : (
            segment.value
          ),
        )}
      </div>
      <div>
        <Button variant="outline" asChild>
          <Link to={listPath}>목록으로</Link>
        </Button>
      </div>
    </article>
  )
}
