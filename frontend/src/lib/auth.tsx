import { createContext, useCallback, useContext, useEffect, useState, type ReactNode } from 'react'
import { sendJson } from '@/lib/api'

type Me = { authenticated: boolean; username: string | null }

type Auth = {
  /** undefined: 아직 확인 중 */
  me: Me | undefined
  /** 로그인 상태를 서버에 다시 묻는다. CSRF 쿠키도 이때 새로 받는다 */
  refresh: () => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<Auth | null>(null)

const LOGGED_OUT: Me = { authenticated: false, username: null }

/** 화면 전체에 로그인 상태를 나눠 준다. 첫 화면에서 한 번 /api/auth/me를 부른다 */
export function AuthProvider({ children }: { children: ReactNode }) {
  const [me, setMe] = useState<Me | undefined>(undefined)

  const refresh = useCallback(async () => {
    try {
      const res = await fetch('/api/auth/me')
      setMe(res.ok ? await res.json() : LOGGED_OUT)
    } catch {
      setMe(LOGGED_OUT)
    }
  }, [])

  const logout = useCallback(async () => {
    await sendJson('POST', '/api/auth/logout')
    await refresh()
  }, [refresh])

  useEffect(() => {
    refresh()
  }, [refresh])

  return <AuthContext.Provider value={{ me, refresh, logout }}>{children}</AuthContext.Provider>
}

export function useAuth(): Auth {
  const auth = useContext(AuthContext)
  if (!auth) throw new Error('AuthProvider 안에서만 쓸 수 있습니다')
  return auth
}
