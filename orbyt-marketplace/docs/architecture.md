# Arquitetura Orbyt Market

## Visao Executiva

Recomendacao: monolito modular com contratos internos e integracao assincrona.

## Estrutura Macro

```text
clients
|- web (Next.js)
|- mobile (Expo / React Native)

edge
|- nginx
|- future api gateway

platform-core
|- backend spring boot modular
   |- platform
   |- identity
   |- catalog
   |- cart
   |- order
   |- payment
   |- shipping
   |- engagement
   |- cms
   |- operations
```

## Decisao Tecnica

Escolha: monolito modular enterprise.

Motivos:
- acelera go-to-market
- simplifica transacoes distribuidas em checkout e pagamento
- reduz custo de operacao inicial
- permite extracao futura por contexto usando eventos, outbox e contratos

## Como Extrair Microservicos Depois

Ordem recomendada:

1. `notification-service`
2. `chat-service`
3. `payment-service`
4. `catalog-service`
5. `order-service`

## Estruturas

Backend:

```text
backend/src/main/java/com/orbyt/marketplace
|- shared
|- platform
|- identity
|- catalog
|- cart
|- order
|- payment
|- shipping
|- engagement
|- cms
|- operations
|- config
```

Frontend:

```text
frontend/app
|- (public)
|- buyer
|- seller
|- admin
|- super-admin
```

Mobile:

```text
mobile/app
|- (auth)
|- (tabs)
|- product
|- order
```
