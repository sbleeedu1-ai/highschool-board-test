import { Navigate, Outlet, useLocation } from 'react-router'
import { useAuth } from '@/lib/auth'
import { loginPath } from '@/lib/redirect'

/** 관리자 화면 앞에 두는 문지기. 로그인하지 않았으면 로그인 화면으로 보내고, 로그인 뒤 원래 화면으로 돌아오게 한다 */
export default function RequireAdmin() {
  const { me } = useAuth()
  const location = useLocation()

  if (me === undefined) {
    return <p className="py-12 text-center text-muted-foreground">불러오는 중입니다</p>
  }
  if (!me.authenticated) {
    return <Navigate to={loginPath(location.pathname + location.search)} replace />
  }
  return <Outlet />
}
