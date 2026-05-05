package com.cloudnative.inventory.dto;

public record InventoryResponse(String sku, int availableQuantity) {
}
