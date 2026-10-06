package dev.project.orderservice.client;

import dev.project.orderservice.dto.InventoryDetails;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/v1/inventory/{productId}")
    InventoryDetails getInventory(@PathVariable Long productId);
}
