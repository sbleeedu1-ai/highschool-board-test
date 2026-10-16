import { useState, type FormEvent } from 'react'
import { useNavigate, useSearchParams } from 'react-router'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { ApiError, sendJson } from '@/lib/api'
import { useAuth } from '@/lib/auth'

/** 관리자 로그인 화면(/admin/login). 방문자 화면에는 이 주소로 가는 링크가 없다 */
export default function LoginPage() {
  const [searchParams] = useSearchParams()
  const navigate = useNavigate()
  const { refresh } = useAuth()
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [submitting, setSubmitting] = useState(false)

  const expired = searchParams.get('expired') === '1'
  // 같은 사이트 안의 주소로만 돌아간다(다른 사이트로 보내는 주소는 무시)
  const from = searchParams.get('from')
  const returnTo = from && from.startsWith('/') && !from.startsWith('//') ? from : '/'

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setSubmitting(true)
    setError(null)
    try {
      await sendJson('POST', '/api/auth/login', { username, password })
      await refresh()
      navigate(returnTo, { replace: true })
    } catch (err) {
      if (err instanceof ApiError && err.status === 401) {
        setError('아이디 또는 비밀번호가 올바르지 않습니다')
      } else if (err instanceof ApiError && err.status === 403) {
        // CSRF 토큰이 없거나 낡았다. 새 토큰을 받아 두고 다시 누르게 한다
        await refresh()
        setError('다시 시도해 주세요')
      } else {
        setError('잠시 후 다시 시도해 주세요')
      }
    } finally {
      setSubmitting(false)
    }
  }

  return (
    <form onSubmit={onSubmit} className="mx-auto flex max-w-sm flex-col gap-4 py-6">
      <h2 className="text-xl font-semibold">관리자 로그인</h2>
      {expired && (
        <p className="rounded-md border px-3 py-2 text-sm">
          로그인이 만료되었습니다. 다시 로그인해 주세요. 작성 중이던 내용은 저장되지 않았을 수 있습니다.
        </p>
      )}
      <div className="flex flex-col gap-2">
        <Label htmlFor="username">아이디</Label>
        <Input id="username" autoComplete="username" value={username} onChange={(e) => setUsername(e.target.value)} required />
      </div>
      <div className="flex flex-col gap-2">
        <Label htmlFor="password">비밀번호</Label>
        <Input
          id="password"
          type="password"
          autoComplete="current-password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
      </div>
      {error && (
        <p role="alert" className="text-sm text-destructive">
          {error}
        </p>
      )}
      <Button type="submit" disabled={submitting}>
        로그인
      </Button>
    </form>
  )
}
