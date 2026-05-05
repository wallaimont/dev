import { useEffect } from 'react';

function Notifications({ notifications, onDismiss }) {
  return (
    <div className="notif-stack">
      {notifications.map((n) => (
        <Toast key={n.id} notif={n} onDismiss={onDismiss} />
      ))}
    </div>
  );
}

function Toast({ notif, onDismiss }) {
  useEffect(() => {
    const t = setTimeout(() => onDismiss(notif.id), 4000);
    return () => clearTimeout(t);
  }, [notif.id, onDismiss]);

  return (
    <div className="notif-toast">
      <span className="notif-icon">{notif.icon || '🔔'}</span>
      <div className="notif-body">
        <p className="notif-title">{notif.title}</p>
        {notif.msg && <p className="notif-msg">{notif.msg}</p>}
      </div>
      <button className="notif-close" onClick={() => onDismiss(notif.id)}>✕</button>
    </div>
  );
}

export default Notifications;
