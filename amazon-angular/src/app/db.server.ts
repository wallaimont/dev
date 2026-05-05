import { existsSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs';
import { dirname, join } from 'node:path';

interface CartStore {
  productIds: number[];
}

const storePath = join(process.cwd(), 'data', 'cart-store.json');

function ensureStoreFile(): void {
  const folder = dirname(storePath);
  if (!existsSync(folder)) {
    mkdirSync(folder, { recursive: true });
  }

  if (!existsSync(storePath)) {
    writeFileSync(storePath, JSON.stringify({ productIds: [] }, null, 2), 'utf-8');
  }
}

function loadStore(): CartStore {
  ensureStoreFile();
  const content = readFileSync(storePath, 'utf-8');
  const parsed = JSON.parse(content) as Partial<CartStore>;
  return { productIds: Array.isArray(parsed.productIds) ? parsed.productIds : [] };
}

function saveStore(store: CartStore): void {
  writeFileSync(storePath, JSON.stringify(store, null, 2), 'utf-8');
}

export async function addToCart(productId: number): Promise<void> {
  const store = loadStore();
  store.productIds.push(productId);
  saveStore(store);
}

export async function getCartItems(): Promise<number[]> {
  return loadStore().productIds;
}

export async function clearCart(): Promise<void> {
  saveStore({ productIds: [] });
}
