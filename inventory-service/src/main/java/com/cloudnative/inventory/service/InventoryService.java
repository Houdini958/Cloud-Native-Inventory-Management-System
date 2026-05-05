package com.cloudnative.inventory.service;

import com.cloudnative.inventory.dto.InventoryResponse;
import com.cloudnative.inventory.model.InventoryItem;
import com.cloudnative.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryResponse getInventory(String sku) {
        int quantity = inventoryRepository.findBySku(sku)
                .map(InventoryItem::getAvailableQuantity)
                .orElse(0);
        return new InventoryResponse(sku, quantity);
    }

    public boolean reserve(String sku, int quantity) {
        synchronized (this) {
            InventoryItem item = inventoryRepository.findBySku(sku).orElse(new InventoryItem(sku, 0));
            int existing = item.getAvailableQuantity();
            if (existing < quantity) {
                return false;
            }
            item.setAvailableQuantity(existing - quantity);
            inventoryRepository.save(item);
            return true;
        }
    }

    public InventoryResponse restock(String sku, int quantity) {
        InventoryItem item = inventoryRepository.findBySku(sku).orElse(new InventoryItem(sku, 0));
        item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
        InventoryItem updated = inventoryRepository.save(item);
        return new InventoryResponse(updated.getSku(), updated.getAvailableQuantity());
    }
}
