package com.cloudnative.order.service;

import com.cloudnative.order.dto.CreateOrderRequest;
import com.cloudnative.order.dto.OrderResponse;
import com.cloudnative.order.model.OrderEntity;
import com.cloudnative.order.repository.OrderRepository;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Instant;
import java.util.Map;

@Service
public class OrderService {

    private final WebClient inventoryWebClient;
    private final OrderRepository orderRepository;

    public OrderService(WebClient inventoryWebClient, OrderRepository orderRepository) {
        this.inventoryWebClient = inventoryWebClient;
        this.orderRepository = orderRepository;
    }

    public OrderResponse createOrder(CreateOrderRequest request, String authorizationHeader) {
        if (orderRepository.existsByOrderId(request.orderId())) {
            return new OrderResponse(request.orderId(), request.sku(), request.quantity(), "REJECTED", "Duplicate orderId");
        }

        Map<String, Object> reserveResult = inventoryWebClient.post()
                .uri("/api/inventory/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizationHeader == null ? "" : authorizationHeader)
                .bodyValue(Map.of("sku", request.sku(), "quantity", request.quantity()))
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorReturn(Map.of("status", "FAILED", "message", "Inventory reservation failed"))
                .block();

        String status = String.valueOf(reserveResult.getOrDefault("status", "FAILED"));
        if (!"SUCCESS".equalsIgnoreCase(status)) {
            OrderResponse rejected = new OrderResponse(
                    request.orderId(),
                    request.sku(),
                    request.quantity(),
                    "REJECTED",
                    String.valueOf(reserveResult.getOrDefault("message", "Insufficient stock"))
            );
            orderRepository.save(new OrderEntity(
                    rejected.orderId(),
                    rejected.sku(),
                    rejected.quantity(),
                    rejected.status(),
                    rejected.message(),
                    Instant.now()
            ));
            return rejected;
        }

        OrderResponse confirmed = new OrderResponse(
                request.orderId(),
                request.sku(),
                request.quantity(),
                "CONFIRMED",
                "Order created and inventory reserved"
        );
        orderRepository.save(new OrderEntity(
                confirmed.orderId(),
                confirmed.sku(),
                confirmed.quantity(),
                confirmed.status(),
                confirmed.message(),
                Instant.now()
        ));
        return confirmed;
    }
}
