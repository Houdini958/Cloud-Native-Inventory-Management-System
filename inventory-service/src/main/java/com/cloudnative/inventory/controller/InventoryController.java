package com.cloudnative.inventory.controller;

import com.cloudnative.inventory.dto.InventoryResponse;
import com.cloudnative.inventory.dto.ReserveRequest;
import com.cloudnative.inventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{sku}")
    public InventoryResponse getInventory(@PathVariable String sku) {
        return inventoryService.getInventory(sku);
    }

    @PostMapping("/reserve")
    public ResponseEntity<Map<String, Object>> reserve(@Valid @RequestBody ReserveRequest request) {
        boolean reserved = inventoryService.reserve(request.sku(), request.quantity());
        if (!reserved) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "FAILED",
                    "message", "Insufficient stock for " + request.sku()
            ));
        }
        return ResponseEntity.ok(Map.of(
                "status", "SUCCESS",
                "message", "Stock reserved",
                "sku", request.sku(),
                "quantity", request.quantity()
        ));
    }

    @PostMapping("/restock")
    public InventoryResponse restock(@Valid @RequestBody ReserveRequest request) {
        return inventoryService.restock(request.sku(), request.quantity());
    }
}
