export async function fetchHistoryFromProxy(historyUrl, idToken) {
  const res = await fetch(historyUrl, {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${idToken}`,
    },
  });

  if (!res.ok) return null;

  const data = await res.json();
  if (!Array.isArray(data?.conversations)) return null;
  return data.conversations;
}

export async function pushHistoryToProxy(historyUrl, idToken, conversations) {
  await fetch(historyUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${idToken}`,
    },
    body: JSON.stringify({ conversations }),
  });
}
