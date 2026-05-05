import { useEffect, useMemo, useState } from 'react';
import MenuBar from './components/MenuBar';
import DesktopIcons from './components/DesktopIcons';
import Dock from './components/Dock';
import WindowFrame from './components/WindowFrame';
import Spotlight from './components/Spotlight';
import WindowTabs from './components/WindowTabs';
import AppSwitcher from './components/AppSwitcher';
import BootScreen from './components/BootScreen';
import Calculator from './components/Calculator';
import CalendarApp from './components/CalendarApp';
import MusicPlayer from './components/MusicPlayer';
import Settings from './components/Settings';
import TerminalApp from './components/TerminalApp';
import ContextMenu from './components/ContextMenu';
import ControlCenter from './components/ControlCenter';
import Notifications from './components/Notifications';
import FinderApp from './components/FinderApp';
import NotesApp from './components/NotesApp';
import TextEdit from './components/TextEdit';
import Photos from './components/Photos';
import SystemMonitor from './components/SystemMonitor';
import LockScreen from './components/LockScreen';
import { WALLPAPERS } from './components/Settings';

const STORAGE_THEME_KEY = 'novaos-theme';
const STORAGE_WINDOWS_KEY = 'novaos-windows';
const STORAGE_NOTE_KEY = 'novaos-note';
const STORAGE_BOOTED_KEY = 'novaos-booted';
const STORAGE_WALLPAPER_KEY = 'novaos-wallpaper';

const APPS = [
  { id: 'finderWindow',     title: 'Finder',           iconClass: 'icon-folder' },
  { id: 'notesWindow',      title: 'Notas',             iconClass: 'icon-note' },
  { id: 'terminalWindow',   title: 'Terminal',          iconClass: 'icon-terminal' },
  { id: 'browserWindow',    title: 'Navegador',         iconClass: 'icon-browser' },
  { id: 'calculatorWindow', title: 'Calculadora',       iconClass: 'icon-calc' },
  { id: 'calendarWindow',   title: 'Calendário',        iconClass: 'icon-calendar' },
  { id: 'musicWindow',      title: 'Música',            iconClass: 'icon-music' },
  { id: 'settingsWindow',   title: 'Configurações',     iconClass: 'icon-settings' },
  { id: 'texteditWindow',   title: 'TextEdit',          iconClass: 'icon-textedit' },
  { id: 'photosWindow',     title: 'Fotos',             iconClass: 'icon-photos' },
  { id: 'monitorWindow',    title: 'Monitor do Sistema',iconClass: 'icon-monitor' },
];

const INITIAL_WINDOWS = {
  finderWindow:     { id: 'finderWindow',     title: 'Finder',            isOpen: true,  left: 90,  top: 85,  width: 660, height: 440, z: 110 },
  notesWindow:      { id: 'notesWindow',      title: 'Notas',             isOpen: false, left: 220, top: 120, width: 580, height: 420, z: 108 },
  terminalWindow:   { id: 'terminalWindow',   title: 'Terminal',          isOpen: false, left: 360, top: 165, width: 600, height: 380, z: 107 },
  browserWindow:    { id: 'browserWindow',    title: 'Navegador',         isOpen: true,  left: 480, top: 95,  width: 560, height: 390, z: 109 },
  calculatorWindow: { id: 'calculatorWindow', title: 'Calculadora',       isOpen: false, left: 140, top: 100, width: 340, height: 530, z: 106 },
  calendarWindow:   { id: 'calendarWindow',   title: 'Calendário',        isOpen: false, left: 300, top: 110, width: 440, height: 460, z: 105 },
  musicWindow:      { id: 'musicWindow',      title: 'Música',            isOpen: false, left: 200, top: 130, width: 480, height: 520, z: 104 },
  settingsWindow:   { id: 'settingsWindow',   title: 'Configurações',     isOpen: false, left: 260, top: 110, width: 520, height: 440, z: 103 },
  texteditWindow:   { id: 'texteditWindow',   title: 'TextEdit',          isOpen: false, left: 180, top: 100, width: 600, height: 480, z: 102 },
  photosWindow:     { id: 'photosWindow',     title: 'Fotos',             isOpen: false, left: 160, top: 95,  width: 680, height: 480, z: 101 },
  monitorWindow:    { id: 'monitorWindow',    title: 'Monitor do Sistema',isOpen: false, left: 200, top: 110, width: 560, height: 440, z: 100 },
};

function loadTheme() {
  const value = localStorage.getItem(STORAGE_THEME_KEY);
  return value === 'dark' ? 'dark' : 'light';
}

