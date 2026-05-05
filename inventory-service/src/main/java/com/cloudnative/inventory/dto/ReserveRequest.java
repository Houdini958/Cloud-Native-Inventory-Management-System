package com.cloudnative.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ReserveRequest(
        @NotBlank(message = "sku is required") String sku,
        @Min(value = 1, message = "quantity must be at least 1") int quantity
) {
}
