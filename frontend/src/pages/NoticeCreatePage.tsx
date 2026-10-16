import { useNavigate } from 'react-router'
import NoticeForm from '@/components/NoticeForm'
import { sendJson } from '@/lib/api'
import type { NoticeDetail } from '@/lib/notices'

/** 공지 등록 화면(/admin/notices/new). 등록하면 새 공지의 상세로 이동한다 */
export default function NoticeCreatePage() {
  const navigate = useNavigate()
  return (
    <NoticeForm
      heading="새 공지"
      submitLabel="등록"
      onSubmit={async (values) => {
        const created = await sendJson<NoticeDetail>('POST', '/api/notices', values)
        navigate(`/notices/${created!.id}`, { replace: true })
      }}
    />
  )
}
