# Roadmap de Evolucao

## Status Atual

- Catalogo, carrinho, checkout, pedidos, CMS e suporte ja contam com camadas application dedicadas no backend.
- Frontend cobre buyer, seller e admin com rotas para wishlist, reviews, chat, perfil, produtos, cupons, CMS e antifraude.
- Documentacao de API atualizada para refletir endpoints reais de CMS e gestao de pedidos.

## Fase 1 (0-60 dias) - Fundacao e Go-Live

- Checkout completo com split por seller
- PIX e webhook de aprovacao
- Carrinho, pedidos, rastreio e suporte
- Backoffice seller e admin
- Observabilidade basica com Prometheus + Grafana

## Fase 2 (60-120 dias) - Escala Comercial

- Motor de frete com cotacao por carrier
- Recomendacoes e busca avancada
- Campanhas de promocao por segmento
- Automacao de atendimento com SLA inteligente
- Melhorias de antifraude com modelo ML

## Fase 3 (120-180 dias) - Internacionalizacao

- Multi-moeda (USD/EUR/BRL)
- Regras fiscais por pais/estado
- Catalogo multilanguage (pt-BR, en-US, es-ES)
- Onboarding global de sellers

## Fase 4 (180-360 dias) - Plataforma Expandida

- Separacao progressiva para microservicos
- Marketplace APIs para parceiros externos
- Programa de afiliados e creators
- Inteligencia de preco dinamico
- CDP para personalizacao omnicanal

## KPI Targets

- Conversao checkout: >= 2.8%
- Aprovação de pagamento: >= 93%
- SLA de entrega no prazo: >= 96%
- NPS Buyer: >= 70
- Latencia p95 backend: < 220ms
