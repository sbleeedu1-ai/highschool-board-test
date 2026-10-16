import { Route, Routes } from 'react-router'
import Layout from '@/components/Layout'
import RequireAdmin from '@/components/RequireAdmin'
import { AuthProvider } from '@/lib/auth'
import LoginPage from '@/pages/LoginPage'
import NoticeDetailPage from '@/pages/NoticeDetailPage'
import NoticeListPage from '@/pages/NoticeListPage'

// 화면 주소 목록. 이후 티켓에서 여기에 길을 덧붙인다.
function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<NoticeListPage />} />
          <Route path="/notices/:id" element={<NoticeDetailPage />} />
          <Route path="/admin/login" element={<LoginPage />} />
          {/* 관리자 화면: 로그인하지 않았으면 로그인 화면으로 보낸다. 등록(05)·수정(06) 화면을 이 안에 덧붙인다 */}
          <Route path="/admin/notices" element={<RequireAdmin />}>
            <Route path="*" element={<p className="py-12 text-center text-muted-foreground">준비 중인 화면입니다</p>} />
          </Route>
        </Route>
      </Routes>
    </AuthProvider>
  )
}

export default App
