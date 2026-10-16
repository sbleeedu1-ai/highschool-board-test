import { useEffect, useState } from 'react'
import { Link, Outlet } from 'react-router'
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
      <header className="mx-auto w-full max-w-3xl px-4 pt-8">
        <h1 className="text-2xl font-bold">
          <Link to="/">공지 게시판</Link>
        </h1>
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
