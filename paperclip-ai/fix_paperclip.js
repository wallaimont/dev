const { Client } = require('C:/Users/wallace/AppData/Local/npm-cache/_npx/43414d9b790239bb/node_modules/pg');
const c = new Client({ host: '127.0.0.1', port: 54329, user: 'paperclip', password: 'paperclip', database: 'paperclip' });

c.connect().then(async () => {
  // Verificar e ocultar duplicatas para stranded_issue_recovery
  const dups1 = await c.query(
    `SELECT id FROM (
      SELECT id, ROW_NUMBER() OVER (PARTITION BY company_id, origin_kind, origin_id ORDER BY created_at) as rn
      FROM issues
      WHERE origin_kind='stranded_issue_recovery' AND origin_id IS NOT NULL AND hidden_at IS NULL AND status NOT IN ('done','cancelled')
    ) t WHERE rn > 1`
  );
  console.log('Duplicatas stranded_issue_recovery:', dups1.rows.length);
  if (dups1.rows.length > 0) {
    const ids = dups1.rows.map(r => r.id);
    const upd = await c.query('UPDATE issues SET hidden_at = NOW() WHERE id = ANY($1::uuid[])', [ids]);
    console.log('Ocultadas:', upd.rowCount);
  }

  // Verificar e ocultar duplicatas para issue_productivity_review
  const dups2 = await c.query(
    `SELECT id FROM (
      SELECT id, ROW_NUMBER() OVER (PARTITION BY company_id, origin_kind, origin_id ORDER BY created_at) as rn
      FROM issues
      WHERE origin_kind='issue_productivity_review' AND origin_id IS NOT NULL AND hidden_at IS NULL AND status NOT IN ('done','cancelled')
    ) t WHERE rn > 1`
  );
  console.log('Duplicatas issue_productivity_review:', dups2.rows.length);
  if (dups2.rows.length > 0) {
    const ids = dups2.rows.map(r => r.id);
    const upd = await c.query('UPDATE issues SET hidden_at = NOW() WHERE id = ANY($1::uuid[])', [ids]);
    console.log('Ocultadas:', upd.rowCount);
  }

  await c.end();
  console.log('Concluido.');
}).catch(e => { console.error('ERRO:', e.message); process.exit(1); });
