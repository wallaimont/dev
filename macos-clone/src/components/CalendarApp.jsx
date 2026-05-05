import { useState } from 'react';

const MONTHS = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho','Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'];
const DAYS_SHORT = ['Dom','Seg','Ter','Qua','Qui','Sex','Sáb'];

const EVENTS = {
  '2026-05-05': 'Reunião de equipe',
  '2026-05-12': 'Sprint review',
  '2026-05-20': 'Deadline do projeto',
  '2026-05-28': 'Deploy de produção',
};

function CalendarApp() {
  const today = new Date();
  const [year, setYear] = useState(today.getFullYear());
  const [month, setMonth] = useState(today.getMonth());
  const [selected, setSelected] = useState(null);

  function prevMonth() {
    if (month === 0) { setMonth(11); setYear(y => y - 1); }
    else setMonth(m => m - 1);
  }

  function nextMonth() {
    if (month === 11) { setMonth(0); setYear(y => y + 1); }
    else setMonth(m => m + 1);
  }

  const firstDay = new Date(year, month, 1).getDay();
  const daysInMonth = new Date(year, month + 1, 0).getDate();
  const cells = [];

  for (let i = 0; i < firstDay; i++) cells.push(null);
  for (let d = 1; d <= daysInMonth; d++) cells.push(d);

  const isToday = (d) => d && d === today.getDate() && month === today.getMonth() && year === today.getFullYear();
  const key = (d) => `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
  const hasEvent = (d) => d && EVENTS[key(d)];

  return (
    <div className="cal-wrap">
      <div className="cal-header">
        <button className="cal-nav" onClick={prevMonth}>‹</button>
        <span className="cal-title">{MONTHS[month]} {year}</span>
        <button className="cal-nav" onClick={nextMonth}>›</button>
      </div>
      <div className="cal-days-header">
        {DAYS_SHORT.map(d => <span key={d}>{d}</span>)}
      </div>
      <div className="cal-grid">
        {cells.map((d, i) => (
          <button
            key={i}
            className={`cal-cell ${d ? '' : 'empty'} ${isToday(d) ? 'today' : ''} ${selected === d ? 'selected' : ''} ${hasEvent(d) ? 'has-event' : ''}`}
            onClick={() => d && setSelected(d)}
            disabled={!d}
          >
            {d || ''}
            {hasEvent(d) && <span className="event-dot" />}
          </button>
        ))}
      </div>
      {selected && EVENTS[key(selected)] && (
        <div className="cal-event-info">
          <strong>{String(selected).padStart(2,'0')}/{String(month+1).padStart(2,'0')}/{year}</strong>
          <span>{EVENTS[key(selected)]}</span>
        </div>
      )}
    </div>
  );
}

export default CalendarApp;
