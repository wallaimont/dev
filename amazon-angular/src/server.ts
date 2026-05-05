import {
  AngularNodeAppEngine,
  createNodeRequestHandler,
  isMainModule,
  writeResponseToNodeResponse,
} from '@angular/ssr/node';
import express from 'express';
import { join } from 'node:path';
import { createStorefrontResponse, products } from './app/store-data';
import { addToCart, getCartItems, clearCart } from './app/db.server';

const browserDistFolder = join(import.meta.dirname, '../browser');

const app = express();
const angularApp = new AngularNodeAppEngine({
  allowedHosts: ['localhost', '127.0.0.1'],
});

app.use(express.json());

function roundCurrency(value: number): number {
  return Math.round((value + Number.EPSILON) * 100) / 100;
}

app.get('/api/storefront', (_req, res) => {
  res.json(createStorefrontResponse());
});

app.get('/api/cart', async (_req, res) => {
  const cartProductIds = await getCartItems();
  const items = cartProductIds
    .map((productId) => products.find((product) => product.id === productId))
    .filter((product): product is (typeof products)[number] => Boolean(product));

  res.json({
    items,
    count: items.length,
    total: roundCurrency(items.reduce((sum, item) => sum + item.discountPrice, 0)),
  });
});

app.post('/api/cart/items', async (req, res) => {
  const productId = Number(req.body?.productId);
  const product = products.find((item) => item.id === productId);

  if (!product) {
    res.status(404).json({ message: 'Produto não encontrado.' });
    return;
  }

  await addToCart(productId);
  const cartProductIds = await getCartItems();
  const items = cartProductIds
    .map((productId) => products.find((product) => product.id === productId))
    .filter((product): product is (typeof products)[number] => Boolean(product));

  res.status(201).json({
    items,
    count: items.length,
    total: roundCurrency(items.reduce((sum, item) => sum + item.discountPrice, 0)),
  });
});

app.delete('/api/cart', async (_req, res) => {
  await clearCart();
  res.json({
    items: [],
    count: 0,
    total: 0,
  });
});

/**
 * Serve static files from /browser
 */
app.use(
  express.static(browserDistFolder, {
    maxAge: '1y',
    index: false,
    redirect: false,
  }),
);

/**
 * Handle all other requests by rendering the Angular application.
 */
app.use((req, res, next) => {
  angularApp
    .handle(req)
    .then((response) =>
      response ? writeResponseToNodeResponse(response, res) : next(),
    )
    .catch(next);
});

/**
 * Start the server if this module is the main entry point, or it is ran via PM2.
 * The server listens on the port defined by the `PORT` environment variable, or defaults to 4000.
 */
if (isMainModule(import.meta.url) || process.env['pm_id']) {
  const port = process.env['PORT'] || 4000;
  app.listen(port, (error) => {
    if (error) {
      throw error;
    }

    console.log(`Node Express server listening on http://localhost:${port}`);
  });
}

/**
 * Request handler used by the Angular CLI (for dev-server and during build) or Firebase Cloud Functions.
 */
export const reqHandler = createNodeRequestHandler(app);
