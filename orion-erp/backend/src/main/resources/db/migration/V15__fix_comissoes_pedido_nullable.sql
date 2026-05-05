-- V15: Relaxar constraints NOT NULL em comissoes para pedido_venda_id
ALTER TABLE comissoes ALTER COLUMN pedido_venda_id DROP NOT NULL;
