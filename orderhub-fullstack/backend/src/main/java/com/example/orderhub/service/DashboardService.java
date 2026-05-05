package com.example.orderhub.service;

import com.example.orderhub.enums.OrderStatus;
import com.example.orderhub.repository.ClientRepository;
import com.example.orderhub.repository.OrderRepository;
import com.example.orderhub.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardService {

    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public DashboardService(ClientRepository clientRepository,
                            ProductRepository productRepository,
                            OrderRepository orderRepository) {
        this.clientRepository = clientRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    public Map<String, Object> getSummary() {
        Map<String, Object> summary = new HashMap<>();
        var orders = orderRepository.findAll();
        summary.put("clients", clientRepository.count());
        summary.put("products", productRepository.count());
        summary.put("orders", orders.size());
        summary.put("pendingOrders", orders.stream().filter(o -> o.getStatus() == OrderStatus.PENDING).count());
        summary.put("billedOrders", orders.stream().filter(o -> o.getStatus() == OrderStatus.BILLED).count());
        summary.put("deliveredOrders", orders.stream().filter(o -> o.getStatus() == OrderStatus.DELIVERED).count());
        return summary;
    }
}
