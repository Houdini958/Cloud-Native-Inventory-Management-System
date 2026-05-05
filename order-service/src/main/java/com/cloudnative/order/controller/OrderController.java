package com.cloudnative.order.controller;

import com.cloudnative.order.dto.CreateOrderRequest;
import com.cloudnative.order.dto.OrderResponse;
import com.cloudnative.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request, HttpServletRequest httpServletRequest) {
        return orderService.createOrder(request, httpServletRequest.getHeader("Authorization"));
    }
}
