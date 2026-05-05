package com.cloudnative.inventory.config;

import com.cloudnative.inventory.model.InventoryItem;
import com.cloudnative.inventory.repository.InventoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InventorySeedData {

    @Bean
    CommandLineRunner seedInventory(InventoryRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                repository.save(new InventoryItem("SKU-100", 120));
                repository.save(new InventoryItem("SKU-200", 80));
                repository.save(new InventoryItem("SKU-300", 45));
            }
        };
    }
}
