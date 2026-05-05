package com.example.orderhub.service;

import com.example.orderhub.dto.OrderItemRequest;
import com.example.orderhub.dto.OrderRequest;
import com.example.orderhub.entity.*;
import com.example.orderhub.enums.IntegrationStatus;
import com.example.orderhub.enums.OrderStatus;
import com.example.orderhub.exception.BusinessException;
import com.example.orderhub.exception.ResourceNotFoundException;
import com.example.orderhub.integration.BillingIntegrationService;
import com.example.orderhub.repository.ClientRepository;
import com.example.orderhub.repository.OrderRepository;
import com.example.orderhub.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final BillingIntegrationService billingIntegrationService;

    public OrderService(OrderRepository orderRepository,
                        ClientRepository clientRepository,
                        ProductRepository productRepository,
                        BillingIntegrationService billingIntegrationService) {
        this.orderRepository = orderRepository;
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.billingIntegrationService = billingIntegrationService;
    }

    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
    }

    public List<Order> findByClientId(Long clientId) {
        return orderRepository.findByClientId(clientId);
    }

    @Transactional
    public Order create(OrderRequest request) {
        Client client = clientRepository.findById(request.clientId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente não encontrado"));

        Order order = Order.builder()
                .client(client)
                .status(OrderStatus.PENDING)
                .integrationStatus(IntegrationStatus.NOT_SENT)
                .totalAmount(BigDecimal.ZERO)
                .build();

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado"));

            if (product.getStock() < itemRequest.quantity()) {
                throw new BusinessException("Estoque insuficiente para o produto: " + product.getName());
            }

            BigDecimal subtotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity()));
            total = total.add(subtotal);

            product.setStock(product.getStock() - itemRequest.quantity());
            productRepository.save(product);

            OrderItem item = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.quantity())
                    .unitPrice(product.getPrice())
                    .subtotal(subtotal)
                    .build();

            items.add(item);
        }

        order.setItems(items);
        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    public Order updateStatus(Long id, OrderStatus status) {
        Order order = findById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public String sendToBilling(Long id) {
        Order order = findById(id);
        String response = billingIntegrationService.sendOrderToBilling(id, order.getTotalAmount().doubleValue());
        if (response.toLowerCase().contains("sucesso")) {
            order.setIntegrationStatus(IntegrationStatus.SENT);
            order.setStatus(OrderStatus.BILLED);
        } else {
            order.setIntegrationStatus(IntegrationStatus.ERROR);
        }
        orderRepository.save(order);
        return response;
    }

    public String getDeliveryStatus(Long id) {
        Order order = findById(id);
        return "Status atual do pedido " + order.getId() + ": " + order.getStatus();
    }
}
