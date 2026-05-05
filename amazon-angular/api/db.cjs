const fs = require('fs');
const path = require('path');

const storePath = path.join(process.cwd(), 'data', 'cart-store.json');

function ensureStoreFile() {
  const folder = path.dirname(storePath);
  if (!fs.existsSync(folder)) {
    fs.mkdirSync(folder, { recursive: true });
  }

  if (!fs.existsSync(storePath)) {
    fs.writeFileSync(storePath, JSON.stringify({ productIds: [] }, null, 2), 'utf-8');
  }
}

function loadStore() {
  ensureStoreFile();
  const content = fs.readFileSync(storePath, 'utf-8');
  const parsed = JSON.parse(content);
  return {
    productIds: Array.isArray(parsed.productIds) ? parsed.productIds : []
  };
}

function saveStore(store) {
  fs.writeFileSync(storePath, JSON.stringify(store, null, 2), 'utf-8');
}

async function initDb() {
  ensureStoreFile();
}

module.exports = {
  initDb,
  
  addToCart: async (productId) => {
    const store = loadStore();
    store.productIds.push(productId);
    saveStore(store);
  },
  
  getCartItems: async () => {
    return loadStore().productIds;
  },
  
  clearCart: async () => {
    saveStore({ productIds: [] });
  }
};
