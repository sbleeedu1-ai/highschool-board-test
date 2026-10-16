import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/lib/auth'
import { formatListDate } from '@/lib/date'
import { fetchNotices, type NoticeList } from '@/lib/notices'
import { parsePage } from '@/lib/page'

// key: 어떤 요청의 결과인지(페이지 번호와 재시도 횟수). 현재 요청과 다르면 아직 받는 중이다.
type Result = { key: string; list: NoticeList | null }

/** 한 번에 보여 줄 페이지 번호 개수(현재 페이지 앞뒤 2개씩). 좁은 화면에서도 한 줄에 들어가게 한다. */
function pageNumbers(current: number, totalPages: number): number[] {
  const start = Math.max(1, Math.min(current - 2, totalPages - 4))
  const end = Math.min(totalPages, start + 4)
  return Array.from({ length: end - start + 1 }, (_, i) => start + i)
}

/** 공지 목록 화면. 로그인한 관리자에게만 [새 공지] 버튼이 보인다(공지가 하나도 없을 때도) */
export default function NoticeListPage() {
  const { me } = useAuth()
  return (
    <div className="flex flex-col gap-4">
      {me?.authenticated && (
        <div className="flex justify-end">
          <Button asChild>
            <Link to="/admin/notices/new">새 공지</Link>
          </Button>
        </div>
      )}
      <NoticeList />
    </div>
  )
}

function NoticeList() {
  const [searchParams, setSearchParams] = useSearchParams()
  const page = parsePage(searchParams.get('page'))
  const [result, setResult] = useState<Result | null>(null)
  // 값이 바뀔 때마다 다시 받아 오게 하는 용도(다시 시도 버튼)
  const [retry, setRetry] = useState(0)
  const key = `${page}:${retry}`

  useEffect(() => {
    let cancelled = false
    fetchNotices(page)
      .then((list) => !cancelled && setResult({ key, list }))
      .catch(() => !cancelled && setResult({ key, list: null }))
    return () => {
      cancelled = true
    }
  }, [page, key])

  const goTo = (n: number) => setSearchParams(n === 1 ? {} : { page: String(n) })

  if (result?.key !== key) {
    return <p className="py-12 text-center text-muted-foreground">불러오는 중입니다</p>
  }

  if (result.list === null) {
    return (
      <div className="flex flex-col items-center gap-4 py-12">
        <p>잠시 후 다시 시도해 주세요</p>
        <Button onClick={() => setRetry((n) => n + 1)}>다시 시도</Button>
      </div>
    )
  }

  const { items, totalPages } = result.list

  if (items.length === 0) {
    return (
      <div className="flex flex-col items-center gap-4 py-12">
        <p>공지가 없습니다</p>
        <Button onClick={() => goTo(1)}>첫 페이지로</Button>
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-6">
      <ul className="divide-y rounded-lg border">
        {items.map((notice) => (
          <li key={notice.id}>
            <Link
              to={`/notices/${notice.id}`}
              state={{ page }}
              className="flex flex-col gap-1 px-4 py-3 hover:bg-muted sm:flex-row sm:items-center sm:justify-between sm:gap-4"
            >
              <span className="break-words font-medium">{notice.title}</span>
              <time className="shrink-0 text-sm text-muted-foreground" dateTime={notice.createdAt}>
                {formatListDate(notice.createdAt)}
              </time>
            </Link>
          </li>
        ))}
      </ul>

      <nav aria-label="페이지 이동" className="flex flex-wrap items-center justify-center gap-1">
        <Button variant="outline" size="sm" disabled={page <= 1} onClick={() => goTo(page - 1)}>
          이전
        </Button>
        {pageNumbers(page, totalPages).map((n) => (
          <Button
            key={n}
            variant={n === page ? 'default' : 'outline'}
            size="sm"
            aria-current={n === page ? 'page' : undefined}
            onClick={() => goTo(n)}
          >
            {n}
          </Button>
        ))}
        <Button variant="outline" size="sm" disabled={page >= totalPages} onClick={() => goTo(page + 1)}>
          다음
        </Button>
      </nav>
    </div>
  )
}
