import { useEffect, useRef, useState } from 'react';

const FONTS = ['Padrão', 'Serif', 'Monospace'];
const SIZES = ['12', '14', '16', '18', '24', '32'];

export default function TextEdit() {
  const editorRef = useRef(null);
  const [fontFamily, setFontFamily] = useState('Padrão');
  const [fontSize, setFontSize] = useState('16');
  const [wordCount, setWordCount] = useState(0);
  const [charCount, setCharCount] = useState(0);

  useEffect(() => {
    if (editorRef.current && !editorRef.current.innerHTML) {
      editorRef.current.innerHTML = '<p>Comece a digitar...</p>';
    }
  }, []);

  function execCmd(cmd, value = null) {
    editorRef.current?.focus();
    document.execCommand(cmd, false, value);
  }

  function handleInput() {
    const text = editorRef.current?.innerText || '';
    setCharCount(text.length);
    setWordCount(text.trim() ? text.trim().split(/\s+/).length : 0);
  }

  function applyFont(f) {
    setFontFamily(f);
    const fontMap = { 'Padrão': 'inherit', 'Serif': 'Georgia, serif', 'Monospace': 'monospace' };
    if (editorRef.current) editorRef.current.style.fontFamily = fontMap[f];
  }

  function applySize(s) {
    setFontSize(s);
    execCmd('fontSize', '7');
    // Patch font size via selection  
    const sel = window.getSelection();
    if (sel && sel.rangeCount > 0) {
      const range = sel.getRangeAt(0);
      const container = range.commonAncestorContainer;
      const el = container.nodeType === 3 ? container.parentElement : container;
      const fonts = editorRef.current?.querySelectorAll('font[size="7"]');
      fonts?.forEach((font) => { font.removeAttribute('size'); font.style.fontSize = s + 'px'; });
    }
  }

  return (
    <div className="textedit-layout">
      {/* Toolbar */}
      <div className="textedit-toolbar">
        <div className="te-btn-group">
          <button className="te-btn" onClick={() => execCmd('bold')} title="Negrito"><strong>B</strong></button>
          <button className="te-btn" onClick={() => execCmd('italic')} title="Itálico"><em>I</em></button>
          <button className="te-btn" onClick={() => execCmd('underline')} title="Sublinhado"><u>U</u></button>
          <button className="te-btn" onClick={() => execCmd('strikeThrough')} title="Tachado"><s>S</s></button>
        </div>
        <div className="te-sep"></div>
        <div className="te-btn-group">
          <button className="te-btn" onClick={() => execCmd('justifyLeft')} title="Esquerda">⬤◻◻</button>
          <button className="te-btn" onClick={() => execCmd('justifyCenter')} title="Centro">◻⬤◻</button>
          <button className="te-btn" onClick={() => execCmd('justifyRight')} title="Direita">◻◻⬤</button>
        </div>
        <div className="te-sep"></div>
        <div className="te-btn-group">
          <button className="te-btn" onClick={() => execCmd('insertUnorderedList')} title="Lista">• Lista</button>
          <button className="te-btn" onClick={() => execCmd('insertOrderedList')} title="Numerada">1. Lista</button>
        </div>
        <div className="te-sep"></div>
        <select
          className="te-select"
          value={fontFamily}
          onChange={(e) => applyFont(e.target.value)}
          title="Fonte"
        >
          {FONTS.map((f) => <option key={f} value={f}>{f}</option>)}
        </select>
        <select
          className="te-select te-size-select"
          value={fontSize}
          onChange={(e) => applySize(e.target.value)}
          title="Tamanho"
        >
          {SIZES.map((s) => <option key={s} value={s}>{s}px</option>)}
        </select>
        <div className="te-sep"></div>
        <button className="te-btn te-color-btn" title="Cor do texto">
          <input
            type="color"
            style={{ opacity: 0, position: 'absolute', width: '100%', height: '100%', cursor: 'pointer' }}
            onChange={(e) => execCmd('foreColor', e.target.value)}
          />
          🎨
        </button>
        <button
          className="te-btn"
          onClick={() => {
            if (window.confirm('Limpar documento?')) {
              if (editorRef.current) editorRef.current.innerHTML = '';
              setWordCount(0); setCharCount(0);
            }
          }}
          title="Limpar"
        >🗑</button>
      </div>

      {/* Editor */}
      <div
        ref={editorRef}
        className="textedit-editor"
        contentEditable
        suppressContentEditableWarning
        onInput={handleInput}
        style={{ fontSize: fontSize + 'px' }}
      />

      {/* Status bar */}
      <div className="textedit-statusbar">
        <span>{wordCount} palavras</span>
        <span>·</span>
        <span>{charCount} caracteres</span>
      </div>
    </div>
  );
}
