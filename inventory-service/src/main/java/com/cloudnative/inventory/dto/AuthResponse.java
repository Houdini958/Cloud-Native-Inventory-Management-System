package com.cloudnative.inventory.dto;

public record AuthResponse(String token, long expiresAtEpochSeconds) {
}
