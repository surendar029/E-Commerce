package dev.project.inventoryservice.service;

import dev.project.inventoryservice.dto.InventoryResponse;
import dev.project.inventoryservice.entity.InventoryEntity;
import dev.project.inventoryservice.exception.InsufficientStockException;
import dev.project.inventoryservice.exception.InventoryNotFoundException;
import dev.project.inventoryservice.exception.ReservationNotFoundException;
import dev.project.inventoryservice.repository.InventoryRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryService(InventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public void initializeStock(Long productId, Integer initialStock) {
        if (inventoryRepository.existsByProductId(productId)) {
            log.warn("Stock already initialized for Product ID: {}. Duplicate event ignored.", productId);
            return;
        }

        InventoryEntity entity = InventoryEntity.builder()
                .productId(productId)
                .availableQuantity(initialStock != null ? initialStock : 0)
                .build();

        inventoryRepository.save(entity);
        log.info("Stock initialized successfully for Product ID: {} with quantity: {}", productId, initialStock);
    }

    @Transactional
    public void deleteStock(Long productId) {
        inventoryRepository.deleteByProductId(productId);

        log.info("Inventory deleted for Product ID: {}", productId);
    }

    @Transactional
    public void addStock(Long productId, Integer quantity) {
        InventoryEntity inventory = getInventory(productId);

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        log.info("Added {} stock for Product ID: {}", quantity, productId);
    }

    @Transactional(readOnly = true)
    public InventoryResponse getStock(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(this::toResponse)
                .orElseThrow(()->new InventoryNotFoundException("Inventory not found for Product ID: " + productId));
    }

    @Transactional
    public void reserveStock(Long productId, Integer quantity) {
        InventoryEntity inventory = getInventory(productId);

        if (inventory.getAvailableQuantity() < quantity) {
            throw new InsufficientStockException("Insufficient stock for Product ID: " + productId);
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setReservedQuantity(inventory.getReservedQuantity() + quantity);

        log.info("Reserved {} units for Product ID: {}", quantity, productId);
    }

    @Transactional
    public void confirmReservation(Long productId, Integer quantity) {
        InventoryEntity inventory = getInventory(productId);

        if (inventory.getReservedQuantity() < quantity) {
            throw new ReservationNotFoundException("Reservation not found for Product ID: " + productId);
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);

        log.info("Confirmed reservation of {} units for Product ID: {}", quantity, productId);
    }

    @Transactional
    public void releaseReservation(Long productId, Integer quantity) {
        InventoryEntity inventory = getInventory(productId);

        if (inventory.getReservedQuantity() < quantity) {
            throw new ReservationNotFoundException("Reservation not found for Product ID: " + productId);
        }

        inventory.setReservedQuantity(inventory.getReservedQuantity() - quantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);

        log.info("Released {} reserved units for Product ID: {}", quantity, productId);
    }

    private InventoryEntity getInventory(Long productId) {
        return inventoryRepository.findByProductId(productId).orElseThrow(() ->
                new InventoryNotFoundException(
                        "Inventory not found for Product ID: " + productId));
    }

    private InventoryResponse toResponse(InventoryEntity inventory) {
        return InventoryResponse.builder()
                .productId(inventory.getProductId())
                .availableQuantity(inventory.getAvailableQuantity())
                .reservedQuantity(inventory.getReservedQuantity())
                .build();
    }

}
