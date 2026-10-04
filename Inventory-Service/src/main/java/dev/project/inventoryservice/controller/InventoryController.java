package dev.project.inventoryservice.controller;

import dev.project.inventoryservice.dto.InventoryResponse;
import dev.project.inventoryservice.dto.StockRequest;
import dev.project.inventoryservice.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryResponse> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getStock(productId));
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addStock(@Valid @RequestBody StockRequest request) {
        inventoryService.addStock(request.productId(), request.quantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveStock(@Valid @RequestBody StockRequest request) {
        inventoryService.reserveStock(request.productId(), request.quantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirmReservation(@Valid @RequestBody StockRequest request) {
        inventoryService.confirmReservation(request.productId(), request.quantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/release")
    public ResponseEntity<Void> releaseReservation(@Valid @RequestBody StockRequest request) {
        inventoryService.releaseReservation(request.productId(), request.quantity());
        return ResponseEntity.ok().build();
    }
}
