import { useState } from 'react'
import Sidebar from './components/Sidebar'
import ChatPage from './pages/ChatPage'
import CodeGenPage from './pages/CodeGenPage'
import AnalysisPage from './pages/AnalysisPage'
import DocsPage from './pages/DocsPage'

const PAGES = {
  chat: ChatPage,
  codegen: CodeGenPage,
  analysis: AnalysisPage,
  docs: DocsPage,
}

export default function App() {
  const [activePage, setActivePage] = useState('chat')
  const PageComponent = PAGES[activePage]

  return (
    <div style={{ display: 'flex', height: '100vh', overflow: 'hidden' }}>
      <Sidebar activePage={activePage} onNavigate={setActivePage} />
      <main style={{ flex: 1, overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
        <PageComponent />
      </main>
    </div>
  )
}
