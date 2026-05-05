package com.cloudnative.order.dto;

public record OrderResponse(String orderId, String sku, int quantity, String status, String message) {
}
