import { useEffect, useState } from 'react'
import { fetchVersion, versionLabel } from '@/lib/version'

function App() {
  // undefined: 아직 묻는 중, null: 받지 못함
  const [version, setVersion] = useState<string | null | undefined>(undefined)

  useEffect(() => {
    fetchVersion().then(setVersion)
  }, [])

  return (
    <div className="flex min-h-svh flex-col">
      <main className="mx-auto w-full max-w-3xl flex-1 px-4 py-8">
        <h1 className="text-2xl font-bold">공지 게시판</h1>
      </main>
      <footer className="px-4 py-3 text-center text-xs text-muted-foreground">
        {version !== undefined && versionLabel(version)}
      </footer>
    </div>
  )
}

export default App
