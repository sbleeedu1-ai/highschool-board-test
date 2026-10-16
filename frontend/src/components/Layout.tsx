import { useEffect, useState } from 'react'
import { Link, Outlet, useNavigate } from 'react-router'
import { Button } from '@/components/ui/button'
import { useAuth } from '@/lib/auth'
import { fetchVersion, versionLabel } from '@/lib/version'

/** 모든 화면이 함께 쓰는 틀: 위쪽 제목, 가운데 화면 내용, 아래쪽 버전 문구 */
export default function Layout() {
  // undefined: 아직 묻는 중, null: 받지 못함
  const [version, setVersion] = useState<string | null | undefined>(undefined)

  useEffect(() => {
    fetchVersion().then(setVersion)
  }, [])

  return (
    <div className="flex min-h-svh flex-col">
      <header className="mx-auto flex w-full max-w-3xl items-center justify-between gap-4 px-4 pt-8">
        <h1 className="text-2xl font-bold">
          <Link to="/">공지 게시판</Link>
        </h1>
        <LogoutButton />
      </header>
      <main className="mx-auto w-full max-w-3xl flex-1 px-4 py-6">
        <Outlet />
      </main>
      <footer className="px-4 py-3 text-center text-xs text-muted-foreground">
        {version !== undefined && versionLabel(version)}
      </footer>
    </div>
  )
}

/** 로그인한 관리자에게만 보이는 로그아웃 버튼. 방문자에게는 아무것도 보이지 않는다 */
function LogoutButton() {
  const { me, logout } = useAuth()
  const navigate = useNavigate()
  const [error, setError] = useState(false)

  if (!me?.authenticated) return null

  async function onClick() {
    setError(false)
    // 관리자 화면에서 로그아웃하면 문지기(RequireAdmin)가 로그인 화면으로 보내 버리므로, 첫 화면으로 먼저 옮긴다
    navigate('/')
    try {
      await logout()
    } catch {
      setError(true)
    }
  }

  return (
    <div className="flex items-center gap-2">
      {error && <span className="text-sm text-destructive">다시 시도해 주세요</span>}
      <Button variant="outline" size="sm" onClick={onClick}>
        로그아웃
      </Button>
    </div>
  )
}
