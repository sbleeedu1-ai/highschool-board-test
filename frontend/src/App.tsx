import { Route, Routes } from 'react-router'
import Layout from '@/components/Layout'
import NoticeListPage from '@/pages/NoticeListPage'

// 화면 주소 목록. 이후 티켓에서 여기에 길을 덧붙인다.
function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<NoticeListPage />} />
      </Route>
    </Routes>
  )
}

export default App