function loadWindows() {
  const raw = localStorage.getItem(STORAGE_WINDOWS_KEY);
  if (!raw) return INITIAL_WINDOWS;
  try {
    const parsed = JSON.parse(raw);
    return { ...INITIAL_WINDOWS, ...parsed };
  } catch {
    return INITIAL_WINDOWS;
  }
}

function loadNote() {
  const content = localStorage.getItem(STORAGE_NOTE_KEY);
  if (typeof content === 'string') return content;
  return 'Checklist de hoje:\n- Revisar backlog\n- Fazer deploy\n- Atualizar documentacao';
}

function loadWallpaper() {
  return localStorage.getItem(STORAGE_WALLPAPER_KEY) || 'sonoma';
}

let notifCounter = 0;

function App() {
  const [booted, setBooted] = useState(() => !!localStorage.getItem(STORAGE_BOOTED_KEY));
  const [locked, setLocked] = useState(false);
  const [theme, setTheme] = useState(loadTheme);
  const [windowsState, setWindowsState] = useState(loadWindows);
  const [noteContent, setNoteContent] = useState(loadNote);
  const [wallpaper, setWallpaper] = useState(loadWallpaper);
  const [spotlightOpen, setSpotlightOpen] = useState(false);
  const [spotlightQuery, setSpotlightQuery] = useState('');
  const [switcherVisible, setSwitcherVisible] = useState(false);
  const [switcherOrder, setSwitcherOrder] = useState([]);
  const [switcherIndex, setSwitcherIndex] = useState(0);
  const [contextMenu, setContextMenu] = useState(null); // {x, y}
  const [controlCenterOpen, setControlCenterOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);

  const maxZ = useMemo(
    () => Math.max(...Object.values(windowsState).map((w) => w.z)),
    [windowsState]
  );

  const openWindowsOrdered = useMemo(
    () => Object.values(windowsState).filter((w) => w.isOpen).sort((a, b) => b.z - a.z),
    [windowsState]
  );

  const activeWindowId = openWindowsOrdered[0]?.id || null;
  const activeAppName = APPS.find((a) => a.id === activeWindowId)?.title || null;

  // Wallpaper CSS var
  const wallpaperCss = useMemo(() => {
    const found = WALLPAPERS.find((w) => w.id === wallpaper);
    return found ? found.css : WALLPAPERS[0].css;
  }, [wallpaper]);

  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem(STORAGE_THEME_KEY, theme);
  }, [theme]);

  useEffect(() => {
    localStorage.setItem(STORAGE_WINDOWS_KEY, JSON.stringify(windowsState));
  }, [windowsState]);

  useEffect(() => {
    localStorage.setItem(STORAGE_NOTE_KEY, noteContent);
  }, [noteContent]);

  useEffect(() => {
    localStorage.setItem(STORAGE_WALLPAPER_KEY, wallpaper);
  }, [wallpaper]);

  function addNotification(title, msg, icon) {
    const id = ++notifCounter;
    setNotifications((prev) => [...prev, { id, title, msg, icon }]);
  }

  function dismissNotification(id) {
    setNotifications((prev) => prev.filter((n) => n.id !== id));
  }

  function focusWindow(id) {
    setWindowsState((prev) => {
      const current = prev[id];
      if (!current) return prev;
      const currentMaxZ = Math.max(...Object.values(prev).map((w) => w.z));
      return { ...prev, [id]: { ...current, isOpen: true, z: currentMaxZ + 1 } };
    });
  }

  function closeSwitcher() {
    setSwitcherVisible(false);
    setSwitcherOrder([]);
    setSwitcherIndex(0);
  }

  useEffect(() => {
    function handleKeyDown(event) {
      const isSwitchShortcut = (event.altKey && event.key === 'Tab') || (event.ctrlKey && event.key === 'Tab');

      if (isSwitchShortcut) {
        event.preventDefault();
        const orderedOpen = Object.values(windowsState).filter((w) => w.isOpen).sort((a, b) => b.z - a.z);
        if (orderedOpen.length === 0) return;
        if (spotlightOpen) closeSpotlight();
        const ids = orderedOpen.map((w) => w.id);
        if (!switcherVisible) {
          const startIndex = ids.length === 1 ? 0 : event.shiftKey ? ids.length - 1 : 1;
          setSwitcherOrder(ids); setSwitcherIndex(startIndex); setSwitcherVisible(true);
          return;
        }
        setSwitcherIndex((prev) => {
          const length = switcherOrder.length || 1;
          return event.shiftKey ? (prev - 1 + length) % length : (prev + 1) % length;
        });
        return;
      }

      const isOpenShortcut =
        ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'k') ||
        ((event.ctrlKey || event.metaKey) && event.code === 'Space');

      if (isOpenShortcut) { event.preventDefault(); setSpotlightOpen(true); return; }

      if (event.key === 'Escape') {
        setSpotlightOpen(false); setSpotlightQuery(''); closeSwitcher();
        setContextMenu(null); setControlCenterOpen(false);
      }
    }

    function handleKeyUp(event) {
      if (!switcherVisible) return;
      if (event.key !== 'Alt' && event.key !== 'Control') return;
      event.preventDefault();
      const targetId = switcherOrder[switcherIndex];
      if (targetId) focusWindow(targetId);
      closeSwitcher();
    }

    window.addEventListener('keydown', handleKeyDown);
    window.addEventListener('keyup', handleKeyUp);
    return () => { window.removeEventListener('keydown', handleKeyDown); window.removeEventListener('keyup', handleKeyUp); };
  }, [windowsState, switcherVisible, switcherOrder, switcherIndex, spotlightOpen]);

  function toggleTheme() {
    setTheme((value) => (value === 'light' ? 'dark' : 'light'));
  }

  function patchWindow(id, patch) {
    setWindowsState((prev) => ({ ...prev, [id]: { ...prev[id], ...patch } }));
  }

  function openWindow(id) { focusWindow(id); }
  function closeWindow(id) { patchWindow(id, { isOpen: false }); }
  function minimizeWindow(id) { patchWindow(id, { isOpen: false }); }
  function bringToFront(id) { patchWindow(id, { z: maxZ + 1 }); }
  function moveWindow(id, position) { patchWindow(id, { ...position, maximized: false }); }
  function resizeWindow(id, size) { patchWindow(id, { ...size, maximized: false }); }

  function toggleAllWindows() {
    const hidden = Object.values(windowsState).filter((w) => !w.isOpen).length;
    const shouldOpenAll = hidden > 0;
    setWindowsState((prev) => {
      const next = { ...prev };
      Object.keys(next).forEach((id, index) => {
        next[id] = { ...next[id], isOpen: shouldOpenAll, z: shouldOpenAll ? maxZ + index + 1 : next[id].z };
      });
      return next;
    });
  }

  function toggleMaximize(id) {
    setWindowsState((prev) => {
      const current = prev[id];
      if (!current) return prev;
      if (!current.maximized) {
        return {
          ...prev, [id]: {
            ...current,
            previous: { left: current.left, top: current.top, width: current.width, height: current.height },
            left: 16, top: 52, width: window.innerWidth - 32, height: Math.max(280, window.innerHeight * 0.78),
            maximized: true, z: maxZ + 1,
          },
        };
      }
      const previous = current.previous || { left: 90, top: 85, width: 560, height: 360 };
      return { ...prev, [id]: { ...current, ...previous, maximized: false, z: maxZ + 1 } };
    });
  }

  function openSpotlight() { setSpotlightOpen(true); }
  function closeSpotlight() { setSpotlightOpen(false); setSpotlightQuery(''); }

  function handleSpotlightOpenApp(id) { openWindow(id); closeSpotlight(); }
  function focusWindowByTab(id) { openWindow(id); }

  function handleWallpaperChange(id) {
    setWallpaper(id);
    addNotification('Papel de Parede', `Tema "${WALLPAPERS.find(w=>w.id===id)?.label}" aplicado.`, '🖼');
  }

  function handleDesktopContextMenu(e) {
    // Only fire on the desktop background or windows-layer, not on windows/dock/menubar
    if (e.target.closest('.window-frame, .dock, .menu-bar, .context-menu, .cc-panel, .spotlight-backdrop')) return;
    e.preventDefault();
    setContextMenu({ x: e.clientX, y: e.clientY });
  }

  const spotlightResults = useMemo(() => {
    const query = spotlightQuery.trim().toLowerCase();
    if (!query) return APPS;
    return APPS.filter((app) => app.title.toLowerCase().includes(query));
  }, [spotlightQuery]);

  const switcherItems = useMemo(
    () =>
      switcherOrder
        .map((id) => APPS.find((app) => app.id === id) || { id, title: windowsState[id]?.title || id, iconClass: 'icon-browser' })
        .filter(Boolean),
    [switcherOrder, windowsState]
  );

  function handleBootDone() {
    localStorage.setItem(STORAGE_BOOTED_KEY, '1');
    setBooted(true);
    addNotification('Bem-vindo ao NovaOS', 'Sistema carregado com sucesso!', '✅');
  }

  function handleLock() {
    setLocked(true);
    addNotification('Tela bloqueada', 'Digite qualquer senha para desbloquear.', '🔒');
  }

  function handleUnlock() {
    setLocked(false);
  }

  if (!booted) return <BootScreen onDone={handleBootDone} />;

  return (
    <div
      className="app-shell"
      style={{ '--wallpaper': wallpaperCss }}
      onContextMenu={handleDesktopContextMenu}
    >
      <div className="wallpaper-glow glow-a"></div>
      <div className="wallpaper-glow glow-b"></div>

      <MenuBar
        theme={theme}
        onToggleTheme={toggleTheme}
        onOpenSpotlight={openSpotlight}
        onOpenControlCenter={() => setControlCenterOpen((v) => !v)}
        controlCenterOpen={controlCenterOpen}
        activeAppName={activeAppName}
        onLock={handleLock}
        onOpenSettings={() => openWindow('settingsWindow')}
      />

      {controlCenterOpen && (
        <ControlCenter theme={theme} onClose={() => setControlCenterOpen(false)} />
      )}

      <WindowTabs items={openWindowsOrdered} activeId={activeWindowId} onSelect={focusWindowByTab} />

      <DesktopIcons onOpen={openWindow} />

      <section className="windows-layer" id="windowsLayer">
        {/* Finder */}
        <WindowFrame id="finderWindow" title="Finder" state={windowsState.finderWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <FinderApp />
        </WindowFrame>

        {/* Notas */}
        <WindowFrame id="notesWindow" title="Notas" state={windowsState.notesWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <NotesApp />
        </WindowFrame>

        {/* Terminal */}
        <WindowFrame id="terminalWindow" title="Terminal" state={windowsState.terminalWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <TerminalApp />
        </WindowFrame>

        {/* Navegador */}
        <WindowFrame id="browserWindow" title="Navegador" state={windowsState.browserWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <div className="browser-view">
            <div className="browser-toolbar">
              <span className="dot"></span><span className="dot"></span><span className="dot"></span>
              <input type="text" value="https://novaos.local" readOnly aria-label="URL" />
            </div>
            <section className="browser-page">
              <h3>Bem-vindo ao NovaOS</h3>
              <p>Uma experiencia desktop inspirada no visual dos sistemas modernos.</p>
            </section>
          </div>
        </WindowFrame>

        {/* Calculadora */}
        <WindowFrame id="calculatorWindow" title="Calculadora" state={windowsState.calculatorWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <Calculator />
        </WindowFrame>

        {/* Calendário */}
        <WindowFrame id="calendarWindow" title="Calendário" state={windowsState.calendarWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <CalendarApp />
        </WindowFrame>

        {/* Música */}
        <WindowFrame id="musicWindow" title="Música" state={windowsState.musicWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <MusicPlayer />
        </WindowFrame>

        {/* Configurações */}
        <WindowFrame id="settingsWindow" title="Configurações" state={windowsState.settingsWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <Settings
            wallpaper={wallpaper}
            onWallpaperChange={handleWallpaperChange}
            theme={theme}
            onToggleTheme={toggleTheme}
          />
        </WindowFrame>

        {/* TextEdit */}
        <WindowFrame id="texteditWindow" title="TextEdit" state={windowsState.texteditWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <TextEdit />
        </WindowFrame>

        {/* Fotos */}
        <WindowFrame id="photosWindow" title="Fotos" state={windowsState.photosWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <Photos />
        </WindowFrame>

        {/* Monitor do Sistema */}
        <WindowFrame id="monitorWindow" title="Monitor do Sistema" state={windowsState.monitorWindow}
          onBringToFront={bringToFront} onClose={closeWindow} onMinimize={minimizeWindow}
          onToggleMaximize={toggleMaximize} onDrag={moveWindow} onResize={resizeWindow}>
          <SystemMonitor />
        </WindowFrame>
      </section>

      <Dock onOpen={openWindow} onToggleAll={toggleAllWindows} windowsState={windowsState} />

      <AppSwitcher isOpen={switcherVisible} items={switcherItems} activeIndex={switcherIndex} />

      <Spotlight
        isOpen={spotlightOpen}
        query={spotlightQuery}
        results={spotlightResults}
        onChangeQuery={setSpotlightQuery}
        onClose={closeSpotlight}
        onOpenApp={handleSpotlightOpenApp}
      />

      {contextMenu && (
        <ContextMenu
          x={contextMenu.x}
          y={contextMenu.y}
          onClose={() => setContextMenu(null)}
          onOpenApp={openWindow}
          onOpenSettings={() => openWindow('settingsWindow')}
          onNewNote={() => openWindow('notesWindow')}
        />
      )}

      <Notifications notifications={notifications} onDismiss={dismissNotification} />

      {locked && (
        <LockScreen onUnlock={handleUnlock} wallpaper={wallpaperCss} />
      )}
    </div>
  );
}

export default App;
