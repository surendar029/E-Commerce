package dev.project.inventoryservice.service;

import dev.project.inventoryservice.dto.CreateInventoryRequest;
import dev.project.inventoryservice.dto.InventoryResponse;
import dev.project.inventoryservice.entity.Inventory;
import dev.project.inventoryservice.exception.InventoryNotFoundException;
import dev.project.inventoryservice.repository.InventoryRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public InventoryResponse getInventoryByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException("Inventory not found for productId: " + productId));
        return mapToResponse(inventory);
    }

    public InventoryResponse createInventory(CreateInventoryRequest request) {
        if (inventoryRepository.existsByProductId(request.productId())) {
            throw new IllegalStateException("Inventory already exists for product ID: " + request.productId());
        }

        Inventory inventory = new Inventory(
                request.productId(),
                request.initialQuantity(),
                0
        );

        return mapToResponse(inventoryRepository.save(inventory));
    }

    public InventoryResponse addStock(Long productId, Integer quantity) {
        Inventory inventory=inventoryRepository.findByProductId(productId).
                orElseThrow(()->
                        new InventoryNotFoundException(("Inventory not found for product ID: " + productId)));
        inventory.setAvailableQuantity(inventory.getAvailableQuantity()+quantity);
        return mapToResponse(inventoryRepository.save(inventory));
    }

    public InventoryResponse mapToResponse(Inventory inventory) {
        return new InventoryResponse(
                inventory.getProductId(),
                inventory.getAvailableQuantity(),
                inventory.getReservedQuantity(),
                inventory.getAvailableQuantity() > 0
        );
    }
}
