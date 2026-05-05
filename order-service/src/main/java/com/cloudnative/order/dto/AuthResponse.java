package com.cloudnative.order.dto;

public record AuthResponse(String token, long expiresAtEpochSeconds) {
}
