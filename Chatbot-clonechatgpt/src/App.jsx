import { useState, useRef, useEffect, useCallback } from 'react';
import { getSession, logout } from './auth.js';
import { FREE_MODELS } from './constants/models.js';

// Hooks
import { useConversations } from './hooks/useConversations.js';
import { useApiKeys }       from './hooks/useApiKeys.js';
import { useSkills }        from './hooks/useSkills.js';
import { useChat }          from './hooks/useChat.js';

// Components
import AuthScreen     from './components/AuthScreen.jsx';
import Sidebar        from './components/Sidebar.jsx';
import ChatHeader     from './components/ChatHeader.jsx';
import ChatMessages   from './components/ChatMessages.jsx';
import ChatInput      from './components/ChatInput.jsx';
import ModelDropdown  from './components/ModelDropdown.jsx';
import SettingsModal  from './components/SettingsModal.jsx';

export default function App() {
  // Auth
  const [user, setUser] = useState(null);

  useEffect(() => {
    const s = getSession();
    if (s) setUser(s);
  }, []);

  const handleLogout = useCallback(async () => {
    await logout();
    setUser(null);
  }, []);

  // UI state
  const [selectedModel, setSelectedModel] = useState(FREE_MODELS[0]);
  const [showSettings,  setShowSettings]  = useState(false);
  const [showModelMenu, setShowModelMenu] = useState(false);
  const [modelMenuPos,  setModelMenuPos]  = useState({ top: 0, left: 0 });
  const [showSidebar,   setShowSidebar]   = useState(() =>
    typeof window !== 'undefined' ? window.innerWidth >= 768 : true
  );
  const [convSearch,  setConvSearch]  = useState('');
  const [modelSearch, setModelSearch] = useState('');
  const [webSearch,   setWebSearch]   = useState(false);

  // Refs
  const inputRef         = useRef(null);
  const modelBtnRef      = useRef(null);
  const modelMenuRef     = useRef(null);
  const modelDropdownRef = useRef(null);
  const extrasRef        = useRef(null);
  const fileInputRef     = useRef(null);
  const bottomRef        = useRef(null);

  // Close model menu on outside click
  useEffect(() => {
    if (!showModelMenu) return;
    const handler = (e) => {
      if (
        modelMenuRef.current?.contains(e.target) ||
        modelDropdownRef.current?.contains(e.target)
      ) return;
      setShowModelMenu(false);
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, [showModelMenu]);

  // Domain hooks
  const {
    conversations,
    setConversations,
    activeId,
    setActiveId,
    persist,
    persistDebounced,
    newConversation,
    deleteConversation,
    commitRename,
    exportConversation,
  } = useConversations(user);

  const { apiKeys, saveKeys, getKey } = useApiKeys();

  const {
    customSkills,
    selectedSkills,
    setSelectedSkills,
    newSkillName,
    setNewSkillName,
    newSkillPrompt,
    setNewSkillPrompt,
    addCustomSkill,
    removeCustomSkill,
    getSkillMeta,
  } = useSkills(user);

  const {
    loading,
    input,
    setInput,
    attachment,
    setAttachment,
    actualModel,
    copiedId,
    send,
    stopGeneration,
    copyMsg,
    handleFile,
    captureScreen,
  } = useChat({
    activeId,
    setActiveId,
    conversations,
    setConversations,
    persist,
    selectedModel,
    getKey,
    selectedSkills,
    getSkillMeta,
    webSearch,
    inputRef,
    bottomRef,
  });

  // Active conversation
  const activeConv = conversations.find(c => c.id === activeId);
  const messages   = activeConv?.messages ?? [];

  // User meta
  const userInitial = (user?.name || user?.email || 'U').charAt(0).toUpperCase();
  const firstName   = (user?.name || user?.email || 'usuario').split(' ')[0].split('@')[0];

  // Model menu toggle
  const handleToggleModelMenu = useCallback(() => {
    if (!showModelMenu && modelBtnRef.current) {
      const r = modelBtnRef.current.getBoundingClientRect();
      setModelMenuPos({ top: r.bottom + 8, left: r.left });
    }
    setModelSearch('');
    setShowModelMenu(v => !v);
  }, [showModelMenu]);

  // Auth gate
  if (!user) return <AuthScreen onAuth={setUser} />;

  return (
    <div className="relative flex h-screen overflow-hidden bg-[#07110f] text-zinc-100">
      {/* Background blobs */}
      <div className="pointer-events-none absolute inset-0 overflow-hidden">
        <div className="absolute -left-24 top-[-12rem] h-80 w-80 rounded-full bg-emerald-500/18 blur-3xl" />
        <div className="absolute right-[-10rem] top-20 h-[28rem] w-[28rem] rounded-full bg-cyan-400/10 blur-3xl" />
        <div className="absolute bottom-[-10rem] left-1/3 h-72 w-72 rounded-full bg-teal-300/10 blur-3xl" />
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_top,rgba(255,255,255,0.06),transparent_32%),linear-gradient(180deg,rgba(6,16,14,0.72),rgba(6,10,12,0.98))]" />
      </div>

      {/* Mobile overlay */}
      {showSidebar && (
        <div
          className="fixed inset-0 z-20 bg-black/60 md:hidden"
          onClick={() => setShowSidebar(false)}
        />
      )}

      <Sidebar
        user={user}
        conversations={conversations}
        activeId={activeId}
        showSidebar={showSidebar}
        setShowSidebar={setShowSidebar}
        convSearch={convSearch}
        setConvSearch={setConvSearch}
        onSelectConv={setActiveId}
        onNewConversation={newConversation}
        onDeleteConversation={deleteConversation}
        onCommitRename={commitRename}
        onLogout={handleLogout}
        onOpenSettings={() => setShowSettings(true)}
      />

      <main className="relative z-10 flex min-w-0 flex-1 flex-col bg-transparent">
        <div ref={modelMenuRef}>
          <ChatHeader
            onToggleSidebar={() => setShowSidebar(v => !v)}
            selectedModel={selectedModel}
            modelBtnRef={modelBtnRef}
            showModelMenu={showModelMenu}
            onToggleModelMenu={handleToggleModelMenu}
            actualModel={actualModel}
            hasMessages={messages.length > 0}
            onExport={() => exportConversation(activeId)}
          />
        </div>

        <ChatMessages
          messages={messages}
          loading={loading}
          selectedModel={selectedModel}
          copiedId={copiedId}
          onCopyMsg={copyMsg}
          onSend={send}
          firstName={firstName}
          userInitial={userInitial}
          bottomRef={bottomRef}
        />

        <ChatInput
          input={input}
          setInput={setInput}
          loading={loading}
          attachment={attachment}
          setAttachment={setAttachment}
          webSearch={webSearch}
          setWebSearch={setWebSearch}
          selectedSkills={selectedSkills}
          setSelectedSkills={setSelectedSkills}
          customSkills={customSkills}
          newSkillName={newSkillName}
          setNewSkillName={setNewSkillName}
          newSkillPrompt={newSkillPrompt}
          setNewSkillPrompt={setNewSkillPrompt}
          addCustomSkill={addCustomSkill}
          removeCustomSkill={removeCustomSkill}
          getSkillMeta={getSkillMeta}
          onSend={send}
          onStop={stopGeneration}
          onHandleFile={handleFile}
          onCaptureScreen={captureScreen}
          fileInputRef={fileInputRef}
          extrasRef={extrasRef}
          inputRef={inputRef}
        />
      </main>

      <ModelDropdown
        show={showModelMenu}
        menuPos={modelMenuPos}
        dropdownRef={modelDropdownRef}
        selectedModel={selectedModel}
        modelSearch={modelSearch}
        setModelSearch={setModelSearch}
        onSelect={(m) => { setSelectedModel(m); setShowModelMenu(false); setModelSearch(''); }}
      />

      {showSettings && (
        <SettingsModal
          apiKeys={apiKeys}
          onSave={saveKeys}
          onClose={() => setShowSettings(false)}
        />
      )}
    </div>
  );
}
