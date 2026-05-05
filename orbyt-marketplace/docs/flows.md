# Fluxos Principais

## 1. Jornada de Compra (Buyer)

1. Buyer acessa catalogo em `GET /v1/catalog/products`
2. Buyer adiciona item ao carrinho em `POST /v1/cart/items`
3. Buyer consulta preview em `POST /v1/checkout/preview`
4. Buyer fecha pedido em `POST /v1/checkout`
5. Sistema gera `OrderGroup` + `Order` por seller
6. Evento `order.created` vai para outbox
7. Buyer gera PIX em `POST /v1/payments/pix/charges`
8. Webhook confirma pagamento em `POST /v1/payments/pix/webhook`
9. Evento `payment.approved` e publicado
10. Seller prepara envio e atualiza shipment

## 2. Jornada do Seller

1. Seller autentica em `POST /v1/auth/login`
2. Seller cria e gerencia produtos em `/v1/catalog/products`
3. Seller consulta pedidos em `GET /v1/orders/seller`
4. Seller cria shipment em `POST /v1/shipping/shipments`
5. Seller atualiza rastreio em `POST /v1/shipping/shipments/{id}/tracking`

## 3. Jornada de Suporte e Pos-venda

1. Buyer abre ticket em `POST /v1/support/tickets`
2. Time adiciona mensagens no ticket
3. Disputa pode ser aberta para pedido
4. Notificacoes de status sao enviadas em `/v1/notifications`

## 4. Fluxo de Promocao

1. Admin cria cupom em `POST /v1/coupons`
2. Front valida cupom em `GET /v1/coupons/validate`
3. No checkout, cupom e aplicado e registrado em `coupon_usages`

## 5. Fluxo de Antifraude

1. Checkout chama analise de risco
2. Sistema consulta blacklist de email/ip/dispositivo
3. Calcula score e define decisao: `APPROVED`, `REVIEW`, `REJECTED`
4. Armazena resultado em `fraud_analyses`
