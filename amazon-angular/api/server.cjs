const express = require('express');
const { addToCart, getCartItems, clearCart, initDb } = require('./db.cjs');

const app = express();
const port = Number(process.env.PORT || 4001);

app.use(express.json());

function roundCurrency(value) {
  return Math.round((value + Number.EPSILON) * 100) / 100;
}

const navigation = ['Início', 'Ofertas do Dia', 'Mais Vendidos', 'Eletrônicos', 'Livros', 'Moda'];

const featuredMetrics = [
  { value: '12x', label: 'sem juros em itens selecionados' },
  { value: '24h', label: 'de ofertas relâmpago no ar' },
  { value: 'Prime', label: 'frete rápido em milhares de produtos' }
];

const products = [
  {
    id: 1,
    name: 'Notebook Gamer RGB Nitro X',
    category: 'Performance extrema para jogos e criação',
    rating: 5,
    reviews: 128,
    originalPrice: 3999.9,
    discountPrice: 2899.9,
    badge: 'Prime',
    accent: 'linear-gradient(135deg, #253b80, #ff9900)',
    section: 'deals'
  },
  {
    id: 2,
    name: 'Fone Bluetooth com cancelamento híbrido',
    category: 'Som imersivo e bateria de 30 horas',
    rating: 4,
    reviews: 89,
    originalPrice: 499.9,
    discountPrice: 249.9,
    badge: 'Prime',
    accent: 'linear-gradient(135deg, #111827, #38bdf8)',
    section: 'deals'
  },
  {
    id: 3,
    name: 'Smartwatch Pulse Pro AMOLED',
    category: 'Treinos, saúde e notificações no pulso',
    rating: 5,
    reviews: 256,
    originalPrice: 1299.9,
    discountPrice: 799.9,
    badge: 'Prime',
    accent: 'linear-gradient(135deg, #1f2937, #22c55e)',
    section: 'deals'
  },
  {
    id: 4,
    name: 'Tênis Urban Runner Unissex',
    category: 'Conforto leve para rotina e corrida',
    rating: 4,
    reviews: 312,
    originalPrice: 459.9,
    discountPrice: 299.9,
    accent: 'linear-gradient(135deg, #7c2d12, #fb7185)',
    section: 'deals'
  },
  {
    id: 5,
    name: 'Caixa de som portátil BassGo 360',
    category: 'Graves intensos com proteção contra água',
    rating: 5,
    reviews: 445,
    originalPrice: 289.9,
    discountPrice: 179.9,
    badge: 'Prime',
    accent: 'linear-gradient(135deg, #0f172a, #a855f7)',
    section: 'best-sellers'
  },
  {
    id: 6,
    name: 'Smartphone Vision 5G 256GB',
    category: 'Tela OLED, câmera tripla e recarga rápida',
    rating: 4,
    reviews: 789,
    originalPrice: 3199.9,
    discountPrice: 2199.9,
    accent: 'linear-gradient(135deg, #1d4ed8, #60a5fa)',
    section: 'best-sellers'
  },
  {
    id: 7,
    name: 'Aromatizador Smart Home',
    category: 'Automação residencial com app integrado',
    rating: 4,
    reviews: 167,
    originalPrice: 189.9,
    discountPrice: 119.9,
    badge: 'Prime',
    accent: 'linear-gradient(135deg, #164e63, #2dd4bf)',
    section: 'best-sellers'
  },
  {
    id: 8,
    name: 'Jogo de cama SoftCloud Queen',
    category: 'Toque acetinado e acabamento premium',
    rating: 5,
    reviews: 234,
    originalPrice: 349.9,
    discountPrice: 199.9,
    accent: 'linear-gradient(135deg, #881337, #fda4af)',
    section: 'best-sellers'
  },
  {
    id: 9,
    name: 'Conjunto de talheres Titanium 24 peças',
    category: 'Mesa posta com brilho inox premium',
    rating: 4,
    reviews: 98,
    originalPrice: 289.9,
    discountPrice: 189.9,
    accent: 'linear-gradient(135deg, #334155, #cbd5e1)',
    section: 'continue-shopping'
  },
  {
    id: 10,
    name: 'Caneta digital Stylus Precision',
    category: 'Escrita precisa para tablet e desenho',
    rating: 5,
    reviews: 56,
    originalPrice: 169.9,
    discountPrice: 99.9,
    badge: 'Prime',
    accent: 'linear-gradient(135deg, #4c1d95, #c084fc)',
    section: 'continue-shopping'
  },
  {
    id: 11,
    name: 'Cafeteira Programável Barista+',
    category: 'Agende o café e mantenha aquecido',
    rating: 4,
    reviews: 134,
    originalPrice: 449.9,
    discountPrice: 299.9,
    accent: 'linear-gradient(135deg, #422006, #f59e0b)',
    section: 'continue-shopping'
  },
  {
    id: 12,
    name: 'Perfume Signature Reserve',
    category: 'Notas amadeiradas e longa duração',
    rating: 4,
    reviews: 211,
    originalPrice: 299.9,
    discountPrice: 189.9,
    accent: 'linear-gradient(135deg, #111827, #f97316)',
    section: 'continue-shopping'
  }
];

app.get('/api/health', (_req, res) => {
  res.json({ ok: true });
});

app.get('/api/storefront', (_req, res) => {
  res.json({
    navigation,
    featuredMetrics,
    products,
    dealDeadline: new Date(Date.now() + 24 * 60 * 60 * 1000).toISOString()
  });
});

app.get('/api/cart', async (_req, res) => {
  const cartProductIds = await getCartItems();
  const items = cartProductIds
    .map((productId) => products.find((product) => product.id === productId))
    .filter(Boolean);

  res.json({
    items,
    count: items.length,
    total: roundCurrency(items.reduce((sum, item) => sum + item.discountPrice, 0))
  });
});

app.post('/api/cart/items', async (req, res) => {
  const productId = Number(req.body && req.body.productId);
  const productExists = products.some((product) => product.id === productId);

  if (!productExists) {
    res.status(404).json({ message: 'Produto não encontrado.' });
    return;
  }

  await addToCart(productId);
  const cartProductIds = await getCartItems();
  const items = cartProductIds
    .map((productId) => products.find((product) => product.id === productId))
    .filter(Boolean);

  res.status(201).json({
    items,
    count: items.length,
    total: roundCurrency(items.reduce((sum, item) => sum + item.discountPrice, 0))
  });
});

app.delete('/api/cart', async (_req, res) => {
  await clearCart();
  
  res.json({
    items: [],
    count: 0,
    total: 0
  });
});

app.listen(port, async () => {
  await initDb();
  console.log(`Dev API listening on http://localhost:${port}`);
});
